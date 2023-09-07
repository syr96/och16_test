package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;


// DBCP + Singleton
public class BoardDao {
	private static BoardDao instance;
	private BoardDao() {
	}
	public static BoardDao getInstance() {
		if(instance == null) { // 왜 != 로 했지?? ㅡㅡ 정신차리렴
			instance = new BoardDao();
		}
		return instance;
	}
	private Connection getConnection() {
		Connection conn = null;
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/OracleDB");
			conn = ds.getConnection();
		}catch(Exception e) { 
			System.out.println(e.getMessage());	
		}
		return conn;
	}
	
	public int getTotalCnt() throws SQLException {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select count(*) from board";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()) result = rs.getInt(1);
			System.out.println("result->"+result);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(rs != null) rs.close();
			if(pstmt != null) pstmt.close();
			if(conn != null) conn.close();
		}
		
		return result;
	}
	
	public List<Board> boardList(int startRow, int endRow) throws SQLException {
		List<Board> list = new ArrayList<Board>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select * from "
					+ "(select rownum rn, a.* from "
						+ "(select * from board order by ref desc, re_step) a)"
					+ "where rn between ? and ?";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, endRow);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				do {
					Board board = new Board();
					board.setNum(rs.getInt("num"));
					board.setWriter(rs.getString("writer"));
					board.setSubject(rs.getString("subject"));
					board.setContent(rs.getString("content"));
					board.setEmail(rs.getString("email"));
					board.setReadcount(rs.getInt("readcount"));
					board.setPasswd(rs.getString("passwd"));
					board.setRef(rs.getInt("ref"));
					board.setRe_step(rs.getInt("re_step"));
					board.setRe_level(rs.getInt("re_level"));
					board.setIp(rs.getString("ip"));
					board.setReg_date(rs.getDate("reg_date"));
					list.add(board);
				} while(rs.next());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(rs != null) rs.close();
			if(pstmt != null) pstmt.close();
			if(conn != null) conn.close();
		}
		return list;
	}
	
	public void readCount(int num) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "update board set readcount=readcount+1 where num=?";
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(pstmt != null) pstmt.close();
			if(conn != null) conn.close();
		}
		// void로 잡으면 return은 안해줘도 된다
	}
	
	public Board select(int num) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select * from board where num=?";
		Board board = new Board();
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				board.setNum(rs.getInt("num"));
				board.setWriter(rs.getString("writer"));
				board.setSubject(rs.getString("subject"));
				board.setContent(rs.getString("content"));
				board.setEmail(rs.getString("email"));
				board.setReadcount(rs.getInt("readcount"));
				board.setIp(rs.getString("ip"));
				board.setReg_date(rs.getDate("reg_date"));
				board.setRef(rs.getInt("ref"));
				board.setRe_level(rs.getInt("re_level"));
				board.setRe_step(rs.getInt("re_step"));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(rs != null) rs.close();
			if(pstmt != null) pstmt.close();
			if(conn != null) conn.close();
		}
		return board;
	}
	
	public int insert(Board board) throws SQLException {
		int result = 0;
		int num = board.getNum(); // = 0 -> 신규 글
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String sqlMaxNum = "select nvl(max(num), 0) from board";
		String sql = "insert into board values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate)";
		// num, writer, subject, content, email, readcount, passwd, ref, re_step, re_level, ip, reg_date
		
		String sql2 = "update board set re_step = re_step + 1 where ref = ? and re_step > ?";
		// 첫 답글일 경우, sql2는 실행되지 않는다
		// 0 > 0 -> false
		// 두번째 답글을 작성할 경우, re_step이 증가(나중 글이기 때문에)해야하기 때문에 1 증가 후 업데이트 해준다
		// 나와 같은 댓글 group & 내가 댓글을 다는 항목보다 큰 re_step을 하나씩 증가

		try {
			conn = getConnection();
			
			pstmt = conn.prepareStatement(sqlMaxNum); // DB에서 num을 가져온다
			rs = pstmt.executeQuery();
			rs.next();
			// key인 num이 1씩 증가(mySql: auto_increment 또는 oracle sequence)
			// sequence를 사용 : values(시퀀스명(board_seq).nextval, ?, ?, ...)
			// sequence를 사용해야 오류가 적어진다
			
			int number = rs.getInt(1) + 1; // 가져온 num에 1 증가
			rs.close();
			pstmt.close();
			// num max값 + 1을 하기위한 로직
			
			// 댓글 -> sql2
			if(num != 0) {
				System.out.println("BoardDAO insert 댓글 sql2 -> " + sql2);
				System.out.println("BoardDAO insert 댓글 board.getRef() -> " + board.getRef());
				System.out.println("BoardDAO insert 댓글 board.getRe_step() -> " + board.getRe_step());
				pstmt = conn.prepareStatement(sql2);
				pstmt.setInt(1, board.getRef());
				pstmt.setInt(2, board.getRe_step());
				pstmt.executeUpdate();
				pstmt.close();
				
				// 댓글 관련 정보
				// 새로 작성한 댓글일 경우에는 원글에 1을 증가해야한다
				board.setRe_step(board.getRe_step() + 1);
				board.setRe_level(board.getRe_level() + 1);
			}
			
			if(num == 0) board.setRef(number);
			
			// 신규 / 댓글
			// ref  -> num Setting
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, number);
			pstmt.setString(2, board.getWriter());
			pstmt.setString(3, board.getSubject());
			pstmt.setString(4, board.getContent());
			pstmt.setString(5, board.getEmail());
			pstmt.setInt(6, board.getReadcount());
			pstmt.setString(7, board.getPasswd());
			pstmt.setInt(8, board.getRef());
			pstmt.setInt(9, board.getRe_step());
			pstmt.setInt(10, board.getRe_level());
			pstmt.setString(11, board.getIp());
			result = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(rs != null) rs.close();
			if(pstmt != null) pstmt.close();
			if(conn != null) conn.close();
		}
		return result;
	}
	
	public int update(Board board) throws SQLException {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "update board set subject=?, writer=?, email=?, passwd=?, content=? WHERE num=?";
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, board.getSubject());
			pstmt.setString(2, board.getWriter());
			pstmt.setString(3, board.getEmail());
			pstmt.setString(4, board.getPasswd());
			pstmt.setString(5, board.getContent());
			pstmt.setInt(6, board.getNum());
			result = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(pstmt != null) pstmt.close();
			if(conn != null) conn.close();
		}
		return result;
	}
	
	public int delete(int num, String passwd) throws SQLException {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "delete from board where num=? and passwd=?";

		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setString(2, passwd);
			result = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(pstmt != null) pstmt.close();
			if(conn != null) conn.close();
		}
		return result;
	}
}
