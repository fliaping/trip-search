package com.fliaping.trip.search;

import org.apache.solr.client.solrj.impl.HttpSolrClient;

/**
 * Created by Payne on 4/5/16.
 */
public class MySolrClient {
    private static final String urlString = "http://localhost:8983/solr/trip";
    private static HttpSolrClient solrClient ;
    private static MySolrClient mySolrClient ;
    private MySolrClient(){}

    public static synchronized MySolrClient getInstance(){
        if (mySolrClient == null) mySolrClient = new MySolrClient();
        return mySolrClient;
    }
    public static HttpSolrClient getSolrClient(){
        if (solrClient == null){
            solrClient = new HttpSolrClient(urlString);
            solrClient.setSoTimeout(1000); // socket read timeout
            solrClient.setAllowCompression(true);  // allow Compression
        }
        return solrClient;
    }
}
