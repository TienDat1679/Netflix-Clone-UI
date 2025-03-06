package com.netflixcloneui.model;

public class Episode {


    private Long id;

    private int episodeNumber;
    private String name;

    private String overview;
    private String airDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getAirDate() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getStillPath() {
        return stillPath;
    }

    public void setStillPath(String stillPath) {
        this.stillPath = stillPath;
    }

    public TVSeries getTvSerie() {
        return tvSerie;
    }

    public void setTvSerie(TVSeries tvSerie) {
        this.tvSerie = tvSerie;
    }

    private int seasonNumber;
    private int runtime;

    public Episode(Long id, int episodeNumber, String name, String overview, String airDate, int seasonNumber, int runtime, String stillPath, TVSeries tvSerie) {
        this.id = id;
        this.episodeNumber = episodeNumber;
        this.name = name;
        this.overview = overview;
        this.airDate = airDate;
        this.seasonNumber = seasonNumber;
        this.runtime = runtime;
        this.stillPath = stillPath;
        this.tvSerie = tvSerie;
    }

    private String stillPath;

    private TVSeries tvSerie;
}
