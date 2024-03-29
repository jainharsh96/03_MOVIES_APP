package com.example.harsh.moviesapp.datastore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieVideoDetails {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<MovieVideoResult> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<MovieVideoResult> getResults() {
        return results;
    }

    public void setResults(List<MovieVideoResult> results) {
        this.results = results;
    }

}
