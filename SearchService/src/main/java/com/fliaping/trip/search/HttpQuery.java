package com.fliaping.trip.search;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Payne on 4/4/16.
 */
public class HttpQuery {
    private String queryUrl = "";
    private Map<String,String> param = new HashMap<String,String>();
    public HttpQuery(String queryUrl){
        this.queryUrl = queryUrl+"/select?";
    }
    public HttpQuery set(String key, String value){
        param.put(key,value+"");
        return this;
    }
    public HttpQuery set(String key, int value){
        param.put(key,value+"");
        return this;
    }

    public boolean query(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getQueryUrl())
                .header("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
                .header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6,en-US;q=0.4,zh-TW;q=0.2")
                .header("Accept-Encoding","gzip, deflate, sdch")
                .header("Cache-Control","max-age=0")
                .header("Connection","keep-alive")
                .header("Upgrade-Insecure-Requests","1")
                .header("Host","localhost:8983")
                .header("Cookie","_ga=GA1.1.1007140748.1458630116; Hm_lvt_a4cf4b31c932dc84491407ee469176a6=1458630117; flashdrop=2378466755309165781; JSESSIONID=1jxzo7p9j1iwupzyqbytg0aiy")
                .build();


        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
       /* Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }*/


        try {
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public String getQueryUrl() {
        Iterator it = param.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String,String> entry = (Map.Entry<String, String>) it.next();
            queryUrl+=entry.getKey()+"="+entry.getValue()+"&";
        }
        return queryUrl;
    }
}
