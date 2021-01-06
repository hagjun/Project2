package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;

public class MemberDAO {

	Connection con;
	PreparedStatement psmt;
	ResultSet rs;

	//기본생성자를 통한 DB연결
	   public MemberDAO() {
	    String driver = "org.mariadb.jdbc.Driver";
	    String url = "jdbc:mariadb://127.0.0.1:3306/suamil_db";
	    try {
	        Class.forName(driver);
	        String id = "suamil_user";
	        String pw = "1234";
	        con = (Connection) DriverManager.getConnection(url, id, pw);
	        System.out.println("DB연결성공(디폴트생성자)");
	    }
	      
	    catch (Exception e) {
	        System.out.println("DB연결실패(디폴트생성자)");
	        e.printStackTrace();
	    }  
	}
	public String idFind(String name, String email){  //이름,이메일로 찾기
        
        
        String id=null ; //찾을아이디
        
        String sql="select id from membership where name=? and email=?";
        
        try {
      
	       psmt = con.prepareStatement(sql);
	       psmt.setString(1, name); //첫번째 ?를 스트링 id로 넣음
	       psmt.setString(2, email); //두번째 ?에 스트링 pw 넣
         
         rs=psmt.executeQuery(); //쿼리를 실행해서 결과값을 rs로 저장
         while(rs.next()){  //rs가 끝날때까지 반복
          id=rs.getString("id"); //cnt를 디비에서 가져온 cnt에 저장  
        }

    }
    catch(Exception e){
    	System.out.println(e);
    }
    return id;
}

	public MemberDAO(ServletContext ctx) {
		try {
			Class.forName(ctx.getInitParameter("MariaJDBCDriver"));
			String id = ctx.getInitParameter("MariaUser");
			String pw = ctx.getInitParameter("MariaPass");
			con = DriverManager.getConnection(ctx.getInitParameter("MariaConnectURL"), id, pw);
			System.out.println("DB 연결성공^^*");
		} 
		catch (Exception e) {
			System.out.println("DB 연결실패ㅜㅜ;");
			e.printStackTrace();
		}
	}

	public MemberDAO(String driver, String url, String id, String pw) {
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, id, pw);
			System.out.println("DB연결 성공(디폴트생성자)");
		} 
		catch (Exception e) {
			System.out.println("DB연결 실패(디폴트생성자)");
			e.printStackTrace();
		}
	}

	public Map<String, String> getMemberMap(String name, String email){
	      
	      //회원정보를 저장할 Map컬렉션 생성
	      Map<String, String> maps = new HashMap<String, String>();
	      
	      String query = "SELECT id, pass, name FROM"
	            + " member WHERE name=? AND email=?";
	      
	    try {
	        //prepare객체생성
	        psmt = con.prepareStatement(query);
	        //인파라미터 설정
	        psmt.setString(1, name);
	        psmt.setString(2, email);
	        //쿼리실행
	        rs = psmt.executeQuery();
	        //회원정보가 있다면 put()을 통해 정보를 저장한다.
	        if(rs.next()) {
	            maps.put("id", rs.getString("id"));
	            maps.put("pass", rs.getString("name"));
	            maps.put("name",rs.getString("email"));
	        }
	        else {
	            	System.out.println("결과셋이 없습니다.");
	        	}
	         
	    	}
	      
	    	catch (Exception e) {
	    		System.out.println("getMemberDTO오류");
	        		e.printStackTrace();
	      		}
	      
	      		return maps;
	   		}
			public void close() {
				try {
					// 연결을 해제하는 것이 아니고 풀에 다시 반납한다.
					if (rs != null)
						rs.close();
					if (psmt != null)
						psmt.close();
					if (con != null)
						con.close();
					} 
				catch (Exception e) {
					System.out.println("자원반납시 예외발생");
				}
			}
			
}