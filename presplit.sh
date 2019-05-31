#!/bin/bash
set -ex
cd ~/videos/
find . -size +1000000000c -print | while read i ; do
  cnt=$(( $(wc -c "$i" | awk '{ print $1 }') / 1000000000 + 1 ))
  split -a 3 -n "$cnt" -d --additional-suffix=.split "$i" "${i}."
  rm "$i"
done
