package service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.Board;
import dao.BoardDao;

public class WriteFormAction implements CommandProcess {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("WriteFormAction Start...");
		
		try {
			// 신규글
			// 0으로 지정한 이유: 신규글과 댓글을 구분하기 위해 0으로 설정했다.
			// 0이 아닌 다른 수 일 경우(ref, re_step, re_level) 댓글로 지정한다
			int num = 0;
			int ref = 0;
			int re_level = 0;
			int re_step = 0;
			String pageNum = request.getParameter("pageNum");
			if(pageNum == null) pageNum = "1";
			
			// 댓글일 경우
			if(request.getParameter("num") != null) {
				num = Integer.parseInt(request.getParameter("num"));
				BoardDao bd = BoardDao.getInstance();
				Board board = bd.select(num);
				ref = board.getRef();
				re_level = board.getRe_level();
				re_step = board.getRe_step();
			}
			
			request.setAttribute("num", num);
			request.setAttribute("ref", ref);
			request.setAttribute("re_level", re_level);
			request.setAttribute("re_step", re_step);
			request.setAttribute("pageNum", pageNum);
		} catch (Exception e) {
			System.out.println("WriteFormAction Exception -> " + e.getMessage());
		}
		
		return "writeForm.jsp";
	}

}
