package com.netflixcloneui.model;

public class Media {
    private Long id;
    private String title;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private String type; // "movie" hoặc "tv_series"

    public Media(Long id, String title, String overview, String posterPath, String backdropPath, String type) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
