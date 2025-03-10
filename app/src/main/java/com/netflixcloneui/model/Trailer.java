package com.netflixcloneui.model;

public class Trailer {

    private String id;
    private String key;
    private String name;

    private String site;
    private String type;
    private Movie movie;

    private TVSeries tvSerie;
    public Trailer(String id, String key, String name, String site, String type, Movie movie, TVSeries tvSerie) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.type = type;
        this.movie = movie;
        this.tvSerie = tvSerie;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public TVSeries getTvSerie() {
        return tvSerie;
    }

    public void setTvSerie(TVSeries tvSerie) {
        this.tvSerie = tvSerie;
    }
}
