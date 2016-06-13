package com.fliaping.trip.extracter;

import com.fliaping.trip.extracter.mysql.SightInsert;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import us.codecraft.webmagic.selector.Html;

import java.io.FileReader;

import static org.junit.Assert.*;

/**
 * Created by Payne on 5/31/16.
 */
public class QunarSightExtracterTest {

    @Test
    public void testExtractHtml() throws Exception {
        Document document = Jsoup.parse("data/0a2b63797a07131255642fa7e19b15ff.json","utf-8");

        String filePath = "/Users/Payne/Workspace/GitHub/trip-search/SightSpider/data/travel.qunar.com/0151a91542839dc2a96245cf761a35c5.json";

        JsonParser parser = new JsonParser();

        JsonObject object = (JsonObject) parser.parse(new FileReader(filePath));

        Html html = new Html(object.get("html").getAsString());

        QunarSightExtracter qunar = new QunarSightExtracter(html);
        Sight sight = qunar.extractHtml(html);
        sight.toString();
        SightInsert insert = new SightInsert();
        insert.insert(sight);
        //System.out.println(qunar.extSite_id());

    }
}