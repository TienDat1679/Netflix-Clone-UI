package com.netflixcloneui.model;

import java.util.List;

public class Genre {
    private Long id;
    private String name;
    private String description;
    private List<Movie> movieSeries;
    private List<TVSeries> series;

    public Genre(Long id, String name, String description, List<Movie> movieSeries, List<TVSeries> series) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.movieSeries = movieSeries;
        this.series = series;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Movie> getMovies() {
        return movieSeries;
    }

    public void setMovies(List<Movie> movieSeries) {
        this.movieSeries = movieSeries;
    }

    public List<TVSeries> getSeries() {
        return series;
    }

    public void setSeries(List<TVSeries> series) {
        this.series = series;
    }
}
