package io.kreatimont.cinematograph.data.model.response;

import io.kreatimont.cinematograph.data.model.tmdb.people.Person;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PersonResponse {

    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private List<Person> results;

    @SerializedName("total_results")
    private int totalResults;

    @SerializedName("total_pages")
    private int totalPages;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Person> getResults() {
        return results;
    }

    public void setResults(List<Person> results) {
        this.results = results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

}
