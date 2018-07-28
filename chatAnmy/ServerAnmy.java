package chatAnmy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import chatAnmy.SqlOperator;

public class ServerAnmy
{
	public static void main(String[] args) throws IOException 
	{
		//构造ServerSocket实例，指定端口监听客户端的连接请求
		@SuppressWarnings("resource")
		ServerSocket ss = new ServerSocket(8088);
		//建立跟客户端的连接 
		Socket s = ss.accept();
		String ip=s.getInetAddress().getHostAddress();
		System.out.println(ip+"..connected.");
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	      
		while(true)
		{
			try 
			{
				//读入从Client发送的Socket流中信息
				BufferedReader bufr = 
						new BufferedReader(new InputStreamReader(s.getInputStream()));
				System.out.println("receive...from.."+bufr.toString());
				//读取键盘录入信息
//				BufferedReader bufIn =
//						new BufferedReader(new InputStreamReader(System.in));
				
				//输出流，输入到Socket中，发送给Client
				PrintWriter pout = 
						new PrintWriter(s.getOutputStream(),true);
				
				String line = bufr.readLine();
				System.out.println("From client:"+line);
				String curDate = df.format(new Date());
			    System.out.println("The current system time is: "+curDate);// new Date()为获取当前系统时间
				
//			    DataInputStream dis = new DataInputStream(s.getInputStream());
//			    DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			    
//			    String clientMsg = dis.readUTF();
//			    System.out.println("客户端的消息是："+clientMsg);
			    
				String paraArray[] = null; 
				String snId="";
				String snType="";
				String snValue1="";
				String snValue2="";
				String snValue3="";
				String status="";
				String remark="";
				String statusType="";
				
				if(line!=null && !line.startsWith("xV") && line.contains(";") && line.contains(","))
				{
					line = line.substring(0,line.length()-1);
//					System.out.println("receive data is :"+line);
					paraArray = line.split(",");
					// Ultrasonic sensor
					if(paraArray[0].startsWith("U") && paraArray.length==3)
					{
						snId = paraArray[0];
						snType = "超声波传感器";
						snValue1 = paraArray[1];//Distance value
						snValue2 = paraArray[2];//Temperature value
						
						if(Float.parseFloat(snValue1) > 400 && Float.parseFloat(snValue2) < 60) //The distance less than 600mm and tpt less than 60 means normal
						{
							status = "正常";
						}
						else if(Float.parseFloat(snValue1) >= 300 && Float.parseFloat(snValue1) <= 400)
						{
							status = "预警";
							statusType = "垃圾桶满溢";
						}
						else if(Float.parseFloat(snValue1) <= 300)
						{
							status = "报警";
							statusType ="垃圾桶满溢";
						}
						
						if(Float.parseFloat(snValue2) >= 60 && Float.parseFloat(snValue2) <= 70) //Temperature more than 70
						{
							status = "预警";
							statusType ="垃圾桶高温";
						}
						else if(Float.parseFloat(snValue2) >70)
						{
							status = "报警";
							statusType ="垃圾桶高温";
						}
					}
					
					//Light sensor--not ready
					else if(paraArray[0].startsWith("L"))
					{
						snId = paraArray[0];
						snType= "光照传感器";
						snValue1 = paraArray[1];
						
						if(Float.parseFloat(snValue1) < 180 )
						{
							status = "正常";
						}
						else if(Float.parseFloat(snValue1) >= 180 && Float.parseFloat(snValue1) <= 280) 
						{
							status = "预警";
							statusType ="井盖光照";
						}
						else
						{
							status = "报警";
							statusType ="井盖光照";
						}
					}
					//Angel sensor
					else if(paraArray[0].startsWith("A"))
					{
						snId = paraArray[0];
						snType="倾角传感器";
						snId = paraArray[0];
						snValue1 = paraArray[1];
						snValue2 = paraArray[2];
						snValue3 = paraArray[3];
						
						//if(Float.parseFloat(snValue1)< 20 && Float.parseFloat(snValue2) < 20 && Float.parseFloat(snValue3) < 20) 
						if(Float.parseFloat(snValue2) < 20 && Float.parseFloat(snValue3) < 20) 
						{
							status = "正常";
						}
//						else if((Float.parseFloat(snValue1) >= 20&&Float.parseFloat(snValue1) <= 30) || (Float.parseFloat(snValue2)>20&&Float.parseFloat(snValue2)<=30) || (Float.parseFloat(snValue3) > 20&&Float.parseFloat(snValue3) <= 30))
						else if((Float.parseFloat(snValue2)>20&&Float.parseFloat(snValue2)<=30) || (Float.parseFloat(snValue3) > 20&&Float.parseFloat(snValue3) <= 30))
						{
							if(paraArray[0].startsWith("A1"))
							{
								status = "预警";
								statusType ="古树倾角";
							}
							else if(paraArray[0].startsWith("A2"))
							{
								status = "预警";
								statusType ="井盖倾角";
							}
						}
						else if(Float.parseFloat(snValue2)>30 || Float.parseFloat(snValue3) > 30)
						{
							if(paraArray[0].startsWith("A1"))
							{
								status = "报警";
								statusType ="古树倾角";
							}
							else if(paraArray[0].startsWith("A2"))
							{
								status = "报警";
								statusType ="井盖倾角";
							}
						}
					}
					//Water level sensor
					else if(paraArray[0].startsWith("W"))
					{
						snId = paraArray[0];
						snType="水位传感器";
						snId = paraArray[0];
						snValue1 = paraArray[1];
						if(Float.parseFloat(snValue1) < 150 ) 
						{
							status = "正常";
						}
						else if(Float.parseFloat(snValue1) >= 150 && Float.parseFloat(snValue1) <= 200)
						{
							status = "预警";
							statusType ="水位高预警";
						}
						else if(Float.parseFloat(snValue1) > 200)
						{
							status = "报警";
							statusType ="水位高报警";
						}
					}
					
					pout.println("server receive success");
					
					int insert = SqlOperator.insertSensorInfo(snId, snType, status, snValue1, snValue2, snValue3, curDate, remark);
					if(insert >0)
					{
						System.out.println("insert sensor_info successful...");
					}
					else
					{
						System.out.println("insert sensor_info failed...");
					}
					
					String entityId = "";
					List list = SqlOperator.searchEntityInfo(snId);
				
				   if(list != null && list.size()>0)//更新或者插入处理记录到maintain_info表
				   {
					   SensorModel sm = (SensorModel) list.get(0);
					   entityId = sm.getEntityId();
					   String entityStatus=sm.getEnyStatus();
					   String userId = sm.getUserId();
					  
					   System.out.println("entityId and userId are:--"+entityId+"----"+userId);
					   System.out.println("entityStatus is:"+entityStatus);
					   System.out.println("status is:"+status);
					   int insertDealInfo = 0;
					   int updateDealInfo = 0;
					   
					   if( ((entityStatus=="正常"|| entityStatus.equals("正常")) && (status=="预警" || status.equals("预警"))) || ((entityStatus=="正常"|| entityStatus.equals("正常")) && ((status=="报警") || (status.equals("报警")))) || ((entityStatus=="报警" || entityStatus.equals("报警")) && ((status=="预警") || (status.equals("预警")))) )
					   {
						   System.out.println("####start to insert####");
						   insertDealInfo = SqlOperator.insertMainInfo(entityId, status,userId,statusType,curDate);
						   if(insertDealInfo>0)
						   {
							   System.out.println("insert maintain info success..");
						   }
						   else
						   {
							   System.out.println("insert maintain info failed..");
						   }
					   }
					   
					   if((entityStatus=="预警" || entityStatus.equals("预警")) && (status=="报警" || status.equals("报警")))
					   {
						   System.out.println("---start to update####");
						   updateDealInfo = SqlOperator.updateMainInfo(entityId, status,statusType,curDate);
						   if(updateDealInfo>0)
						   {
							   System.out.println("update maintain info success..");
						   }
						   else
						   {
							   System.out.println("update maintain info failed..");
						   }
					   }
					   
					    int update = SqlOperator.updateEntity(entityId, status,statusType);
						if(update > 0)
						{
							System.out.println("update entity_info successful...");
						}
						else
						{
							System.out.println("update entity_info fail...");
						}
					   
				   }
				   else
				   {
					   System.out.println("entity no status");
				   }
				}
				else
				{
					System.out.println("Please check the data format!");
					pout.println("receive data format wrong");
				}
				
				if(line.equals("bye")||line.equals(null))
				{
					break;
				}
//				pout.println(bufIn.readLine());//输出键盘录入到Socket输入流中，发送给Client
				
			} 
			catch (IOException e) 
			{
				throw new RuntimeException(ip+" Connection failed");
			}
		}
		//ss.close();
		//s.close();
	}
}
