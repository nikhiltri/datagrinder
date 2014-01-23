Datagrinder
===========

## Prerequisites

1) Tomcat
2) ImageMagick
3) JMagick
   This one is a little tricky to install. http://wesleyli.blog.com/2011/09/01/install-jmagick-on-mac/
   and http://comments.gmane.org/gmane.comp.java.jmagick/559 provided
   helpful tips on getting JMagick installed on OSX.

   * Download JMagick source (I cloned the git repo: https://github.com/techblue/jmagick)
   * Run `./configure JFLAGS="-target 1.6 -source 1.6"
     --with-java-home=/System/Library/Frameworks/JavaVM.framework/Versions/Current
     --with-magick-home=/opt/local/lib/ImageMagick-6.8.7`
   * Run `make all`. This gave me the following error:

         make[1]: Entering directory `/Users/ntrive/Documents/DAMS/jmagick/6.4.0/src'
         make[2]: Entering directory `/Users/ntrive/Documents/DAMS/jmagick/6.4.0/src/magick'
         ../../Make.rules:175: *** missing separator.  Stop.
         make[2]: Leaving directory `/Users/ntrive/Documents/DAMS/jmagick/6.4.0/src/magick'
         make[1]: *** [dir_target] Error 2
         make[1]: Leaving directory `/Users/ntrive/Documents/DAMS/jmagick/6.4.0/src'
         make: *** [dir_target] Error 2

     To solve this, open up `Make.rules` in a text editor. Go to line
     175 and 176. You'll see these lines begin with four
     spaces instead of a tab character. Replace the spaces with a tab,
     then `make all` will work. (Source: http://stackoverflow.com/questions/14819033/jmagick-make-error)
   * Run `make install`
   * Create a symbolic link from JMagick to your Java library:

         ln -s /usr/local/lib/libJMagick-6.6.9.so /Library/Java/Extensions/libJMagick.jnilib

   * Copy `jmagick-X-X-X.jar` to your Tomcat lib directory:

         sudo cp lib/jmagick-6.6.9.jar /opt/local/share/java/tomcat6/lib/

## Installation

0) In `pom.xml`, modify the property `jmagick.path.jar` to point to
   your system's JMagick JAR file.

1) Add the following options to your Tomcat config, usually to
   `TOMCAT_HOME/comf/setenv.local`:

    # Add options for JMagick to load properly
    JAVA_OPTS="$JAVA_OPTS -Djmagick.systemclassloader=no"

2) From the command-line, type:

    mvn package

3) Copy `target/datagrinder.war` to your Tomcat webapps directory. For
   quicker deployment in the future, modify `deploy.sh` to use a
   utility script.
