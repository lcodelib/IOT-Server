package CoAPServer;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ConSqLite {
	
	private static Logger logger = Logger.getLogger("SqlLog");
	
	private Connection SqlCon = null;
	private Statement stmt = null;
	
	public ConSqLite(String DBname){//连接数据库
		try {
		      Class.forName("org.sqlite.JDBC");
		      SqlCon = DriverManager.getConnection("jdbc:sqlite:"+DBname+".db");//连接数据库，不存在则打开
			  stmt = SqlCon.createStatement();
		    } catch ( Exception e ) {
			  logger.severe("Error:can't not connect the database!");
		      System.exit(0);//退出
		    }
		logger.info("Message:Database connected.");
	}
	public void ExecSql(String sqlcmd){//执行sql
		try {
		    stmt.executeUpdate(sqlcmd);
		} catch (SQLException e) {
			if(!(e.getErrorCode()==0)){
				logger.severe("Error:can't not Exec the cmd!");
			}
		}
	}
	public ArrayList<String> ExecSelect(String sqlcmd,int data_len){//查询sql
		ArrayList<String> list = new ArrayList<String>();
		try {
			ResultSet rs = stmt.executeQuery(sqlcmd);
			for (int i = 0; i < data_len; i++) {  
				list.add(rs.getString("DATA")); 
				rs.next();
			}
		    rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.warning("Error:Failed to get data from database!\n"+e.getMessage());
		}
		return list;
	}
	public void CloseExec(){//关闭执行
		try {
			stmt.close();
		} catch (SQLException e) {
			logger.warning("Error:Failed to close Exec connect!\n"+e.getMessage());
		}
	}
	public void CloseCon(){//关闭连接
		try {
			SqlCon.close(); 
		} catch (SQLException e) {
			logger.warning("Error:Failed to close database connect!\n"+e.getMessage());
		}
	}
}
