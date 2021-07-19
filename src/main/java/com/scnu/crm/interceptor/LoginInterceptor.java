package com.scnu.crm.interceptor;

import com.scnu.crm.settings.domain.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //System.out.println("进入拦截器....");
        User user = (User) request.getSession().getAttribute("user");
        if(user != null)
            return true;
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return false;
    }

}
