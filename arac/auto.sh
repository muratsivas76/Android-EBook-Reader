#!/bin/sh

./clean.sh

JAVA_HOME=/usr/java/jdk1.8.0_261
PATH=$PATH:$JAVA_HOME/bin:

java -version

java -cp cls: ToMDK test.txt SansSerif,0,48 ISO-8859-9 8 1600F no

java -cp cls: CountPages
echo " "

rm -f aa*.txt

echo " "
echo "Total Page Count: "
cat cnt.txt

echo " "
echo " "

java -cp cls: ChangeValues

rm -fv ../assets/texts/*

cp -fv infos/r00000.dat ../assets/texts/
cp -fv customInfo.dat ../assets/texts/info.txt

mv -fv mdk/* ../assets/texts

java -cp cls/: DesignPackage realme
mkdir ../src/net/murat/realme
mkdir ../src/net/murat/realme/ebook
mv -fv ../src/net/murat/ebook/* ../src/net/murat/realme/ebook
rm -rfv ../src/net/murat/ebook

java -cp cls: LogoGen realme

./clean.sh

cd ..

./control.sh

./xderleme.sh

rm -fv *.keystore
rm -rfv build/classes/net
rm -fv assets/texts/*.*

cd arac

mkdir ../src/net/murat/ebook
mv -fv ../src/net/murat/realme/ebook/* ../src/net/murat/ebook
rm -rfv ../src/net/murat/realme/ebook
rm -rfv ../src/net/murat/realme

java -cp cls/: ReDesignPackage realme
echo " "
echo "Done."

echo " "
echo "cd .. and run this firstly: "
echo "/usr/java/android-sdk/tools/emulator -wipe-data -avd MuratAVD &"

echo " "
echo "Run this when the emulator is ready:"
echo "/usr/java/android-sdk/tools/adb -e install dist/EBook_realme.apk"

echo " "
echo "Uninstall command:"
echo "/usr/java/android-sdk/tools/adb -e uninstall net.murat.realme.ebook"
echo " "
echo "For memory info:"
echo "/usr/java/android-sdk/tools/adb shell dumpsys meminfo net.murat.realme.ebook"
echo " "

java -cp cls: CompareFiles
echo "Page count: "
cat cnt.txt

ls ../dist/EBook*
