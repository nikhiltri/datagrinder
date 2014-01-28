Datagrinder Installation - collections-sandbox
====================================
Listed here are notes from installing datagrinder on collections-sandbox, a
VM Redhat instance.

## Tomcat
Version 7 already installed. But I made tomcat status runable from the
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
* Create a symbolic link from JMagick to your Java library:

      #sudo ln -s /usr/local/lib/libJMagick-6.6.9.so /usr/lib/jvm/java/lib/
      sudo ln -s /usr/local/lib/libJMagick-6.6.9.so /usr/lib/jvm/java/jre/lib/ext/

* Copy `jmagick-X-X-X.jar` to your Tomcat lib directory:

      sudo cp lib/jmagick-6.6.9.jar /usr/share/tomcat7/lib/

* Restart Tomcat
* Follow the rest of the instructions in README
