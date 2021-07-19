package com.scnu.crm.settings.service;

import com.scnu.crm.exceptions.LoginException;
import com.scnu.crm.settings.domain.User;

import java.util.List;

public interface UserService {
    User login(String loginAct, String loginPwd, String ip) throws LoginException;

    List<User> getUserList();
}
