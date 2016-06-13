package com.fliaping.trip.extracter.mysql;



import com.fliaping.trip.extracter.Sight;
import com.fliaping.trip.extracter.Utils;
import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by Payne on 3/18/16.
 */
public class SightInsert {

    private String tableName = "";
    private PreparedStatement pst = null;
    private DBconnect dbc = null;
    private Connection conn = null;
    public SightInsert(){
        dbc = new DBconnect();
        conn = dbc.getConnection();
        if (conn == null)
            System.out.println("conn is null");
    }

    public int insert(Sight sight){
        int result = 0 ; //未知异常,-1为主键重复异常,大于0为成功
        String sql = "INSERT INTO tripsearch.sights " +
                "(site_id, name, images, score," +
                " intro, address,comments, comment_num, near_hotels," +
                " tickets,traffic,coordinate,types,place,pageurl) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try{
            pst = conn.prepareStatement(sql);
            pst.setInt(1,sight.getSite_id());
            pst.setString(2,sight.getName());
            pst.setString(3, Utils.arrayToString(sight.getImages()));
            pst.setFloat(4,sight.getScore());
            pst.setString(5,sight.getIntro());
            pst.setString(6,sight.getAddress());
            pst.setString(7,Utils.arrayToString(sight.getComments()));
            pst.setInt(8,sight.getComment_num());
            pst.setString(9,Utils.arrayToString(sight.getNear_hotels()));
            pst.setString(10, Utils.arrayToString(sight.getTickets()));
            pst.setString(11,sight.getTraffic());
            pst.setString(12,sight.getCoordinate());
            pst.setString(13,Utils.arrayToString(sight.getTypes()));
            pst.setString(14,Utils.arrayToString(sight.getPlace()));
            pst.setString(15,sight.getPageurl());


            result = pst.executeUpdate();

        }catch(Exception e){
            //捕获主键重复异常
            /*result = e instanceof MySQLIntegrityConstraintViolationException ?  -1 : 0;
            if (result != -1){  //别的异常,要打出*/
                e.printStackTrace();


        }
        return result;
    }





    @Override
    protected void finalize() throws Throwable {
        if(dbc!=null){
            dbc.close();
        }
        super.finalize();
    }
}
