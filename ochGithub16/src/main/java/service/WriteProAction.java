package service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.Board;
import dao.BoardDao;

public class WriteProAction implements CommandProcess {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			request.setCharacterEncoding("utf-8");
			
			// 1. num , pageNum, writer ,  email , subject , passwd , content   Get
			String pageNum = request.getParameter("pageNum");
			int num = Integer.parseInt(request.getParameter("num"));
			String writer = request.getParameter("writer");
			String email = request.getParameter("email");
			String subject = request.getParameter("subject");
			String passwd = request.getParameter("passwd");
			String content = request.getParameter("content");
			int ref = Integer.parseInt(request.getParameter("ref"));
			int re_level = Integer.parseInt(request.getParameter("re_level"));
			int re_step = Integer.parseInt(request.getParameter("re_step"));
			String ip = request.getRemoteAddr();
			
			// 2. Board board 생성하고 Value Setting
			Board board = new Board();
			board.setNum(num);
			board.setWriter(writer);
			board.setSubject(subject);
			board.setContent(content);
			board.setEmail(email);
			board.setPasswd(passwd);
			board.setRef(ref);
			board.setRe_step(re_step);
			board.setRe_level(re_level);
			board.setIp(ip);
			
			// 3. BoardDao bd Instance
			BoardDao bd = BoardDao.getInstance();
			
			// 4 int result = bd.insert(board);
			int result = bd.insert(board);
			
			// 5. request 객체에 result, num , pageNum
			request.setAttribute("result", result);
			request.setAttribute("num", board.getNum()); // bd.insert에 저장된 num을 가져온다
			request.setAttribute("pageNum", pageNum);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		
		return "writePro.jsp";
	}

}
