#!/bin/sh

sudo cp target/datagrinder.war /opt/local/share/java/tomcat6/webapps/
echo "[deploy.sh] Copied datagrinder.war to Tomcat webapps directory.\n"
