package com.netflixcloneui.model.request;

public class PlaybackProgressRequest {


    public PlaybackProgressRequest(Long position, Long episodeId, Long movieId) {
        this.position = position;
        this.episodeId = episodeId;
        this.movieId = movieId;
    }
    private Long episodeId;
    private Long position;
    private Long movieId;

    public PlaybackProgressRequest() {

    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public Long getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Long episodeId) {
        this.episodeId = episodeId;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }


}
