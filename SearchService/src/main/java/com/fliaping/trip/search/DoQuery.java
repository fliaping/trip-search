package com.fliaping.trip.search;

import com.fliaping.trip.search.entity.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.fliaping.trip.search.Util.notNull;


/**
 * Created by Payne on 4/3/16.
 */
public class DoQuery {

    private SolrClient solrClient = MySolrClient.getSolrClient();
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

            solrQuery.setSort("sight_score", SolrQuery.ORDER.desc);

        }else if (SortOrder.comment.is(sortOrder)){ //评论数量排序
            solrQuery.setSort("sight_comment_num", SolrQuery.ORDER.desc);

        }else if (SortOrder.keyword.is(sortOrder)){ //关键词最佳匹配
            // 关键词最佳匹配
            solrQuery.set("defType","dismax");
            solrQuery.set("qf","sight_name^2 sight_intro^1 sight_comments^0.8");

        }else if (SortOrder.best.is(sortOrder)){ //综合排序
            //  &defType=dismax&qf=sight_name^10+sight_coordinate^5
            solrQuery.set("defType","dismax");
            solrQuery.set("qf","sight_name^2 sight_intro^1");
            //solrQuery.set("bf", "sum(div(Song_Quality,0.01),if(exists(Song_FileMV),20000,0),recip(ms(NOW,Song_CreateTimeForNew),1,10000,1))");
            solrQuery.addSort("geodist()", SolrQuery.ORDER.asc);
            solrQuery.addSort("sight_score", SolrQuery.ORDER.desc);
        }

        //设置过滤
        //设置景点类型过滤
        String sight_type = request.getParameter(UrlP.sight_type.name());
        if(sight_type != null){
            String[] type = sight_type.split(",");
            for (String item : type) {
                if (item != null){
                    //solrQuery.addFilterQuery("sight_type:"+item);
                    solrQuery.addFacetQuery("sight_type:"+item);
                }
            }
        }

        // 设置距离过滤
        String distance = request.getParameter(UrlP.distance.name());
        if (distance != null ) {
            //判断纯数字距离值有效  //0.x or xxxx
            if(distance.matches("^((0.[1-9])|([1-9][0-9]{0,3}))$")){
                solrQuery.set("d",distance);
            }
            //判断距离范围值有效 // xxx-xxx
            else if (distance.matches("[0-9]+-[0-9]+")){
                String fromto[] = distance.split("-");
                solrQuery.addFacetQuery("{!frange l="+fromto[0]+".001 u="+fromto[1]+"}geodist()");
            }


        }

        // 设置评分过滤
        String score_range = request.getParameter(UrlP.score_range.name());
        if(score_range != null && score_range.matches("[1-5],[1-5]")){
            String score[] = score_range.split(",");
            int start = Integer.parseInt(score[0]);
            int end = Integer.parseInt(score[1]);
            if (start < end){
                //solrQuery.addFacetField("sight_score:["+start+" TO "+end);
                solrQuery.addFacetQuery("sight_score:["+start+" TO "+end);
            }
        }




        //设置高亮
        /*solrQuery.setHighlight(true)
                .setHighlightSimplePre("<em>")
                .setHighlightSimplePost("</em>")
                .set("hl.fl","sight_intro");*/
        //设置facet
        solrQuery.setFacet(true)
                .setFacetMissing(true);
        //景点类型facet
        solrQuery.addFacetField(new String[]{"sight_type"});

        //评分范围facet
        solrQuery.add("facet.range","sight_score");

        solrQuery.add("f.sight_score.facet.range.start","1")
                .add("f.sight_score.facet.range.end","5.1")
                .add("f.sight_score.facet.range.gap","1");

        solrQuery.addFacetQuery("{!frange l=0 u=5}geodist()")
                .addFacetQuery("{!frange l=5.001 u=50}geodist()")
                .addFacetQuery("{!frange l=50.001 u=500}geodist()")
                .addFacetQuery("{!frange l=500.001 u=5000}geodist()");



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

            //整理facetFields数据
            List<SightFacetField> sightFacetFields = new ArrayList<SightFacetField>();
            List facetFields = queryResponse.getFacetFields();
            for (int i = 0; i < facetFields.size(); i++) {
                //取得每个facet的信息
                FacetField ff = (FacetField) facetFields.get(i);
                //System.out.println("name:"+ff.getName()+" valuecount:"+ff.getValueCount());

                SightFacetField sightFacetField = new SightFacetField(); //facet对象
                sightFacetField.setFacetName(ff.getName()); //设置facet的field名字
                List<SightFacetField.Item> items = new ArrayList<SightFacetField.Item>();
                //取得每个facet中的每个item
                List<FacetField.Count> countList =  ff.getValues();
                for (int j = 0; j < countList.size(); j++) {
                    FacetField.Count count = countList.get(j);
                    if (count.getCount() <= 0) continue; //facet中没有数据的,不加入结果集
                    //System.out.println("name:"+count.getName()+" count:"+count.getCount());
                    SightFacetField.Item item = new SightFacetField.Item(count.getName(),count.getCount());
                    items.add(item);
                }

                sightFacetField.setFacetCount(items.size()); //设置facet中的个数
                sightFacetField.setItems(items);   //设置facet中的items
                sightFacetFields.add(sightFacetField);  //添加到facet列表中
            }
            sightList.setSightFacetFields(sightFacetFields);

            //整理facetRanges数据
            List<SightFacetRange> sightFacetRanges = new ArrayList<SightFacetRange>();
            List<RangeFacet> facetRanges = queryResponse.getFacetRanges();
            for (int i = 0; i < facetRanges.size(); i++) {
                RangeFacet rf =  facetRanges.get(i);
                SightFacetRange sightFacetRange = new SightFacetRange();
                sightFacetRange.setFacetName(rf.getName());
                sightFacetRange.setStart((Float) rf.getStart());
                sightFacetRange.setEnd((Float) rf.getEnd());
                sightFacetRange.setGap((Float) rf.getGap());
                List items = new ArrayList<SightFacetRange.Item>();
                //取得每个facet中的每个item
                List<RangeFacet.Count> countList =  rf.getCounts();
                for (int j = 0; j < countList.size(); j++) {
                    RangeFacet.Count count = countList.get(j);
                    //System.out.println("range:"+count.getValue()+" "+count.getCount());
                    if (count.getCount() <= 0) continue; //facet中没有数据的,不加入结果集
                    SightFacetRange.Item item = new SightFacetRange.Item(count.getValue(),count.getCount());
                    items.add(item);
                }
                sightFacetRange.setFacetCount(items.size()); //设置facet中的个数
                sightFacetRange.setItems(items);   //设置facet中的items
                sightFacetRanges.add(sightFacetRange);
            }
            sightList.setSightFacetRanges(sightFacetRanges);

            //整理距离facet
            List<SightFacetQuery> facetDistanceList = new ArrayList<SightFacetQuery>();
            Map<String, Integer> facetQuery = queryResponse.getFacetQuery();

            Iterator iterator = facetQuery.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iterator.next();
                String query = entry.getKey();
                if (query.contains("geodist")){
                    SightFacetQuery facetDistance = new SightFacetQuery();
                    facetDistance.setQuery(query);

                    int lstart = query.indexOf("l=") +2;
                    int lend = query.indexOf(" ", lstart);
                    String from = query.substring(lstart,lend);

                    int ustart = query.indexOf("u=") +2;
                    int uend = query.indexOf("}", ustart);
                    String to = query.substring(ustart,uend);

                    facetDistance.setAlias((int)Float.parseFloat(from)+" - "+(int)Float.parseFloat(to));
                    facetDistance.setCount(entry.getValue());
                    facetDistanceList.add(facetDistance);
                }
            }
            sightList.setSightFacetDistances(facetDistanceList);
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
