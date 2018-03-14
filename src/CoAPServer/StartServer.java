/*----------------
CoAP端口默认为5683
CoAP安全模式为5684
数据库是SQLite
HTTP端口8080（可修改）
数据为JSON格式
NB设备注册发送过数据，IMEI码自动记录

代码简单实现了模拟电信IOT平台实现CoAP对接
能够接受NB设备上报数据到服务器
其它设备访问服务器HTTP端口即可获取NB设备上报的数据
访问例子：
http://服务器IP:8080/GetData?IMEI=XXXXX
-------------------

Power By MaxPowell
-----------------*/

package CoAPServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpServer;

public class StartServer {
	
	private static Logger logger = Logger.getLogger("StartLog");
	
	private static String DBname = "IOT";//数据库名
	private static String sqlcmd = "CREATE TABLE MESSAGE(ID INTEGER PRIMARY KEY AUTOINCREMENT,IMEI TEXT NOT NULL,DATA TEXT NOT NULL)";//建表
    public static void main(String[] args) {
    	InitSql();
    	Recv();
    	Push();
    }
    public static void InitSql(){//初始化数据库
    	ConSqLite con = new ConSqLite(DBname);
    	con.ExecSql(sqlcmd);
    	con.CloseExec();
    	con.CloseCon();
    }
    public static void Recv(){//接收NB终端服务
    	RecvServer recv = new RecvServer();
    	recv.Start();
    }
    public static void Push(){//HTTP数据推送
    	  HttpServer server;
    	  try {
    		  server = HttpServer.create(new InetSocketAddress(8080), 0);//设定端口
		      server.createContext("/GetData", new DataPush());//设备服务名
		      server.start();//开启
    	  } catch (IOException e) {
    		  logger.severe("Error:Failed to create the HTTP Server!\n"+e.getMessage());
    	  }
    }

}
