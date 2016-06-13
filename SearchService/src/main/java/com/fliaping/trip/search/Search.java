package com.fliaping.trip.search;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by Payne on 4/2/16.
 *
 */
public class Search extends HttpServlet{
    private Map<String, String[]> urlMap;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //resp.getWriter().println("hello");

        DoQuery query = new DoQuery(req,resp);
        String query_type = req.getParameter(UrlP.query_type.name());

        if (!Setting.contains(query_type, UrlP.query_type))
            query_type = QueryType.near.get(); //没有定义的query_type方法,设置默认


        if (QueryType.key.is(query_type)){    //关键词查询
            query.keyQuery();
        }else if (QueryType.map.is(query_type)){  //地图查询
            query.mapQuery();
        }else {      //附近查询
            query.nearQuery();
        }


    }

}
