package chatAnmy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientAnmy 
{
	public static void main(String[] args) throws IOException 
	{
//		Socket s = new Socket("10.63.45.197",12222);
		Socket s=new Socket("127.0.0.1",8088);
		
		while(true)
		{
			try {
				//读入Socket流中Server发送的信息
				BufferedReader bufr = 
						new BufferedReader(new InputStreamReader(s.getInputStream()));
				//读取键盘录入信息
				BufferedReader bufIn =
						new BufferedReader(new InputStreamReader(System.in));
				//输出流，输入到Socket中，发送给Server
				PrintWriter pout = new PrintWriter(s.getOutputStream(),true);
				
				String line = bufIn.readLine();//读取键盘录入信息
				pout.println(line);//输出键盘录入信息到Socket输出缓冲流中
				if(line.equalsIgnoreCase("bye"))
				{
					break;
				}
				
				String msg = bufr.readLine();//接收Server发送的消息
				System.out.println("From Server:"+msg);//打印Server发送的消息到控制台
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			} 
		}
		//s.close();
	}
}
