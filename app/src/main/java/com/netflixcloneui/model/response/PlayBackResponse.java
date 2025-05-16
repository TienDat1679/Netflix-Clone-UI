package com.netflixcloneui.model.response;

public class PlayBackResponse {

    private Long position;
    private Long ppid;

    private String userId;

    private Long mediaId;
    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public Long getPpid() {
        return ppid;
    }

    public void setPpid(Long ppid) {
        this.ppid = ppid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }


    public PlayBackResponse(Long mediaId, String userId, Long ppid, Long position) {
        this.mediaId = mediaId;
        this.userId = userId;
        this.ppid = ppid;
        this.position = position;
    }


}
