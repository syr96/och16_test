package service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.Board;
import dao.BoardDao;

public class UpdateProAction implements CommandProcess {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		
		// 1. num , pageNum, writer ,  email , subject , passwd , content   Get
		String pageNum = request.getParameter("pageNum");
		int num = Integer.parseInt(request.getParameter("num"));
		String writer = request.getParameter("writer");
		String email = request.getParameter("email");
		String subject = request.getParameter("subject");
		String passwd = request.getParameter("passwd");
		String content = request.getParameter("content");
		
		// 2. Board board 생성하고 Value Setting
		Board board = new Board();
		board.setNum(num);
		board.setWriter(writer);
		board.setEmail(email);
		board.setSubject(subject);
		board.setPasswd(passwd);
		board.setContent(content);
		board.setIp(request.getRemoteAddr());
		
		try {
			// 3. BoardDao bd Instance
			BoardDao bd = BoardDao.getInstance();
			
			// int result = bd.update(board);
			int result = bd.update(board);
			request.setAttribute("num", num);
			request.setAttribute("pageNum", pageNum);
			request.setAttribute("board", board);
			request.setAttribute("result", result);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		
		return "updatePro.jsp";
	}

}
