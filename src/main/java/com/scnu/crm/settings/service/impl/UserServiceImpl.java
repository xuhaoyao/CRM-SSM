package com.scnu.crm.settings.service.impl;

import com.scnu.crm.exceptions.LoginException;
import com.scnu.crm.settings.dao.UserDao;
import com.scnu.crm.settings.domain.User;
import com.scnu.crm.settings.service.UserService;
import com.scnu.crm.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    public User login(String loginAct, String loginPwd, String ip) throws LoginException {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        User user = userDao.login(map);
        if(user == null)
            throw new LoginException("账号或密码错误");
        //如果程序执行到这 那么账号密码正确 需要继续验证

        //验证失效时间
        String expireTime = user.getExpireTime();
        String currentTime = DateTimeUtil.getSysTime();
        if(currentTime.compareTo(expireTime) > 0)
            throw new LoginException("账号已失效");

        //验证是否锁定
        String lockState = user.getLockState();
        if("0".equals(lockState))
            throw new LoginException("账号已被锁定");

        //验证ip地址的合法性
        String allowIps = user.getAllowIps();
        if(allowIps != null && allowIps != "" && !allowIps.contains(ip))
            throw new LoginException("IP地址不合法");

        //执行到此,则验证成功 可以登陆
        return user;
    }

    public List<User> getUserList() {
        return userDao.getUserList();
    }

}
