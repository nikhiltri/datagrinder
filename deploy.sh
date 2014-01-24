#!/bin/sh

sudo cp target/datagrinder.war /opt/local/share/java/tomcat6/webapps/
echo "[deploy.sh] Copied datagrinder.war to Tomcat webapps directory.\n"

scp -r * collections-sandbox:/home/ntrive/datagrinder/
echo "[deploy.sh] Pushed code to collections-sandbox.\n"
