package com.fliaping.trip.extracter;

import com.fliaping.trip.extracter.mysql.SightInsert;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import us.codecraft.webmagic.selector.Html;

import java.io.*;

/**
 * Created by Payne on 5/30/16.
 */
public class DoExtract {

    public void ctrip(){

        //CtripSightSightExtracter ctrip_er = new CtripSightSightExtracter();

        String htmltext = "";
        Html html = new Html(htmltext);

        //Sight sight = ctrip_er.extractHtml(html);


    }

    private static SightInsert sightInsert = new SightInsert();
    private static CtripSightExtracter ctrip = new CtripSightExtracter();
    private static QunarSightExtracter qunar = new QunarSightExtracter();
    private static int extraterror = 0;
    private static int inserterror = 0;
    private static int success = 0;

    private static OutputStream ops = null;
    private static long begintime = System.currentTimeMillis();
    private static long lasttime = 0;

    public static void main(String[] args) {
        String ctripDir = "/Users/Payne/Workspace/GraduateProject/Heritrix/Data/html/you.ctrip.com/sight";

        String qunarDir = "/Users/Payne/Workspace/GitHub/trip-search/SightSpider/data/travel.qunar.com";

        //for ctrip
        //File dirFile =  new File(ctripDir);

        //for qunar
        File dirFile =  new File(qunarDir);

        //日志文件
        try {
            ops = new FileOutputStream(dirFile.getParent()+"/log-"+System.currentTimeMillis()+".txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        //for ctrip
        //ctripResolve(new File(ctripDir));

        //for qunar
        qunarResolve(new File(qunarDir));


        try {
            writeLog("time: "+(System.currentTimeMillis()-begintime)/1000+"  success: "+success+"  ExtratError: "+extraterror+"  inserterror: "+inserterror);
            ops.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void ctripResolve(File file){
        long currenttime = System.currentTimeMillis();
        if(currenttime > (lasttime+5000)){
            System.out.println("time: "+(currenttime-begintime)+"  success: "+success+"  ExtratError: "+extraterror+"  inserterror: "+inserterror);
        }
        lasttime = currenttime;
        if(file.isFile() ){
            String path[] = file.getPath().split("/");
            if("sight".equals(path[path.length-3]) && path[path.length-1].matches("[0-9]*.html$")){

                Sight sight = null;
                boolean isSkip = false;
                try{

                    Html html = new Html(Utils.readFile(file.getPath()));
                    sight = ctrip.extractHtml(html);

                }catch (Exception e){
                    isSkip = true;
                    extraterror++;
                    writeLog("extract error   "+file.getPath());
                    System.out.println("解析失败  "+ file.getPath());
                }
                if(!isSkip){
                    try{
                        int result = sightInsert.insert(sight);
                        if(result > 0){
                            success++;
                        }else {
                            inserterror++;
                            writeLog("Duplicate insert error "+file.getPath());
                            System.out.print("已存在该景点   ");
                        }
                        System.out.print(result+"  名字:("+sight.getName()+")   属地:(");
                        for (int i = 0; i < sight.getPlace().length; i++) {
                            System.out.print(sight.getPlace()[i]+"/");
                        }
                        System.out.println(")");
                    }catch (Exception e){
                        inserterror++;
                        writeLog("insert error   "+file.getPath());
                        System.out.println("插入失败  "+ file.getPath());
                    }
                }
            }
        }else {
            //System.out.println("分析文件夹  "+file.getPath());
            for (File subFile :
                    file.listFiles() ) {
                ctripResolve(subFile);
            }
        }
    }

    private static void qunarResolve(File file){
        long currenttime = System.currentTimeMillis();
        if(currenttime > (lasttime+5000)){
            System.out.println("time: "+(currenttime-begintime)+"  success: "+success+"  ExtratError: "+extraterror+"  inserterror: "+inserterror);
        }
        lasttime = currenttime;
        if(file.isFile() ){
            String path[] = file.getPath().split("/");
                Sight sight = null;
                boolean isSkip = false;
                try{

                    JsonParser parser = new JsonParser();

                    JsonObject object = (JsonObject) parser.parse(new FileReader(file.getPath()));

                    Html html = new Html(object.get("html").getAsString());
                    sight = qunar.extractHtml(html);

                }catch (Exception e){
                    isSkip = true;
                    extraterror++;
                    writeLog("extract error   "+file.getPath());
                    System.out.println("解析失败  "+ file.getPath());
                }
                if(!isSkip){
                    try{
                        int result = sightInsert.insert(sight);
                        if(result > 0){
                            success++;
                        }else {
                            inserterror++;
                            writeLog("Duplicate insert error "+file.getPath());
                            System.out.print("已存在该景点   ");
                        }
                        System.out.print(result+"  名字:("+sight.getName()+")   属地:(");
                        for (int i = 0; i < sight.getPlace().length; i++) {
                            System.out.print(sight.getPlace()[i]+"/");
                        }
                        System.out.println(")");
                    }catch (Exception e){
                        inserterror++;
                        writeLog("insert error   "+file.getPath());
                        System.out.println("插入失败  "+ file.getPath());
                    }
                }
        }else {
            //System.out.println("分析文件夹  "+file.getPath());
            for (File subFile :
                    file.listFiles() ) {
                qunarResolve(subFile);
            }
        }
    }

    private static void writeLog(String log){
        try {
            ops.write((log+"\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
