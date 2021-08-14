
##Streaming Server##
<div align="center">A streaming server powered by JAVA 11 and JavaFX.
<img src="https://i.imgur.com/AHfbHOU.png">
</div>

## Usage
By pressing the "Start" button the server will automatically generate each missing variant of the videos that are included in the specified "root directory" (first TextBox). For each video title a [VideoManager](https://github.com/DTselikis/Streaming-Server/blob/master/src/main/java/utils/VideoConverter.java) is created which handle this video title only. Each VideoManager then is responsible of finding its missing varaints and create an instance of [VideoConverter](https://github.com/DTselikis/Streaming-Server/blob/master/src/main/java/utils/VideoManager.java) for each one. After **all** convertions are finished, the server starts listenning fron incoming connections.
### Note
**All video conversions are performed at the same time so heavy CPU usage is to be expected.**

## Features
- Convert video files to 240p, 360, 480p, 720p and 1080p resolution.
- Stream video **without audio**.
- Multiple client support.
- Independentl ogging for each client.
### Supported video formats
- avi
- mp4
- mkv
### Supported protocols
- TCP/IP
- UDP
- RTP/UDP

## Dependencies
- OpenJDK 11
- JavaFX SDK 15
- FFmpeg (ffplay, ffprobe, ffmpeg)
### Maven dependencies
- log4j 2.14.1 (core & api)
- FFmpeg wrapper 0.6.2

## Companion app
[Streaming Client](https://github.com/DTselikis/Streaming-Client)
