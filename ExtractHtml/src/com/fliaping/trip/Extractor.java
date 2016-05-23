package com.fliaping.trip;

import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

/**
 * Created by Payne on 3/18/16.
 */
public class Extractor {


    public static Sight extractHtml(String filePath){
        File htmlFile = new File(filePath);
        return extractHtml(htmlFile);
    }
    /**
     * 从景点页面的html文件中解析出来我们所需要的信息
     * @param htmlFile html文件
     * @return 景点对象 @Sight
     */
    public static Sight extractHtml(File htmlFile){
        Sight sight = new Sight();
        Document htmlDoc = null;
        //解析景点id
        String fileName[] = htmlFile.getName().split("\\.");
        String preName[] = fileName[0].split("-");
        sight.setId(Integer.valueOf(preName[0]));
        try {
            //HTML文档解析为Document对象
            htmlDoc = Jsoup.parse(htmlFile,"utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //解析页面链接
        Elements url = htmlDoc.getElementsByAttributeValue("rel","canonical");

        String pageUrl = url.first().attr("href");
        sight.setPageUrl(pageUrl);
        // 解析景点名字
        Elements f_left = htmlDoc.getElementsByClass("f_left");
        String sight_name = f_left.first().child(0).child(0).text();
        sight.setName(sight_name);
        //解析携程评分
        Elements noscore = htmlDoc.getElementsByClass("noscore");
        if(noscore.first()!=null){  //暂无评分
            sight.setScore_ctrip(-1);
        }else {
            Elements score = htmlDoc.getElementsByClass("score");
            String score_v = score.first().child(0).text();
        }

        //解析景点介绍
        Elements sight_intro = htmlDoc.getElementsByClass("detailbox_dashed");
        if(sight_intro.first().children().size() < 3){  //没有景点介绍
            sight.setIntro("");
        }else {
            String intro = sight_intro.first().child(2).text();
            sight.setIntro(intro);
        }

        //解析景点地址
        Elements sight_addr = htmlDoc.getElementsByClass("s_sight_addr");
        String addr = sight_addr.first().text().substring(3);
        sight.setAddress(addr);
        //解析景点坐标
        Elements sight_coordinate = htmlDoc.getElementsByClass("s_sight_map");
        String coordinate = sight_coordinate.first().child(0).child(0).attr("src");
        MultiMap<String> values = new MultiMap<String>();
        UrlEncoded.decodeTo(coordinate,values,"UTF-8");
        sight.setCoordinate(values.getString("center"));
        //解析景点类型
        Elements sight_con = htmlDoc.getElementsByClass("s_sight_con");
        if(sight_con.first()==null || sight_con.first().children().size() < 1){  //没有景点类型
            sight.setType("");
        }else {
            Elements sight_types = sight_con.first().children();
            String type = "";
            for (Element sight_type:
                    sight_types) {
                type+=sight_type.text()+",";
            }
            type = type.substring(0,type.length()-1);
            sight.setType(type);
        }

        //解析palce
        Elements breadbar = htmlDoc.getElementsByClass("breadbar_v1");
        Element ul = breadbar.first().child(0);
        Elements lis = ul.children();
        String place[] = new String[lis.size()-3];
        int index = 0;
        for (int i = 0; i < lis.size() ; i++) {
            if(i<2 || i == (lis.size() - 1)) continue;
            String placename = lis.get(i).child(0).text();
            if (placename.endsWith("景点")){
                placename = placename.substring(0,placename.length()-2);
            }
            place[index++] = placename;
        }
        sight.setPlace(place);
        //System.out.println(place[0]);
        return sight;
    }
}
