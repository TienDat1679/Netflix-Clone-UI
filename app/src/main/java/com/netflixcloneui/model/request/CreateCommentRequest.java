package com.netflixcloneui.model.request;

public class CreateCommentRequest {
    String content;
    Long mediaId;
    String userId;

    public CreateCommentRequest(String content, Long mediaId, String userId) {
        this.content = content;
        this.mediaId = mediaId;
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
