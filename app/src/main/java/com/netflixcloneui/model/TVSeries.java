package com.netflixcloneui.model;

import java.util.List;

public class TVSeries {
    private Long id;
    private String name;
    private String overview;
    private String firstAirDate;
    private String posterPath;
    private String backdropPath;
    private boolean adult;
    private List<Episode> episodes;
    private List<Trailer> trailers;
    private List<Genre> genres;
    private List<Credit> credits;

    public TVSeries(Long id, String name, String overview, String firstAirDate, String posterPath, String backdropPath, boolean adult, List<Episode> episodes, List<Trailer> trailers, List<Genre> genres, List<Credit> credits) {
        this.id = id;
        this.name = name;
        this.overview = overview;
        this.firstAirDate = firstAirDate;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.adult = adult;
        this.episodes = episodes;
        this.trailers = trailers;
        this.genres = genres;
        this.credits = credits;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
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

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Credit> getCredits() {
        return credits;
    }

    public void setCredits(List<Credit> credits) {
        this.credits = credits;
    }
}
