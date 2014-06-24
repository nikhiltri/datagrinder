Datagrinder
===========
Datagrinder is a set of services for deriving different versions of
media files. You can create smaller-sized images, convert images to
text, etc.

## Prerequisites

1) Tomcat
2) ImageMagick
3) JMagick

## Installation

1) Add the following options to your Tomcat config, usually to
   `TOMCAT_HOME/bin/setenv.sh`, or `/etc/sysconfig/tomcat7`:

    # Add options for JMagick to load properly
    JAVA_OPTS="$JAVA_OPTS -Djmagick.systemclassloader=no"

2) Copy
   [src/main/webapp/WEB-INF/classes/{datagrinder.properties.example](datagrinder.properties.example)
   to [src/main/webapp/WEB-INF/classes/{datagrinder.properties](datagrinder.properties),
   particularly if your Fedora repo requires authentication.

3) From the command-line, run:

    mvn install

4) Copy `target/datagrinder.war` to your Tomcat webapps directory. For
   quicker deployment in the future, modify `deploy.sh` to use a
   utility script.
