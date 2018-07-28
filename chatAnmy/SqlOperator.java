package chatAnmy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.ResultSet;

public class SqlOperator {

	protected static String dbClassName = "com.mysql.jdbc.Driver";
	protected static String dbUrl = "jdbc:mysql://localhost:3306/db_IoT2017?characterEncoding=utf8";
//	protected static String dbUrl = "jdbc:mysql://115.157.200.88:3306/iot_2017?characterEncoding=utf8";
	protected static String dbUser = "root";
	protected static String dbPwd = "1";
	private static Connection conn = null;
	
	private SqlOperator()
	{
		if (conn == null)
		{
			try {
				Class.forName(dbClassName).newInstance();
				conn = DriverManager.getConnection(dbUrl, dbUser, dbPwd);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void close()
	{
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("conn close failed.");
		}
		conn = null;
	}
	
	private static ResultSet excuteResult(String sql)
	{
		if(conn == null)
		{
			new SqlOperator();
		}
		try {
			return (ResultSet) conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(sql);
		} catch (SQLException e) {
			return null;
		}
	}
	
	private static ResultSet executeQuery(String sql) {
		try {
			if(conn==null)
			new SqlOperator();
			return (ResultSet) conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE).executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
		}
	}
	
	public static int excuteUpdate(String sql)
	{
		try {
			if(conn == null)
			{
				new SqlOperator();
			}
			return conn.createStatement().executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Update sql failed..");
		}
		return -1;
	}
	
	public static int insertSensorInfo(String sensorId, String sensorType, String status, String value1, String value2, String value3, String recordTime, String remark)
	{
		String sql = "";
		if(value1=="" || ("").equals(value1))
		{
			System.out.println("No value..please check.");
		}
		else if(value2=="" || ("").equals(value2))
		{
			sql = "insert into SENSOR_COL(sensorId,sensorType,status,value1,recordTime,remark) values ('"+sensorId+"','"+sensorType+"','"+status+"','"+Float.parseFloat(value1)+"','"+recordTime+"','"+remark+"' "+")";
		}
		else if(value3=="" || ("").equals(value3))
		{
			sql = "insert into SENSOR_COL(sensorId,sensorType,status,value1,value2,recordTime,remark) values ('"+sensorId+"','"+sensorType+"','"+status+"','"+Float.parseFloat(value1)+"','"+Float.parseFloat(value2)+"','"+recordTime+"','"+remark+"' "+")";
		}
		else
		{
			sql = "insert into SENSOR_COL(sensorId,sensorType,status,value1,value2,value3,recordTime,remark) values ('"+sensorId+"','"+sensorType+"','"+status+"','"+Float.parseFloat(value1)+"','"+Float.parseFloat(value2)+"', '"+Float.parseFloat(value3)+"', '"+recordTime+"','"+remark+"' "+")";
		}
		int i = SqlOperator.excuteUpdate(sql);
		if(i>0)
		{
			return i;
		}
		return -1;
	}
	
	public static List searchEntityInfo(String sensorId)
	{
		List list=new ArrayList();
		String sql = "";
		if(sensorId != ""&& !sensorId.equals(null))
		{
			sql = "select si.entityId, ei.entityStatus, ei.userId from SENSOR_INFO si left join ENTITY_INFO ei on si.entityId = ei.entityId where si.sensorId='"+sensorId+"'";
		}
		
		ResultSet rs = SqlOperator.executeQuery(sql);
		try {
			while (rs.next()) {
				SensorModel sm = new SensorModel();
				sm.setEntityId(rs.getString("entityId"));
				sm.setEnyStatus(rs.getString("entityStatus"));
				sm.setUserId(rs.getString("userId"));
				list.add(sm);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		SqlOperator.close();
		return list;
	}
	
	public static int updateEntity(String entityId, String status,String statusType)
	{
		int i = 0;
		String sql = "update entity_info set entityStatus = '"+status+"' , statusType = '"+statusType+"' where entityId = '"+entityId+"'";
		i = SqlOperator.excuteUpdate(sql);
		return i;
	}
	
	public static int insertMainInfo(String entityId, String status,String userId,String cause,String recordTime)
	{
		int i = 0;
		String sql = "insert into maintain_info(entityId,status,userId,cause,recordTime) values ('"+entityId+"','"+status+"','"+userId+"','"+cause+"','"+recordTime+"' "+")";
		i = SqlOperator.excuteUpdate(sql);
		return i;
	}
	
	public static int updateMainInfo(String entityId, String status,String cause,String recordTime)
	{
		int i = 0;
		String sql = "update maintain_info set status = '"+status+"' , cause = '"+cause+"' , recordTime = '"+recordTime+"' where entityId in (select a.entityId from (select entityId from MAINTAIN_INFO where entityId='"+entityId+"' order by recordTime desc limit 1) a)";
		i = SqlOperator.excuteUpdate(sql);
		return i;
	}
}
