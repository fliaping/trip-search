package com.fliaping.trip.search;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

/**
 * Created by Payne on 4/5/16.
 */
public class MySolrClient {
    private static final String urlString = "192.168.0.2:2181,192.168.0.3:2181,192.168.0.4:2181";
    private static CloudSolrClient solrClient ;
    private static MySolrClient mySolrClient ;
    private MySolrClient(){}

    public static synchronized MySolrClient getInstance(){
        if (mySolrClient == null) mySolrClient = new MySolrClient();
        return mySolrClient;
    }
    public static SolrClient getSolrClient(){
        if (solrClient == null){
            solrClient = new CloudSolrClient(urlString);
            solrClient.setDefaultCollection("trip");
        }
        return solrClient;
    }
}
