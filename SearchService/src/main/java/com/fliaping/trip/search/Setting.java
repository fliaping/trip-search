package com.fliaping.trip.search;


/**
 * Created by Payne on 4/4/16.
 * url参数意义
 *    keyword          关键词                               null
 *    point            坐标点                               null
 *    distance         距离                                 100KM
 *    bound_type       界限方式{geofilt,bbox,linestring,polygon}   null
 *    boundary         范围                                 null
 *    sort_order       排序方式 {score,distance}             null
 *    sight_type       景点类型                              null
 *    place            行政区划                              null
 *    page             页数                                  1
 *    rows             每页记录条数                           10
 *    query_type       查询方式{near,key,map}                     near
 */
enum UrlP{   // url parameter enum ,url参数枚举
    keyword,           //关键词                               null
    point,             //坐标点                               null
    distance,          //距离                                 100KM
    bound_type,        //界限方式{geofilt,bbox,linestring,polygon}   null
    boundary,          //范围                                 null
    sort_order,        //排序方式 {score,distance}             null
    sight_type,        //景点类型                              null
    place,             //行政区划                              null
    page,              //页数                                  1
    rows,              //每页记录条数                           10
    query_type         //查询方式{near,key,map}                     near
}


enum  BoundType {
    geofilt("geofilt"),
    bbox("bbox"),
    linestring("linestring"),
    polygon("polygon");

    private String name;
    BoundType(String name) { this.name = name; }
    public String get(){ return this.name; }
    public boolean is(String is){ return name.equals(is); }
}

enum SortOrder {
    score("score"),
    distance("distance"),
    best("best"),
    price("price"),
    keyword("keyword");

    private String name;
    SortOrder(String name) { this.name = name; }
    public String get(){ return this.name; }
    public boolean is(String is){ return name.equals(is); }
}


enum QueryType {
    near("near"),
    key("key"),
    map("map");

    private String name;
    QueryType(String name) { this.name = name; }
    public String get(){ return this.name; }
    public boolean is(String is){ return name.equals(is); }
}



public class Setting {

    public static String sfield = "sight_coordinate";


    public static boolean contains(String string,UrlP method){
        if (null == string) return false;
        boolean contain = false;
        if (method == UrlP.bound_type){
            for (BoundType bt : BoundType.values()) {
                if(bt.get().equals(string)) contain = true;
            }
        }
        if (method == UrlP.sort_order){
            for (SortOrder bt : SortOrder.values()) {
                if(bt.get().equals(string)) contain = true;
            }
        }
        if (method == UrlP.query_type){
            for (QueryType bt : QueryType.values()) {
                if(bt.get().equals(string)) contain = true;
            }
        }

        return contain;
    }
}
