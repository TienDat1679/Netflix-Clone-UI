package com.netflixcloneui.model;



import java.util.List;

public class Genre {
    private Long id;
    private String name;
    private String description;
    private List<Movie> movies;
    private List<TVSeries> series;

    public Genre(Long id, String name, String description, List<Movie> movies, List<TVSeries> series) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.movies = movies;
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
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public List<TVSeries> getSeries() {
        return series;
    }

    public void setSeries(List<TVSeries> series) {
        this.series = series;
    }

}