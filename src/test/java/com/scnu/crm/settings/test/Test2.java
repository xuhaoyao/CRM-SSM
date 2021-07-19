package com.scnu.crm.settings.test;

import com.scnu.crm.settings.dao.UserDao;
import com.scnu.crm.settings.domain.User;
import com.scnu.crm.utils.SqlSessionUtil;

import java.util.List;

public class Test2 {
    public static void main(String[] args) {
        UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
        List<User> users = userDao.getUserList();
        System.out.println(users);
    }
}
