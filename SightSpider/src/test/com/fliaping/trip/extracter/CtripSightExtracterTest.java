package com.fliaping.trip.extracter;

import com.fliaping.trip.extracter.mysql.SightInsert;
import org.junit.Test;
import us.codecraft.webmagic.selector.Html;

/**
 * Created by Payne on 5/31/16.
 */
public class CtripSightExtracterTest {

    @Test
    public void testExtractHtml() throws Exception {
        String htmltext = Utils.readFile("/Users/Payne/Workspace/GraduateProject/Heritrix/Data/html/you.ctrip.com/sight/zigong575/1835707.html");
        Html html = new Html(htmltext);
        CtripSightExtracter ctrip = new CtripSightExtracter();

        SightInsert insert = new SightInsert();
        Sight sight = ctrip.extractHtml(html);
        sight.toString();
        //insert.insert(sight);
    }

    @Test
    public void testExtSite_id() throws Exception {

    }

    @Test
    public void testExtName() throws Exception {

    }

    @Test
    public void testExtImages() throws Exception {

    }

    @Test
    public void testExtScore() throws Exception {

    }

    @Test
    public void testExtIntro() throws Exception {

    }

    @Test
    public void testExtAddress() throws Exception {

    }

    @Test
    public void testExtComments() throws Exception {

    }

    @Test
    public void testExtComment_num() throws Exception {

    }

    @Test
    public void testExtNear_hotels() throws Exception {

    }

    @Test
    public void testExtTickets() throws Exception {

    }

    @Test
    public void testExtTraffic() throws Exception {

    }

    @Test
    public void testExtCoordinate() throws Exception {

    }

    @Test
    public void testExtTypes() throws Exception {

    }

    @Test
    public void testExtPlace() throws Exception {

    }

    @Test
    public void testExtPageurl() throws Exception {

    }
}