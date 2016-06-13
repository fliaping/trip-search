package com.fliaping.trip.extracter;

import org.junit.Test;

/**
 * Created by Payne on 5/30/16.
 */
public class ConfReaderTest {

    @Test
    public void testGetItem() throws Exception {
        ConfReader conf = new ConfReader("conf/ctrip.xml");
        String method = conf.getItemRules("name").get(0).getValues()[0];
        System.out.println(method);
    }
}