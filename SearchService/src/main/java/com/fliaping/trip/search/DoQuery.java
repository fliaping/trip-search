package com.fliaping.trip.search;

import com.fliaping.trip.search.entity.Sight;
import com.fliaping.trip.search.entity.SightList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Payne on 4/3/16.
 */
public class DoQuery {

    private HttpSolrClient solrClient = MySolrClient.getSolrClient();
    private HttpServletRequest request;
    private HttpServletResponse response;
    private SolrQuery solrQuery;

    public DoQuery(HttpServletRequest request,HttpServletResponse response){
        if (solrQuery == null)   solrQuery = new SolrQuery();
        if (request != null)   this.request = request;
        if (response != null)   this.response = response;

        // set default query
        solrQuery.setStart(0)
                .setRows(10)
                .setQuery("{!geofilt}")
                .set("indent","true")
                .set("d",100)
                .set("pt","22.5347168,113.971228") //当前坐标
                .set("sfield",Setting.sfield) //位置域
                .set("fl","_dist_:geodist(),*"); //返回结果中添加_dist_字段(到当前坐标的距离)
    }

    //for test
    public DoQuery(){
        if(solrQuery == null)   solrQuery = new SolrQuery();
        if(request != null)   this.request = request;

        // set default query
        solrQuery.setStart(0)
                .setRows(10)
                .setQuery("{!geofilt}")
                .set("indent","true")
                .set("d",100)
                .set("pt","22.5347168,113.971228") //当前坐标
                .set("sfield",Setting.sfield) //位置域
                .set("fl","_dist_:geodist(),*"); //返回结果中添加_dist_字段(到当前坐标的距离)
    }



    /**
     * 附近搜索
     */
    public  void  nearQuery(){
        //http://localhost:9090/ss?query_type=near&distance=10&point=22.5347168,113.971228
        int distance = Integer.parseInt(request.getParameter(UrlP.distance.name()));
        if (distance <= 0 || distance > 40076) distance = 100; //判断距离值有效
        String point = request.getParameter(UrlP.point.name());


        solrQuery.set("d",distance)
                 .set("pt",point)
                 .set("q","{!geofilt}");



        //设置facet
        solrQuery.setFacet(true)
                .setFacetMissing(true)
                .set("facet.field","sight_type");


        System.out.println(solrQuery.toQueryString());



        try {
            QueryResponse queryResponse = solrClient.query(solrQuery);

            List facet = queryResponse.getFacetFields();
            System.out.println(facet.size());
            for (int i = 0; i < facet.size(); i++) {
                FacetField ff = (FacetField) facet.get(i);
                System.out.println("name:"+ff.getName()+" valuecount:"+ff.getValueCount());
                List<FacetField.Count> countList =  ff.getValues();
                for (int j = 0; j < countList.size(); j++) {
                    FacetField.Count count = countList.get(j);
                    if (count.getCount() == 0) {countList.remove(j);continue;}
                    System.out.println("name:"+count.getName()+" count:"+count.getCount());
                }
            }

            List<Sight> list = queryResponse.getBeans(Sight.class);
            SolrDocumentList documentList = queryResponse.getResults();
            SightList sightList = new SightList();
            sightList.setSights(list);
            sightList.setTime(queryResponse.getQTime());
            sightList.setTotalNum(documentList.getNumFound());
            sightList.setNowPage((int) (documentList.getStart()/documentList.size()) + 1);
            sightList.setTotalPage((int) (documentList.getNumFound()/documentList.size()) + 1);
            Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
            String json = gson2.toJson(sightList);
            System.out.println(json);

            respJson(json);

        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public void keyQuery(){
        //设置查询
        solrQuery.set("fq",request.getParameter(UrlP.keyword.name()));


        //设置高亮
        solrQuery.setHighlight(true)
                .setHighlightSimplePre("<em>")
                .setHighlightSimplePost("</em>")
                .set("hl.fl","sight_intro");
        //设置facet
        solrQuery.setFacet(true)
                .setFacetMissing(true)
                .set("facet.field","sight_type");

    }

    public void mapQuery(){

    }

    private void respJson(String json){
        if (response == null) return;
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            pw.print(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
