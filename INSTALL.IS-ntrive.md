Datagrinder Installation - IS-ntrive
====================================
Listed here are notes from installing datagrinder on IS-ntrive, a
local Mac OSX workstation.

## Tomcat
sudo port install tomcat6

## ImageMagick
sudo port install ImageMagick

## JMagick
This one is a little tricky to install. http://wesleyli.blog.com/2011/09/01/install-jmagick-on-mac/
and http://comments.gmane.org/gmane.comp.java.jmagick/559 provided
helpful tips on getting JMagick installed on OSX.

* Get JMagick source (I cloned the git repo: https://github.com/techblue/jmagick)
* Configure
  * For Fedora 3, running on Tomcat6, run `./configure JFLAGS="-target 1.6 -source 1.6"
    --with-java-home=/System/Library/Frameworks/JavaVM.framework/Versions/Current
    --with-magick-home=/opt/local/lib/ImageMagick-6.8.8`
  * For Fedora 4, running on Tomcat7, run `./configure
    --with-java-home=/System/Library/Frameworks/JavaVM.framework/Versions/Current
    --with-magick-home=/opt/local/lib/ImageMagick-6.8.8`
* Run `make all`. Older versions of JMagick code gave me the following error:

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

      sudo cp lib/jmagick-6.6.9.jar /Users/ntrive/local/apache-tomcat-7.0.50/lib/

* Restart Tomcat
* Follow the rest of the instructions in README
