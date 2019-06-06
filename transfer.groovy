#!/usr/bin/env groovy
import com.google.common.io.Files as GFiles
@GrabResolver(name = 'jitpack', root = 'https://jitpack.io')
@GrabResolver(name = 'central', root = 'http://central.maven.org/maven2/')
@Grab('com.github.nao20010128nao:Cryptorage:9c4ada0997')
import com.nao20010128nao.Cryptorage.ExposedKt as UtilsKt

import java.nio.file.Files as JFiles

final maxScore=1024*1024*1024

def home=new File(System.env["HOME"])
def videos=new File(home,"videos")

def transferTo=new File(videos,args.length>=1?args[0]:"bkup")
transferTo.mkdirs()

def crypto=UtilsKt.withV1Encryption(UtilsKt.asFileSource(transferTo), System.env.PASSWORD)
crypto.meta("split_size", "${20000000}")

def toPut = videos.listFiles().findAll {
    "$it".matches(~'.+\\.(?:web[mp]|m(?:p4|kv|4[av])|(?:pn|jp)g|og[gva]|zip|7z|t(?:ar(?:.gz)|gz))(?:\\.[0-9]+\\.split)?$')
}.sort{a,b->
  return b.length()<=>a.length()
}
def ended = new File(videos,'ok')
ended.mkdirs()

if(!toPut){
    System.exit(1)
    return
}

def currentScore=maxScore-(crypto.list().inject(0){r,a->r+crypto.size(a)})
println "currentScore: ${currentScore}"
def finalFiles=[]

toPut.each{a->
    final entryScore=a.length()
    if(currentScore>=entryScore){
        // pass
        currentScore-=entryScore
        finalFiles+=a
    }
}
println "remainingScore: ${currentScore}"

println(finalFiles)

if(!finalFiles){
    System.exit(1)
    return
}

finalFiles.each{
    println "Copying $it as $it.name"
    def copied=false
    if(!crypto.has(it.name)||true){
        copied=true
        GFiles.asByteSource(it).copyTo crypto.put(it.name)
    }
}
println 'Committing'
crypto.commit()

finalFiles.each{
    def dest = new File(ended, it.name)
    dest.delete()
    JFiles.move it.toPath(), dest.toPath()
}

crypto.close()
