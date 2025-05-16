package com.netflixcloneui.model.request;

public class AddToWatchListRequest {
    private Long mediaId;
    private String type;

    public AddToWatchListRequest(Long mediaId, String type) {
        this.mediaId = mediaId;
        this.type = type;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
