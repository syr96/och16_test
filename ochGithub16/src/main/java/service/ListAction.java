package service;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.Board;
import dao.BoardDao;

public class ListAction implements CommandProcess {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("ListAction Start...");
		
		BoardDao bd = BoardDao.getInstance();
		try {
			// 게시판 총 갯수
			int totCnt = bd.getTotalCnt(); // 38 
			
			String pageNum = request.getParameter("pageNum");
			if(pageNum == null || pageNum.equals("")) pageNum = "1";
			int currentPage = Integer.parseInt(pageNum); 	  // 1
			int pageSize = 10;
			int blockSize = 10;
			int startRow = (currentPage - 1) * pageSize + 1; 
			//  1			(1 - 1) * 10 + 1
			int endRow = startRow + pageSize - 1;
			// 	10		 1 + 10 - 1
			int startNum = totCnt - startRow + 1;
			//	
			
			// Board 조회								1	,	10
			List<Board> boardList = bd.boardList(startRow, endRow); 
			
			int pageCnt = (int)Math.ceil((double)totCnt / pageSize); 
			// 4		=				(3.8)
			
			int startPage = (int)(currentPage - 1) / blockSize * blockSize + 1; // 1
			int endPage = startPage + blockSize - 1;
			
			// 공갈 Page 방지
			// 		10 > 4
			if(endPage > pageCnt) endPage = pageCnt;
			
			System.out.println("ListAction startPage -> " + startPage);
			System.out.println("ListAction endPage -> " + endPage);
			System.out.println("ListAction pageCnt -> " + pageCnt);
			System.out.println("ListAction blockSize -> " + blockSize);
			request.setAttribute("boardList", boardList); // ***
			request.setAttribute("totCnt", totCnt);
			request.setAttribute("pageNum", pageNum);
			request.setAttribute("currentPage", currentPage);
			request.setAttribute("startNum", startNum);
			request.setAttribute("blockSize", blockSize);
			request.setAttribute("pageCnt", pageCnt);
			request.setAttribute("startPage", startPage);
			request.setAttribute("endPage", endPage);
		} catch (Exception e) {
			System.out.println("ListAction e.getMessage() -> " + e.getMessage());
		}
		
		// view 명칭
		return "list.jsp";
		// return "listMakeup.jsp";
	}

}
