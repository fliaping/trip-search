package com.fliaping.trip.extracter;

import us.codecraft.webmagic.selector.Html;

/**
 * Created by Payne on 5/30/16.
 */
public interface SightExtracter {

    Sight sight = null;
    Html html = null;
    ConfReader conf = null;

    Sight extractHtml(Html html);

    /**
     * 解析站点上的景点ID
     * @return id
     */
    int extSite_id();

    String extName();

    String[] extImages();

    float extScore();

    String extIntro();

    String extAddress();

    String[] extComments();

    int extComment_num();

    String[] extNear_hotels();

    String[] extTickets();

    String extTraffic();

    String extCoordinate();

    String[] extTypes();

    String[] extPlace();

    String extPageurl();


}
