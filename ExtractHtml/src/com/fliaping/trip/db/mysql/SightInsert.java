package com.fliaping.trip.db.mysql;

import com.fliaping.trip.Sight;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

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
        String sql = "INSERT INTO tripsearch.sight " +
                "(sight_id, sight_name, sight_score_ctrip, sight_intro," +
                " sight_coordinate, sight_type,pageurl, sight_place_1, sight_place_2," +
                " sight_place_3,sight_place_4,sight_place_5) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";
        try{
            pst = conn.prepareStatement(sql);
            pst.setInt(1,sight.getId());
            pst.setString(2,sight.getName());
            pst.setFloat(3,sight.getScore_ctrip());
            pst.setString(4,sight.getIntro());
            pst.setString(5,sight.getCoordinate());
            pst.setString(6,sight.getType());
            pst.setString(7,sight.getPageUrl());
            int index = 8;
            for (int i = 0; index <= 12; i++) {
                if(i < sight.getPlace().length){
                    pst.setString(index++,sight.getPlace()[i]);
                }else {
                    pst.setString(index++,null);
                }
            }

            result = pst.executeUpdate();

        }catch(Exception e){
            //捕获主键重复异常
            result = e instanceof MySQLIntegrityConstraintViolationException ?  -1 : 0;
            if (result != -1){  //别的异常,要打出
                e.printStackTrace();
            }

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
