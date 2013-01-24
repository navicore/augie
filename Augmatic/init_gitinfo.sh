#!/bin/bash
echo "<resources><string name=\"gitname\">`git describe --tags`</string></resources>" > res/values/gitinfo.xml

