package com.fliaping.trip.spider;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;

/**
 * Created by Payne on 5/24/16.
 */
public class Main {

    public static Spider qunarSpider = null;
    public static boolean isVerified = false;
    public static long lastVerifyTime ;
    public static void main(String[] args) {
        System.out.println("start tripSpider");
        for (String item : args) {
            System.out.println(item);
        }


        //BasicConfigurator.configure();
        qunarSpider = Spider.create(new QunarPageProcessor());
        qunarSpider.setScheduler(new FileCacheQueueScheduler("data"))
                .addUrl(QunarPageProcessor.URL_SEED)
                //.addPipeline(new ConsolePipeline())
                .addPipeline(new JsonFilePipeline("data"))
                .thread(25)
                .run();
    }
}
