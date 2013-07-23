#!/bin/sh

projectname=`sed -n 's,.*name="app_name">\(.*\)<.*,\1,p' app/res/values/strings.xml`

echo "Setting up build for $projectname"
echo ""

android update project -p app/ --subprojects --name "$projectname"
