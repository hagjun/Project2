package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

public class MainDAO {
	
	Connection con;
	PreparedStatement psmt;
	ResultSet rs;
	
	//기본생성자에서 DBCP(커넥션풀)을 통해 DB연결
	public MainDAO() {
		
		try {
			Context initCtx = new InitialContext();
			Context ctx = (Context)initCtx.lookup("java:comp/env");
			DataSource source = (DataSource)ctx.lookup("jdbc_suamil_db");
			con = source.getConnection();
			System.out.println("DBCP연결성공");
			
		} catch (Exception e) {
			System.out.println("DBCP연결실패");
			e.printStackTrace();
		}
		
	}
	
	public MainDAO(ServletContext ctx) {
		try {
			Class.forName(ctx.getInitParameter("MariaJDBCDriver"));
			String id = ctx.getInitParameter("MariaUser");
			String pw = ctx.getInitParameter("MariaPass");
			con = DriverManager.getConnection(
					ctx.getInitParameter("MariaConnectURL"), id, pw);
			System.out.println("DB 연결성공^^*");
					
		} catch (Exception e) {
			System.out.println("DB 연결실패ㅜㅜ;");
			e.printStackTrace();
		}
	}
	
   public MainDAO(String driver, String url, String id, String pw) {
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
	
	public void close() {
		try {
			//연결을 해제하는 것이 아니고 풀에 다시 반납한다.
			if(rs!=null) rs.close();
			if(psmt!=null) psmt.close();
			if(con!=null) con.close();
		} catch (Exception e) {
			System.out.println("자원반납시 예외발생");
		}
	}
	
	//////////로그인방법3>DTO객체 대신 Map컬렉션에 회원정보를 저장후 반환한다.
   public Map<String, String> getMemberMap(String id, String pass){
	   
	   //회원정보를 저장할 Map컬렉션 생성
	   Map<String, String> maps = new HashMap<String, String>();
	   
	   String query = "SELECT id, pass, name FROM membership WHERE id=? AND pass=?";
	    //디버깅용
	    System.out.println(id+"<쿼리문실행후>"+pass);
	   
	   try {
			psmt = con.prepareStatement(query);
			psmt.setString(1, id);
			psmt.setString(2, pass);
			rs = psmt.executeQuery();
			
			//회원정보가 있다면 put()을 통해 정보를 저장한다.
			if(rs.next()) {
				maps.put("id", rs.getString(1));
				maps.put("pass", rs.getString("pass"));
				maps.put("name", rs.getString("name"));
			}
			else {
				System.out.println("결과셋이 없습니다.");
			}
	} catch (Exception e) {
		   System.out.println("getMemberDTO오류");
		   e.printStackTrace();
		}
	   return maps;
    }
	
//////////로그인방법3>DTO객체 대신 Map컬렉션에 회원정보를 저장후 반환한다.
public Map<String, String> adminMemberMap(String id, String pass){

//회원정보를 저장할 Map컬렉션 생성
Map<String, String> maps = new HashMap<String, String>();

String query = "SELECT id, pass, name, admin FROM membership WHERE id=? AND pass=?";
 //디버깅용
 System.out.println(id+"<쿼리문실행후>"+pass);

try {
		psmt = con.prepareStatement(query);
		psmt.setString(1, id);
		psmt.setString(2, pass);
		rs = psmt.executeQuery();
		
		//회원정보가 있다면 put()을 통해 정보를 저장한다.
		if(rs.next()) {
			maps.put("id", rs.getString(1));
			maps.put("pass", rs.getString("pass"));
			maps.put("name", rs.getString("name"));
			maps.put("admin", rs.getString("admin"));
		}
		else {
			System.out.println("결과셋이 없습니다.");
		}
} catch (Exception e) {
	   System.out.println("getMemberDTO오류");
	   e.printStackTrace();
	}
return maps;
}
	
	
//////////////////////////////////////아이디찾기/////////////////////////////////////////////////////	
   //회원정보를 저장후 반환한다.
   public String idSearch(String name, String email) { //이름,이메일로 찾기.
      
	  String user_id = null;
	   
      System.out.println(name+"<쿼리문실행전>"+email);
      
      String sql = "SELECT id, name FROM membership WHERE name=? and email=?";
      
       try {
          
         psmt = con.prepareStatement(sql); //쿼리.
         psmt.setString(1, name); //첫번째 ?를 스트링 name을 넣음.
         psmt.setString(2, email); //두번째 ?에 스트링 email을 넣음.
         
         rs = psmt.executeQuery(); //쿼리를 실행해서 결과값을 rs 로 저장
         
         //디버깅용
         System.out.println(name+"<쿼리문실행후>"+email);
         
         //회원정보가 있다면 put()을 통해 정보를 저장한다.
         if(rs.next()) { //rs가 끝날때까지 반복.
            
        	user_id = rs.getString("id");
        	 
         	}
         else {
        	System.out.println("결과셋이 없습니다.");
        }
    }
      
    catch(Exception e) {
        System.out.println("idSearchDAO오류");
        e.printStackTrace();
    }
      return  user_id;
}
   
////////////비밀번호찾기///////////////
   //회원정보를 저장후 반환한다.
   public String passSearch(String id, String name, String email) { //이름,이메일로 찾기.
	   
	   String user_pass = null;
	   
	   System.out.println(name+"<쿼리문실행전>"+email);
	   
	   String sql = "SELECT pass FROM membership WHERE id=? and name=? and email=?";
	   
	   try {
		   
		   psmt = con.prepareStatement(sql); //쿼리.
		   
		   psmt.setString(1, id); //첫번째 ?를 스트링 id을 넣음.
		   psmt.setString(2, name); //첫번째 ?를 스트링 name을 넣음.
		   psmt.setString(3, email); //두번째 ?에 스트링 email을 넣음.
		   
		   rs = psmt.executeQuery(); //쿼리를 실행해서 결과값을 rs 로 저장
		   
		   //디버깅용
		   System.out.println(name+"<쿼리문실행후>"+email);
		   
		   //회원정보가 있다면 put()을 통해 정보를 저장한다.
		   if(rs.next()) { //rs가 끝날때까지 반복.
			   
			   user_pass = rs.getString("pass");
		    }
		   else {
			   System.out.println("결과셋이 없습니다.");
		    }
	    }
	   catch(Exception e) {
		   System.out.println("passSearchDAO오류");
		   e.printStackTrace();
	    }
	    return  user_pass;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//회원가입시 id의 존재유무만 판단한다.
	public int idCheck(String id) {
		
		int res = 0;
	
		try {
			//쿼리문 작성
			String sql = "SELECT id FROM membership "
					+ " WHERE id=?";
			//prepare객체를 통해 쿼리문을 전송한다. 
			//생성자에서 연결정보를 저장한 커넥션 객체를 사용함.
			psmt = con.prepareStatement(sql);
			//쿼리문의 인파라미터 설정(DB의 인덱스는 1부터 시작)
			psmt.setString(1, id);
			//쿼리문 실행후 결과는 ResultSet객체를 통해 반환받음
			rs = psmt.executeQuery();
			//실행결과를 가져오기 위해 next()를 호출한다. 
			
			System.out.println("affected:"+res);
			//select절의 첫번째 결과값을 얻어오기위해 getInt()사용함.
			if(rs.next()) {
				//아이디가 중복되는경우 입력안되게구현...해야되.
				res = 1;
			}
			else {
				//아이디가 중복되지않을 때!!!
				res = 0;
			}
		}
		catch(Exception e) {
			//예외가 발생한다면 확인이 불가능하므로 무조건 false를 반환하여
			//아이디 입력 못하게 해야함.
			res = 1;
			e.printStackTrace();
		}
		return res;
	}
	
	
	//회원가입정보 삽입
	public int insertData(MainDTO dto) {
		int affected = 0; 

		try{
			String sql = "INSERT INTO membership ("
				+ " id,name,pass,tel,mobile,email,address,open_email,admin) "
				+ " VALUES ( "
				+ " ?,?,?,?,?,?,?,?,'F')";
			psmt = con.prepareStatement(sql);
			psmt.setString(1, dto.getId());
			psmt.setString(2, dto.getName());
			psmt.setString(3, dto.getPass());
			psmt.setString(4, dto.getTel());
			psmt.setString(5, dto.getMobile());
			psmt.setString(6, dto.getEmail());
			psmt.setString(7, dto.getAddress());
			psmt.setString(8, dto.getOpen_email());
						
			//insert성공시 1반환, 실패시 0반환
			affected = psmt.executeUpdate();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return affected;
	}
	
	   //게시판 리스트 + 페이지처리 + 회원 이름으로 검색기능 추가
	public List<MainDTO> selectListPageSearch(){
	    	
	    	List<MainDTO> bbs = new Vector<MainDTO>();
	    	
	    	//쿼리문이 아래와같이 페이지처리 쿼리문으로 변경됨.
	    	String query = " SELECT * FROM membership ";				    
					
	    	System.out.println("쿼리문:"+ query);
	    	
	    	try {
	    		psmt = con.prepareStatement(query);
	    		
	    	
	    		
	    		rs = psmt.executeQuery();
	    		
	    		while(rs.next()) {
	    			MainDTO dto = new MainDTO();
	    			
	    			dto.setName(rs.getString("name"));
	    			dto.setId(rs.getString("id"));
	    			dto.setMobile(rs.getString("mobile"));
	    			dto.setEmail(rs.getString("email"));
	    			
	    			bbs.add(dto);
	    		}
	    	}
	    	
	    	catch(Exception e) {
	    		System.out.println("Select시 예외발생");
	    		e.printStackTrace();
	    	}
	    	return bbs;
	    }
	    
}
