<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="style.css" type="text/css">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
<style type="text/css">
	table {
		width: 100%;
	}
</style>
<%	String context = request.getContextPath();	%>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript">
	function getDeptName(v_num) {
		// alert("getDeptName 1");
		$.ajax({
			url : "<%=context%>/ajaxGetDeptName.do",
			data : {num : v_num},
			dataType : 'text',
			success : function(writer) {
						// alert(".ajax Data" + writer);
						/* input Tag */
						$('#writerName').val(writer);
						/* span Tag */
						$('#writerMsg').html(writer);
						// alert("getDeptName 2");
			}
		});
	};
</script>
</head>
<body>
	<div id="header">
		<nav class="navbar navbar-expand-lg navbar-light bg-light">
		  <div class="container-fluid">
		    <a class="navbar-brand" href="#">Navbar</a>
		    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
		      <span class="navbar-toggler-icon"></span>
		    </button>
		    <div class="collapse navbar-collapse" id="navbarSupportedContent">
		      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
		        <li class="nav-item">
		          <a class="nav-link active" aria-current="page" href="list.do">listHome</a>
		        </li>
		        <li class="nav-item">
		          <a class="nav-link" href="writeForm.do">신규 글쓰기</a>
		        </li>
		        <li class="nav-item dropdown">
		          <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
		            Dropdown
		          </a>
		          <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
		            <li><a class="dropdown-item" href="#">Action</a></li>
		            <li><a class="dropdown-item" href="#">Another action</a></li>
		            <li><hr class="dropdown-divider"></li>
		            <li><a class="dropdown-item" href="#">Something else here</a></li>
		          </ul>
		        </li>
		        <li class="nav-item">
		          <a class="nav-link disabled" href="#" tabindex="-1" aria-disabled="true">Disabled</a>
		        </li>
		      </ul>
		      <form class="d-flex">
		        <input class="form-control me-2" type="search" placeholder="Search" aria-label="Search">
		        <button class="btn btn-outline-success" type="submit">Search</button>
		      </form>
		    </div>
		  </div>
		</nav>
	</div>
	
	<div id="contents">
		<h1>게시판 전체 Count : ${totCnt }</h1>
		<table>
			<tr>
				<td><a href="writeForm.do">글쓰기</a></td>
			</tr>
		</table>
		<table>
			<tr>
				<th>번호</th><th>제목</th><th>작성자</th><th>이메일</th><th>IP</th><th>작성일</th><th>조회수</th>
			</tr>
			<c:if test="${totCnt > 0 }">
				<c:forEach var="board" items="${boardList }">
					<tr>
						<td>${startNum }</td>
						<td class="left" width=200>
							<c:if test="${board.readcount > 20 }">
								<img src="images/hot.gif" onmousemove="getDeptName(${board.num})">
							</c:if>
							<c:if test="${board.re_level > 0 }">
								<img src="images/level.gif" width="${board.re_level * 10 }">
								<img src="images/re.gif">
							</c:if>
							<a href="content.do?num=${board.num }&pageNum=${currentPage }">
							${board.subject }</a>
						</td>
						<td>${board.writer }</td>
						<td><a href="mailto:${board.email }">${board.email }</a></td>
						<td>${board.ip }</td>
						<td>${board.reg_date }</td>
						<td>${board.readcount }</td>
					</tr>
					<c:set var="startNum" value="${startNum - 1 }"/>
				</c:forEach>
			</c:if>
			<c:if test="${totCnt == 0 }">
				<tr>
					<td colspan="7">데이터가 없네</td>
				</tr>
			</c:if>
		</table>
		<div style="text-align: center;">
			<c:if test="${startPage > blockSize }">
				<a href="list.do?pageNum=${startPage-blockSize }">[이전]</a>
			</c:if>
			<c:forEach var="i" begin="${startPage }" end="${endPage }">
				<a href="list.do?pageNum=${i }">[${i }]</a>
			</c:forEach>
			<c:if test="${endPage < pageCnt }">
				<a href="list.do?pageNum=${startPage + blockSize }">[다음]</a>
			</c:if>
		</div>
		<div id="ajaxMessage">
			AJax writerName 결과 : <input type="text" id="writerName" readonly="readonly"><p>
			AJax writerMsg  결과 : <span id="writerMsg"></span><p>
		</div>
	</div>
	
	<div id="footer">
		<h4>저작권 침해 시 법적처벌대상 유의</h4>
		<h5>중앙정보(주) 02-1234-5678</h5>
	</div>
</body>
</html>