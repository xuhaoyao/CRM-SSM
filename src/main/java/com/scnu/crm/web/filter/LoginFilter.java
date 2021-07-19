package com.scnu.crm.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        System.out.println("doFilter...");
        HttpServletRequest request = (HttpServletRequest)req;
        HttpSession session = request.getSession(false);
        String path = request.getServletPath();
        if("/settings/user/login.do".equals(path) || "/login.jsp".equals(path))
            chain.doFilter(req,resp);
        else if(session != null && session.getAttribute("user") != null)
            chain.doFilter(req,resp);
        else{
            /*
                转发:
                    使用一种特殊的绝对路径的使用方式，这种绝对路径前面不加/项目名，这种路径也称为内部路径
                    /login.jsp
                    request.getRequestDispatcher("/login.jsp").forward(request,response);
                重定向:
                    使用传统的绝对路径的写法,前面必须加/项目名,后面跟具体的资源
                    /crm/login.jsp
                    response.sendRedirect(request.getContextPath() + "/login.jsp");
                这里转发之后，路径会停留在老路径，而不是跳转之后的最新路径，因此选重定向
             */
            HttpServletResponse response = (HttpServletResponse) resp;
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }

    public void destroy() {

    }
}
