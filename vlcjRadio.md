# vlcj radio #

## Introduction ##

This is a Swing rich-client application that uses vlcj and libvlc to play internet radio audio streams.

A number of different internet radio directories (e.g. Icecast, Indymedia, ListenLive and BBCStreams) are scanned for available stations and displayed in the application.

You can then search through the list of available radio stations and filter by various properties (such as name or genre).

The application also includes functionality to save the audio stream, if this is supported by your operating system.

## Screenshots ##

[An exciting progress dialog](http://vlcj-apps.googlecode.com/svn/trunk/vlcj-radio/etc/vlcj-radio-1.png)

[Genre live filtering](http://vlcj-apps.googlecode.com/svn/trunk/vlcj-radio/etc/vlcj-radio-2.png)

[Station name live filtering](http://vlcj-apps.googlecode.com/svn/trunk/vlcj-radio/etc/vlcj-radio-3.png)

## Prerequisites ##

The prerequisites are:

  * a Java 6 (1.6) run-time environment;
  * vlc 1.1.4 (or later, the later the version the better).

In addition, to save audio streams:

  * a version of ffmpeg that contains an MP3 encoder.

You do not get MP3 encoding by default on Ubuntu, the following may help:

```
$sudo apt-get install libavcodec-extra-52
```

## Dependencies ##

This application has the following dependencies (all required Java library dependencies are included in the download package):

  * vlcj 1.1.5;
  * JNA 3.2.7 (jna.jar and platform.jar);
  * MiGLayout 3.7.3.1;
  * GlazedLists 1.8.0;
  * Simple XML Framework 2.4;
  * HtmlUnit 2.8 (and all associated dependencies).

## Implementation ##

Most of the code actually deals with the Swing user interface and the downloading of remote station directory information.

The amount of code that deals purely with vlcj and vlc is minimal and demonstrates how trivially easy it is to play audio media in Java with vlc.

## Download ##

Prepare a local directory:

```
$mkdir vlcj-apps
$cd vlcj-apps
```

Download the application package from the [downloads](http://code.google.com/p/vlcj-apps/downloads/list) page, or:

```
$wget http://vlcj-apps.googlecode.com/files/vlcj-radio-1.2.1-dist.tgz
```

Unpack the archive to a directory on your local disk.

```
$tar -xzf vlcj-radio-1.2.1-dist.tgz
```

This will create a directory named "vlcj-radio-1.2.1":

```
$cd vlcj-radio-1.2.1
```

## Starting the Application ##

```
$java -jar vlcj-radio-1.2.1.jar
```

You can enable vlcj logging if you like:

```
$java -Dvlcj.log=DEBUG -jar vlcj-radio-1.2.1.jar
```

## Usage ##

This application stores files in a directory called "vlcj-radio" in your user home directory.

After launching the application the main window will appear.

First the radio station directories are fetched, in a background thread, from a local cache if available and if not from the appropriate internet directory.

If the local cache files somehow gets corrupted you can simply delete them and they will be fetched again the next time you start the application.

To force an update of the cached files, you use "Update Directory" on the application "Radio" menu.

When the directories have finished loading, you will then see a scrolling table of radio stations and a set of search controls.

You can sort, in ascending or descending order, by clicking on the table column headers. The sort can work across multiple columns. To reset the sort back to a single column you double-click the column header.

The search controls implement a live filter on the list of radio stations so you can narrow down the list to those stations you are interested in. For example, typing "trance" into the genre filter control will remove any items from the list that do not contain "trance" in their genre.

When you have found a radio station you want to listen to, double-click the corresponding table row or select the row and click the "Play" button.

The streaming should start after a brief pause. Some streams may take a little longer to start.

If the streaming does not start within five seconds or so it is likely the radio station you selected is currently off-line. There is no guarantee that every station in the directory is on-line.

There are no volume controls in the application itself, you're expected to use your desktop volume applet/controls.

### Recording Streams ###

To enable stream recording you use the "Radio" menu and the "Enable Stream Recording" menu item. You will be shown an informational dialog box that tells you that the recording will begin/end the next time you play a stream.

Recorded files will appear in a vlcj-radio directory inside your user home directory.

Some internet radio stations forbid the recording of audio streams - it is your responsibility to comply with any such terms and conditions. The basic operation of the record functionality provided by this application is no different than that provided by vlc itself.

This software does not contain an MP3 encoder - it may, through libvlc, makes use of an MP3 encoder if one is available as part of your operating system. Again, the basic operation of encoding to MP3 is no different than that provided by vlc itself.

It is entirely your responsibility what you record and encode.

## Notes ##

It would be very easy to build upon this application to broadcast the radio station stream to devices across a local network.

The IceCast directory service at xiph.org is currently restricted to returning 1000 station entries even though there are around 9000 stations in the directory.

This application scrapes HTML pages to look for radio stations - if the format of the HTML pages changes, then this will quite likely stop working. It can be a moving target to keep up to date with such changes and inevitably there will be times when the current version of this application stops working. In any event, this is intended as a reference application rather than a production application.