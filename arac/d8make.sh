#!/bin/bash

# --- CLEAN PREVIOUS BUILDS ---
rm -fv bin/*.apk
rm -fv bin/classes.dex
rm -fv bin/*.signed.*
rm -fv bin/*.unsigned.*
rm -fv src/net/murat/ebook/R.java
rm -rf obj/net

# --- GENERATE R.JAVA AND RESOURCE PACKAGES (API 33) ---
aapt package -v -f -m -S res -J src -M AndroidManifest.xml -I /usr/java/android/platforms/android-33/android.jar

# --- COMPILE JAVA SOURCE FILES (WITH EXPLICIT PARAMETER NAMES FOR D8) ---
javac --release 17 \
-sourcepath "src" \
-cp "/usr/java/android/platforms/android-33/android.jar":"obj" \
-g -parameters -proc:none -nowarn -O -Xmaxwarns 1 \
-d "obj" "src/net/murat/ebook/EBook.java"

# --- DEEP DISCOVERY FOR ALL .CLASS FILES IN SUBDIRECTORIES ---
# This looks into all nested packages like net/murat/ebook and net/murat/sayfas
# Adding the android.jar as a library to make d8 absolutely silent
d8 --debug --lib /usr/java/android/platforms/android-33/android.jar --output bin $(find obj -name "*.class")

# Clean temporary class list file after dexing
rm -fv bin/class_list.txt

# --- BUILD UNSIGNED APK ---
aapt package -v -f -M "AndroidManifest.xml" -A "assets" -S "res" -I "/usr/java/android/platforms/android-33/android.jar" -F "bin/EBook.unsigned.apk" "bin"

# --- SIGN THE APK WITH KEYSTORE ---
jarsigner -keystore EBook.keystore -storepass 5EmrE432 -keypass 5EmrE432 -signedjar "bin/EBook.signed.apk" "bin/EBook.unsigned.apk" EBook.keystore

# --- ALIGN THE SIGNED APK FOR MAXIMUM PERFORMANCE ---
zipalign -v -f 4 "bin/EBook.signed.apk" "bin/EBook.apk"

# --- FINAL POST-BUILD CLEANUP ---
rm -fv *.keystore.old
rm -fv bin/classes.dex
rm -fv bin/*.signed.*
rm -fv bin/*.unsigned.*
rm -fv src/net/murat/ebook/R.java
rm -rf obj/net

# --- SHOW FINAL REPORT ---
echo "--- BUILD COMPLETED SUCCESSFULY ---"
ls bin
