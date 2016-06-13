package com.fliaping.trip.search.entity;

/**
 * Created by Payne on 6/1/16.
 */
public class SightFacetQuery {
    private String query;
    private String alias;
    private int count;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
