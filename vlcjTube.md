# vlcj tube #

## Introduction ##

This is an SWT rich-client application that uses vlcj and libvlc to browse and play YouTube media.

The application contains an embedded SWT browser control used to navigate around and search YouTube for media to play.

On clicking a YouTube media link the video is played in an embedded vlcj media player.

## Screenshots ##

[Embedded Web Browser](http://vlcj-apps.googlecode.com/svn/trunk/vlcj-tube/etc/vlcj-tube-1.png)

[Embedded Media Player](http://vlcj-apps.googlecode.com/svn/trunk/vlcj-tube/etc/vlcj-tube-2.png)

## Prerequisites ##

The prerequisites are:

  * a Java 6 (1.6) run-time environment;
  * vlc 1.1.9 or vlc 1.2.0 (from git or a snapshot download - currently in development).

## Dependencies ##

This application has the following dependencies (most required Java library dependencies are included in the download package):

  * vlcj 1.2.0;
  * JNA 3.2.7 (jna.jar and platform.jar);
  * SWT 3.7M6 (OS-dependant)

## Implementation ##

The entire application is contained within a single small class file. The majority of the code deals with the construction of the user interface and the embedding of the browser and media player components.

## Download ##

Since SWT is used and there is no unified SWT jar for all platforms, you must download the correct package for your platform. These instructions assume "linux-x86\_64". See the downloads page for available packages.

Prepare a local directory:

```
$mkdir vlcj-apps
$cd vlcj-apps
```

Download the application package from the [downloads](http://code.google.com/p/vlcj-apps/downloads/list) page, or:

```
$wget http://vlcj-apps.googlecode.com/files/vlcj-tube-1.0.0-linux-x86_64.tgz
```

Unpack the archive to a directory on your local disk.

```
$tar -xzf vlcj-tube-1.0.0-linux-x86_64.tgz
```

This will create a directory named "vlcj-tube-1.0.0-linux-x86\_64":

```
$cd vlcj-tube-1.0.0-linux-x86_64
```

## Starting the Application ##

```
$java -jar vlcj-tube-1.0.0.jar
```

You can enable vlcj logging if you like:

```
$java -Dvlcj.log=DEBUG -jar vlcj-tube-1.0.0.jar
```

## Usage ##

After launching the application the main window will appear, presenting a web browser displaying the YouTube home page.

Use the web browser as normal to search for media to play. Clicking a media hyperlink will switch the display to an embedded media player and play it.

You can press the F11 key to toggle full-screen mode.

Press the ESCAPE key to quit out of playing video.

There are no volume controls in the application itself, you're expected to use your desktop volume applet/controls.

## Notes ##

The YouTube web page structure is subject to change and this can make vlc unable to play the media. You should always have the latest vlc version to make sure you have the properly working scripts that parse the YouTube pages to find the media.

There is plenty of scope to build upon and improve this application.