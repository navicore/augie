#!/bin/bash
echo "<resources><string name=\"gitname\">`git describe --tags`</string></resources>" > src/main/res/values/gitinfo.xml

