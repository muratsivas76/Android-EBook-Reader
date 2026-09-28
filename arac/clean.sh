#!/bin/sh
cd ..

./clean.sh

echo "ls obj"
ls obj

echo "ls bin"
ls bin

cd arac

rm -rfv hedef
rm -rf xyz
mkdir xyz
rm -f aformat.utf
rm -f aa*.txt
rm -rfv mdk/*
rm -rfv ndk/*
rm -rfv rnd/*
rm -fv zauto.sh
rm -fv dest.txt
rm -fv zsil.html
rm -fv file.html
rm -rfv islem
rm -fv ztv.txt
rm -fv yedek_auto.sh

clear

echo "java ControlXChanges"
java ControlXChanges
