package com.fliaping.trip;

/**
 * Created by Payne on 3/17/16.
 */
public class Sight {
    private int id;
    private String pageUrl;
    private String name;
    private float score_ctrip;
    private String intro;
    private String address;
    private String coordinate;
    private String type;
    private String place[];

    public void setId(int id) {
        this.id = id;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore_ctrip(float score_ctrip) {
        this.score_ctrip = score_ctrip;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPlace(String[] place) {
        this.place = place;
    }

    public int getId() {

        return id;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public String getName() {
        return name;
    }

    public float getScore_ctrip() {
        return score_ctrip;
    }

    public String getIntro() {
        return intro;
    }

    public String getAddress() {
        return address;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public String getType() {
        return type;
    }

    public String[] getPlace() {
        return place;
    }
}
