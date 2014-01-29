#!/bin/sh

#sudo cp target/datagrinder.war /opt/local/share/java/tomcat6/webapps/
sudo cp target/datagrinder.war /Users/ntrive/local/apache-tomcat-7.0.50/webapps/
echo "[deploy.sh] Copied datagrinder.war to Tomcat webapps directory."

scp target/datagrinder.war collections-sandbox:/home/ntrive/
echo "[deploy.sh] Pushed war to collections-sandbox ntrive's home directory."
