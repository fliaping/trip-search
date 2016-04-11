package com.fliaping.trip.search.entity;

import org.apache.solr.client.solrj.beans.Field;

import java.lang.annotation.Retention;

/**
 * Created by Payne on 4/5/16.
 * 查询景点
 */
public class Sight {

    @Field("sight_id")
    private int id;

    @Field("sight_name")
    private String name ;

    @Field("sight_intro")
    private String intro ; //sight detail introduction

    @Field("sight_address")
    private String address;

    @Field("sight_coordinate")
    private String coordinate;

    @Field("sight_type_to")
    private String type[];

    @Field("sight_score_ctrip")
    private float score_ctrip;

    @Field("sight_place_to")
    private String place[];

    @Field("pageurl")
    private String pageurl;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public String[] getType() {
        return type;
    }

    public float getScore_ctrip() {
        return score_ctrip;
    }

    public String[] getPlace() {
        return place;
    }

    public String getPageurl() {
        return pageurl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setType(String[] type) {
        this.type = type;
    }

    public void setScore_ctrip(float score_ctrip) {
        this.score_ctrip = score_ctrip;
    }

    public void setPlace(String[] place) {
        this.place = place;
    }

    public void setPageurl(String pageurl) {
        this.pageurl = pageurl;
    }
}
