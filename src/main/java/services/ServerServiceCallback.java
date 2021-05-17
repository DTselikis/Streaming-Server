package services;

import res.VideoInfo;

public class ServerServiceCallback {
    private final StreamingServerService service;

    public ServerServiceCallback(StreamingServerService service) {
        this.service = service;
    }

    public void callback(VideoInfo videoInfo) {
        service.addVideoInfo(videoInfo);
    }
}
