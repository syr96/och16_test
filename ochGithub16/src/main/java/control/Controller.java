package control;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.CommandProcess;

/**
 * Servlet implementation class Controller
 */

// @WebServlet("/Controller")
// web.xml로 연결하기위해 주석처리한다
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	// 목적: init으로 command.properties 읽어서 commandMap 등록
	private Map<String, Object> commandMap = new HashMap<String, Object>();
	// config가 key, value 형식이므로 Map으로 받아온다
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Controller() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// 1. web.xml에서 propertyConfig에 해당하는 init-param의 값을 읽어옴
		String props = config.getInitParameter("config");
		System.out.println("1. init String props => " + props);
		// = /WEB-INF/command.properties
		
		Properties pr = new Properties();
		FileInputStream f = null;
		try {
			String configFilePath = config.getServletContext().getRealPath(props);
			System.out.println("2. init String configFilePath -> " + configFilePath);
			// 실제 경로
			// = C:\jsp\jspSrc\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\och16\WEB-INF\command.properties
			
			// string props -> file로 변신
			f = new FileInputStream(configFilePath);

			// Memory Up
			// property 형식으로 메모리에 올린다
			pr.load(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(f != null)
				try {
					f.close();
				} catch (IOException ex) {}
		}
		
		Iterator keyIter = pr.keySet().iterator();
		// key만 모아서 iterator(순서대로 정렬)한다
		
		while (keyIter.hasNext()) {
			String command = (String)keyIter.next();
			String className = pr.getProperty(command);
			System.out.println("3. init command -> " + command);
			// = /list.do(=com) : 값이 변한다. 특정할 수 없다.
			System.out.println("4. init className -> " + className);
			// = service.ListAction
			
			// ListAction listAction = new ListAction(); 와 같은 작업을 진행
			try {
				Class<?> commandClass = Class.forName(className);
				// 해당 문자열을 클래스로 만든다
				// 문자열 -> service.ListAction가 class로 변신
				// class의 사용도를 높이기위해서 가변값으로 지정한다
				
				CommandProcess commandInstance = 
						(CommandProcess)commandClass.getDeclaredConstructor().newInstance();
				// instance로 변신(공식같은 개념)
				
				commandMap.put(command, commandInstance);
				// 			 = list.do	= instance화 된 ListAction
				//				String, Object
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		requestServletPro(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		requestServletPro(request, response);
	}
	
	protected void requestServletPro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String view = null;
		CommandProcess com = null;
		String command = request.getRequestURI();
		System.out.println("1. requestServletPro command -> " + command);
		// = /och16/list.do(=com)
		command = command.substring(request.getContextPath().length());
		System.out.println("2. requestServletPro command substring -> " + command);
		// = /list.do(=com)
		
		try {
			com = (CommandProcess) commandMap.get(command);
			// service.ListAction Instance
			
			System.out.println("3. requestServletPro command -> " + command);
			// = /list.do(=com)

			System.out.println("4. requestServletPro com -> " + com);
			// = service.ListAction@186a598 = instance
			// 			 ListAction의 HashCode(Object 임을 뜻한다)

			view = com.requestPro(request, response);
			// service.ListAction.requestPro가 실행됨
			
			System.out.println("5. requestServletPro view -> " + view);
			// = list.jsp
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
		// Ajax or NOT Ajax
		// 1. Ajax이면
		if(command.contains("ajaxGet")) {
			System.out.println("ajaxGet String -> " + command);
			String writer = (String)request.getAttribute("writer");
			PrintWriter pw = response.getWriter();
			pw.write(writer); // write: 출력하기
			pw.flush(); // flush: 강제 출력
			
		// 2. or NOT -> 일반 command(또는 Service)
		} else {
			RequestDispatcher dispatcher = request.getRequestDispatcher(view);
			dispatcher.forward(request, response);
		}
	}
}
