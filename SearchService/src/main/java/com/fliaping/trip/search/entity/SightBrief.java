package com.fliaping.trip.search.entity;

/**
 * Created by Payne on 4/5/16.
 * 景点概要
 */
public class SightBrief {
    private int id;
    private String coverImg;
    private String name;
    private String address;
    private float score;
    private int markNum;
    private String recom; //recommend
    private String highlight;
    private double latitude;
    private double longitude;

    public String getCoverImg() {
        return coverImg;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public float getScore() {
        return score;
    }

    public int getMarkNum() {
        return markNum;
    }

    public String getRecom() {
        return recom;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void setMarkNum(int markNum) {
        this.markNum = markNum;
    }

    public void setRecom(String recom) {
        this.recom = recom;
    }

    public int getId() {
        return id;
    }

    public String getHighlight() {
        return highlight;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
