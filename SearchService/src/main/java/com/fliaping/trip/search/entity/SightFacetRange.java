package com.fliaping.trip.search.entity;

import java.util.List;

/**
 * Created by Payne on 6/1/16.
 */
public class SightFacetRange {

    private String facetName;
    private long facetCount;
    private float start;
    private float end;
    private float gap;
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

    public String getFacetName() {
        return facetName;
    }

    public void setFacetName(String facetName) {
        this.facetName = facetName;
    }

    public long getFacetCount() {
        return facetCount;
    }

    public void setFacetCount(long facetCount) {
        this.facetCount = facetCount;
    }

    public float getStart() {
        return start;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public float getEnd() {
        return end;
    }

    public void setEnd(float end) {
        this.end = end;
    }

    public float getGap() {
        return gap;
    }

    public void setGap(float gap) {
        this.gap = gap;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
