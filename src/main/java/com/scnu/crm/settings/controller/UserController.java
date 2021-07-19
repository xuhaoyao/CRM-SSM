package com.scnu.crm.settings.controller;

import com.scnu.crm.exceptions.LoginException;
import com.scnu.crm.settings.domain.User;
import com.scnu.crm.settings.service.UserService;
import com.scnu.crm.utils.MD5Util;
import com.scnu.crm.utils.PrintJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/settings/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/login.do")
    @ResponseBody
    private Map<String,Object> login(HttpServletRequest request) {
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        //将密码的明文形式转换为MD5的密文形式
        loginPwd = MD5Util.getMD5(loginPwd);
        //接受浏览器的ip地址
        String ip = request.getRemoteAddr();
        System.out.println(ip);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",true);
        try{
            /*
                注意此处: us.login 调用的是代理类的方法
                        因此要在代理类中,将在代理类中捕获的异常,抛出来
                        此处的异常才能被捕获 否则异常在代理类中就被截胡了。
                        因此 在代理类的catch块中 写上 throw e.getCause();
             */
            User user = userService.login(loginAct,loginPwd,ip);


            request.getSession().setAttribute("user",user);
            return map;
        }catch(LoginException e){
            /*
                需要为ajax请求提供多项信息
                两种手段来提供:
                    map: 只有在这个需求才使用
                    vo : 对于展现的信息将来还会大量使用
                那么这里用map
             */
            map.put("success",false);
            map.put("msg",e.getMessage());
            return map;
        }
    }
}
