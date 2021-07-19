package com.scnu.crm.settings.test;

import com.scnu.crm.utils.MD5Util;

public class Test1 {
    public static void main(String[] args) {
        String password = "dsdsds@dsd#s2ss";
        String md5 = MD5Util.getMD5(password);
        System.out.println(md5);
    }
}
