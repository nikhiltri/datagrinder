Datagrinder
===========

## Prerequisites

1) Tomcat
2) ImageMagick
3) JMagick

## Installation

1) In `pom.xml`, modify the property `jmagick.path.jar` to point to
   your system's JMagick JAR file.

2) Add the following options to your Tomcat config, usually to
   `TOMCAT_HOME/comf/setenv.local`:

    # Add options for JMagick to load properly
    JAVA_OPTS="$JAVA_OPTS -Djmagick.systemclassloader=no"

3) From the command-line, type:

    mvn package

4) Copy `target/datagrinder.war` to your Tomcat webapps directory. For
   quicker deployment in the future, modify `deploy.sh` to use a
   utility script.
