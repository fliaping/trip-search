package com.fliaping.trip.extracter.mysql;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBconnect{
	private final String DBDRIVER = "com.mysql.jdbc.Driver";
	private final String DBURL = "jdbc:mysql://localhost:3306/tripsearch?useUniCode=true&characterEncoding=UTF8";
	private final String DBUSER = "root";
	private final String DBPASSWD = "root";
	private Connection conn = null;
	public DBconnect(){
		try{
			Class.forName(DBDRIVER);
			conn = DriverManager.getConnection(DBURL,DBUSER,DBPASSWD);
		}catch(Exception e){
			e.printStackTrace();
		}
	} 

	public Connection getConnection(){
		return this.conn;
	}

	public void close(){
		try{
			this.conn.close();
		}catch(Exception e){

		}
		
	}
}