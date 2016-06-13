package com.fliaping.trip.search.entity;

import org.apache.solr.client.solrj.beans.Field;

/**
 * Created by Payne on 4/5/16.
 * 查询景点
 */
public class Sight {

    @Field("id")
    private int id;

    @Field("sight_id")
    private int sight_id;

    @Field("sight_name")
    private String name ;

    @Field("sight_images")
    private String images ;

    @Field("sight_score")
    private float score;

    @Field("sight_intro")
    private String intro ; //sight detail introduction

    @Field("sight_address")
    private String address;

    @Field("sight_comments")
    private String comments[];

    @Field("sight_comment_num")
    private int comment_num;

    @Field("sight_near_hotels")
    private String near_hotels[];

    @Field("sight_tickets")
    private String tickets[];

    @Field("sight_traffic")
    private String traffic;

    @Field("sight_coordinate")
    private String coordinate;

    @Field("sight_type")
    private String type[];



    @Field("sight_place")
    private String place[];

    @Field("pageurl")
    private String pageurl;

    @Field("_dist_")
    private double _dist_;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSight_id() {
        return sight_id;
    }

    public void setSight_id(int sight_id) {
        this.sight_id = sight_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String[] getComments() {
        return comments;
    }

    public void setComments(String[] comments) {
        this.comments = comments;
    }

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public String[] getNear_hotels() {
        return near_hotels;
    }

    public void setNear_hotels(String[] near_hotels) {
        this.near_hotels = near_hotels;
    }

    public String[] getTickets() {
        return tickets;
    }

    public void setTickets(String[] tickets) {
        this.tickets = tickets;
    }

    public String getTraffic() {
        return traffic;
    }

    public void setTraffic(String traffic) {
        this.traffic = traffic;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public String[] getType() {
        return type;
    }

    public void setType(String[] type) {
        this.type = type;
    }

    public String[] getPlace() {
        return place;
    }

    public void setPlace(String[] place) {
        this.place = place;
    }

    public String getPageurl() {
        return pageurl;
    }

    public void setPageurl(String pageurl) {
        this.pageurl = pageurl;
    }

    public double get_dist_() {
        return _dist_;
    }

    public void set_dist_(double _dist_) {
        this._dist_ = _dist_;
    }
}
