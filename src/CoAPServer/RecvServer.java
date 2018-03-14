package CoAPServer;

import java.util.logging.Logger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class RecvServer {
	
	private static Logger logger = Logger.getLogger("RecvLog");
	
	private String DBname = "IOT"; //数据库名
	private String sqlcmd = "INSERT INTO MESSAGE VALUES (null,'";//插入命令
	CoapServer CoServer;
	
	public RecvServer(){
		 CoServer = new CoapServer();//创建CoAP服务
	     CoServer.add(new CoapResource("t").add(new CoapResource("r"){//服务器资源/t/r
	     		@Override
	     		public void handlePOST(CoapExchange exchange) { //POST接收线程
							Request request = exchange.advanced().getRequest();//获取返回结果
							String IMEI = request.getURI().substring(request.getURI().indexOf("=")+1);//取IMEI
							logger.info("Message:IMEI-"+IMEI+"-Connected.");//显示设备连接
							exchange.respond(ResponseCode.CONTENT, "OK");
							ConSqLite con = new ConSqLite(DBname);
							CoapClient client = createClient(request.getSource().toString().replace("/","")+":"+request.getSourcePort()+"/t/d");//获取源地址
							client.observe(//开启订阅，订阅NB客户端s
									new CoapHandler() {
										public void onLoad(CoapResponse response) {//加载资源时被调用
											String data = response.getResponseText();//获取客户端数据结果
											if(!data.equals("")){//是否为空
												con.ExecSql(sqlcmd+IMEI+"','"+data+"')");//执行SQL语句
												con.CloseExec();;//关闭执行
											}
										}
										public void onError() {
											logger.info("Message:IMEI-"+IMEI+"-Disconnect.");
											con.CloseCon();
											//超时或出错
										}
							});                
					}  
			}));  	
	}
	public void Start(){
		CoServer.start();  //开启服务
	}
}
