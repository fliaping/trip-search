package com.fliaping.trip.search.entity;

import java.util.List;

/**
 * Created by Payne on 5/3/16.
 */
public class SightFacet {
    private String facetName;
    private long facetCount;
    private List<Item> items;
    public static class Item {

        public Item(String name,long count){
            this.name = name;
            this.count = count;
        }
        private String name;
        private long count;

        public void setName(String name) {
            this.name = name;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public long getCount() {
            return count;
        }
    }

    public void setFacetName(String facetName) {
        this.facetName = facetName;
    }

    public void setFacetCount(long facetCount) {
        this.facetCount = facetCount;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getFacetName() {
        return facetName;
    }

    public long getFacetCount() {
        return facetCount;
    }

    public List<Item> getItems() {
        return items;
    }
}
