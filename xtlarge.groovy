#!/usr/bin/env groovy
@GrabResolver(name = 'jitpack', root = 'https://jitpack.io')
@GrabResolver(name = 'central', root = 'http://central.maven.org/maven2/')
@Grab('com.github.nao20010128nao:CryptorageDecentralized:4aab97f')
@Grab('com.github.nao20010128nao:HttpServerJava:4582a9d30f')
import com.nao20010128nao.Cryptorage.ExposedKt as UtilsKt
import net.freeutils.httpserver.HTTPServer

def home=new File(System.env["HOME"])
def videos=new File(home,"videos")

def mem=UtilsKt.newMemoryFileSource()

for(def num=0;num<=52;num++){
    println "Doing for $num"
    def dir = UtilsKt.asFileSource(new URL("https://gitlab.com/yaju1145148101919/data-$num/raw/master"))
    //def dir = UtilsKt.asFileSource(new File("/tmp/data-4"))
    def crypt = UtilsKt.withV1Encryption(dir, System.env.PASSWORD)

    def files = crypt.list().findAll{crypt.size(it)>1000000000}
    dir.open("manifest").copyTo mem.put("a")
    //println(files.size())
    //println(crypt.list())
    if(!files)continue
    crypt.list().each{
        println("Copying $it")
        def source = crypt.open(it)
        new File(videos,it).withOutputStream{os->
            source.copyTo os
        }
    }
}
println("OK")
