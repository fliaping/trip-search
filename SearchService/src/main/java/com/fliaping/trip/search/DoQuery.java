package com.fliaping.trip.search;

import com.fliaping.trip.search.entity.Sight;
import com.fliaping.trip.search.entity.SightFacet;
import com.fliaping.trip.search.entity.SightList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static com.fliaping.trip.search.Util.notNull;


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

        //get request page number and rows per page
        int rows = notNull(request.getParameter(UrlP.rows.name()),10);
        int page = notNull(request.getParameter(UrlP.page.name()),0);

        int start = page == 0 ? page*rows : (page-1)*rows;
        // set default query
        solrQuery.setStart(start)
                .setRows(rows)
                //.setQuery("{!geofilt}")
                //.set("indent","true")
                //.set("pt","22.5347168,113.971228") //当前坐标
                .set("sfield",Setting.sfield) //位置域
                .set("fl","_dist_:geodist(),*"); //返回结果中添加_dist_字段(到当前坐标的距离)
    }

    //for test
    public DoQuery(){
    }

    /**
     * 附近搜索
     */
    public  void  nearQuery(){
        //http://localhost:9090/ss?query_type=near&distance=10&point=22.5347168,113.971228

        //设置查询Query
        solrQuery.setQuery("{!geofilt}");

        //设置距离distance
        int distance = notNull(request.getParameter(UrlP.distance.name()),3000); //距离默认值3000公里
        if (distance >= 0 && distance < 40076) { //判断距离值有效
            solrQuery.set("d",distance);
        }

        //设置坐标点
        String point = notNull(request.getParameter(UrlP.point.name()),"22.5347168,113.971228"); //默认坐标世界之窗
        solrQuery.set("pt",point);

        //设置排序
        solrQuery.setSort("geodist()", SolrQuery.ORDER.asc);

        //设置显示字段 fl
        solrQuery.setFields("_dist_:geodist()","*");


        //设置facet
        solrQuery.setFacet(true)
                .setFacetMissing(true)
                .set("facet.field","sight_type");


        System.out.println(solrQuery.toQueryString());


        try {
            QueryResponse queryResponse = solrClient.query(solrQuery);

            //对solr的查询结果,整合后返回
            respJson(wrapResult(queryResponse,false));
            System.out.println("distance:"+distance);

        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public void keyQuery(){
        //http://localhost:8983/solr/trip/select?start=0&rows=10&sfield=sight_coordinate&fl=_dist_:geodist(),*&q=石河子&pt=44.3093867,86.0555942&sort=geodist() asc&facet=true&facet.missing=true&facet.field=sight_type
        //设置过滤
        //solrQuery.set("fq",request.getParameter(UrlP.keyword.name()));

        //设置关键字
        String keyword = notNull(request.getParameter(UrlP.keyword.name()),"*:*");
        solrQuery.setQuery(keyword);

        //设置坐标点
        String point = notNull(request.getParameter(UrlP.point.name()),"22.5347168,113.971228"); //默认坐标世界之窗
        solrQuery.set("pt",point);

        //设置排序
        String sortOrder = notNull(request.getParameter(UrlP.sort_order.name()),SortOrder.distance.get()); //默认距离排序


        if (SortOrder.distance.is(sortOrder)) { //距离排序
            solrQuery.setSort("geodist()", SolrQuery.ORDER.asc);

        }else if (SortOrder.score.is(sortOrder)){ //评分排序

            solrQuery.setSort("sight_score_ctrip", SolrQuery.ORDER.desc);

        }else if (SortOrder.price.is(sortOrder)){ //价格排序
            // TODO: 5/15/16 价格排序

        }else if (SortOrder.best.is(sortOrder)){ //综合排序
            // TODO: 5/15/16 综合排序

        }

        //设置过滤
        //设置景点类型过滤
        String sight_type = request.getParameter(UrlP.sight_type.name());
        if(sight_type != null){
            String[] type = sight_type.split(",");
            for (String item : type) {
                if (item != null){
                    solrQuery.addFilterQuery("sight_type:"+item);
                }
            }
        }
        //:TODO 设置地区过滤

        //:TODO 设置距离过滤

        //:TODO 设置评分过滤



        //设置高亮
        /*solrQuery.setHighlight(true)
                .setHighlightSimplePre("<em>")
                .setHighlightSimplePost("</em>")
                .set("hl.fl","sight_intro");*/
        //设置facet
        solrQuery.setFacet(true)
                .setFacetMissing(true)
                .set("facet.field","sight_type");


        System.out.println(solrQuery.toQueryString());
        try {
            QueryResponse queryResponse = solrClient.query(solrQuery);
            //对solr的查询结果,整合后返回
            respJson(wrapResult(queryResponse,true));
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void mapQuery(){

    }

    /**
     * 对查询结果包装成为客户端可用的格式
     * @param queryResponse solr的请求结果
     * @param hasFacet 是否需要facet
     * @return
     */
    private String wrapResult(QueryResponse queryResponse,boolean hasFacet){
        SightList sightList = new SightList();

        if(hasFacet){
            List<SightFacet> sightFacets = new ArrayList<SightFacet>();
            //整理facet数据
            List facet = queryResponse.getFacetFields();
            for (int i = 0; i < facet.size(); i++) {
                //取得每个facet的信息
                FacetField ff = (FacetField) facet.get(i);
                //System.out.println("name:"+ff.getName()+" valuecount:"+ff.getValueCount());

                SightFacet sightFacet = new SightFacet(); //facet对象
                sightFacet.setFacetName(ff.getName()); //设置facet的field名字
                List<SightFacet.Item> items = new ArrayList<SightFacet.Item>();
                //取得每个facet中的每个item
                List<FacetField.Count> countList =  ff.getValues();
                for (int j = 0; j < countList.size(); j++) {
                    FacetField.Count count = countList.get(j);
                    if (count.getCount() <= 0) continue; //facet中没有数据的,不加入结果集
                    //System.out.println("name:"+count.getName()+" count:"+count.getCount());
                    SightFacet.Item item = new SightFacet.Item(count.getName(),count.getCount());
                    items.add(item);
                }

                sightFacet.setFacetCount(items.size()); //设置facet中的个数
                sightFacet.setItems(items);   //设置facet中的items
                sightFacets.add(sightFacet);  //添加到facet列表中
            }
            sightList.setSightFacets(sightFacets);
        }


        //整理景点数据
        List<Sight> list = queryResponse.getBeans(Sight.class);
        SolrDocumentList documentList = queryResponse.getResults();

        sightList.setSights(list);
        sightList.setTime(queryResponse.getQTime());
        sightList.setTotalNum(documentList.getNumFound());
        sightList.setNowPage((int) (documentList.getStart()/documentList.size()) + 1);
        sightList.setTotalPage((int) (documentList.getNumFound()/documentList.size()) + 1);

        //美观型json
        Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
        String json = gson2.toJson(sightList);

        //紧凑型json
        //String json = sightList.toJson();
        return json;
    }
    /**
     * 返回json结果到客户端
     * @param json json字符串
     */
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
