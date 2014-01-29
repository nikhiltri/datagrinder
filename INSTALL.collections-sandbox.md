Datagrinder Installation - collections-sandbox
====================================
Listed here are notes from installing datagrinder on collections-sandbox, a
VM Redhat instance.

## Tomcat
Version 7 already installed. But I made `sudo tomcat7 version` runable from the
command line:
`sudo chsh -s /bin/bash tomcat`
`sudo chsh -s /bin/bash tomcat7`

## ImageMagick
`sudo yum install ImageMagick`
`sudo yum install ImageMagick-devel`

## JMagick
Because collections-sandbox has no connection to the Internet, I cloned the JMagick code on my local box and scp'd it over.

On `IS-ntrive`:
* `git clone https://github.com/techblue/jmagick.git jmagick-6.6.9-toCollections-sandbox`
* `scp -r jmagick-6.6.9-toCollections-sandbox ntrive@collections-sandbox:/home/ntrive/jmagick`

Back on `collections-sandbox`:
* `cd ~/jmagick`
* `./configure --with-java-home=/usr/lib/jvm/java --with-magick-home=/usr/lib64/ImageMagick-6.5.4`
* `make all`
* `sudo make install`
* Copy `jmagick-X-X-X.jar` to your Tomcat lib directory:

      sudo cp lib/jmagick-6.6.9.jar /usr/share/tomcat7/lib/

* Add the following lines to `/etc/sysconfig/tomcat7`:

      # Add options for JMagick to load properly
      JAVA_OPTS="$JAVA_OPTS -Djmagick.systemclassloader=no"

      # Set properties so the JMagick library is found
      LD_LIBRARY_PATH="/usr/local/lib"

* Restart Tomcat
* Follow the rest of the instructions in README
