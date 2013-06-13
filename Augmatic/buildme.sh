#!/bin/bash
#echo "<resources><string name=\"gitname\">`git describe --tags`</string></resources>" > res/values/gitinfo.xml
./init_gitinfo.sh

#ant debug && ant installd

#adb install Augmatic.apk

../gradlew build && ../gradlew installDebug

