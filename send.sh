#!/bin/bash
set -ex
cd ~/videos/${2:-bkup}
echo "$REPO/data-${1:-1}.git"
git init
git add .
git commit -am.
git remote add origin "$REPO/data-${1:-1}.git"
git push -u origin master
cd
rm -rf ~/videos/${2:-bkup}
