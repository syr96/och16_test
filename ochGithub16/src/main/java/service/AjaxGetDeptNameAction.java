package service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.Board;
import dao.BoardDao;

public class AjaxGetDeptNameAction implements CommandProcess {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("AjaxGetDeptNameAction start...");
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		try {
			int num = Integer.parseInt(request.getParameter("num"));
			BoardDao bd = BoardDao.getInstance();
			Board board;
			board = bd.select(num);
			request.setAttribute("writer", board.getWriter());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// ajax 경우 -> 더미 return
		// requestPro의 형식을 맞춰주기 위해서 사용한다(의미가 없는 return)
		return "ajax";
	}

}
