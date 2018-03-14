package CoAPServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONObject;

public class DataPush implements HttpHandler{
	
	private static Logger logger = Logger.getLogger("PushLog");
	
	private int MaxPush = 5; //最大推送条数
	private String DBname = "IOT";//数据库名
	private String sqlcmd = "SELECT * FROM 'MESSAGE' WHERE IMEI='"; //查询
    @Override
    public void handle(HttpExchange exchange) {
        new Thread(new Runnable() {//并发
            @Override
            public void run() {
                try{
                    String queryString =  exchange.getRequestURI().getQuery();
                    Map<String,String> queryStringInfo = formData2Dic(queryString);//获取GET请求
                    String IMEI = queryStringInfo.get("IMEI");//得到IMEI
                    ConSqLite SqlCon = new ConSqLite(DBname);//连接数据库
                    ArrayList<String> result = SqlCon.ExecSelect(sqlcmd+IMEI+"'"+" ORDER BY ID DESC LIMIT "+MaxPush,MaxPush);//查询
                    JSONObject json = new JSONObject();//将结果转JSON
                    json.put(IMEI, result);
                    exchange.sendResponseHeaders(200,0);//返回响应OK,200
                    OutputStream os = exchange.getResponseBody();
                    os.write(json.toString().getBytes());//回复推送
                    os.close();
                }catch (IOException e) {
                	logger.severe("Error:Failed to get data from database and push!\n"+e.getMessage());
                } catch (Exception e) {
                	logger.severe("Error:Failed change to JSON!\n"+e.getMessage());
                }
            }
        }).start();
    }
    
	public static Map<String,String> formData2Dic(String formData ) {//格式转换
	    Map<String,String> result = new HashMap<>();
	    if(formData== null || formData.trim().length() == 0) {
	        return result;
	    }
	    final String[] items = formData.split("&");
	    Arrays.stream(items).forEach(item ->{
	        final String[] keyAndVal = item.split("=");
	        if( keyAndVal.length == 2) {
	            try{
	                final String key = URLDecoder.decode( keyAndVal[0],"utf8");
	                final String val = URLDecoder.decode( keyAndVal[1],"utf8");
	                result.put(key,val);
	            }catch (UnsupportedEncodingException e) {
	            	logger.warning("Error:Failed to change this!\n"+e.getMessage());
	            }
	        }
	    });
	    return result;
	}
}