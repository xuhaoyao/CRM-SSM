package com.scnu.crm.web.listener;

import com.scnu.crm.settings.domain.DicValue;
import com.scnu.crm.settings.service.DicService;
import com.scnu.crm.settings.service.impl.DicServiceImpl;
import com.scnu.crm.utils.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;


/*
在web容器中,servlet,filter,listener都不是spring容器管理的,因此无法通过注入的方式获取对象
如:
    @Autowired
    private DicService ds;
采用这种方式,项目启动的时候就会报错
原因:
    Listener的生命周期是由servlet容器(Tomcat)管理的,项目启动的时候SystemInitListener是由servlet容器实例化并调用
    contextInitialized(ServletContextEvent sce)方法,但servlet容器不认识@Autoried注解,因此报错

但此处需要将数据字典放在ServletContext里面,就采取最原始的方法,先得到spring容器,然后通过getBean来获取
(WebApplicationContextUtils.getWebApplicationContext(application)).getBean(xxx.class)
 */

public class SystemInitListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        /*
            对于一些固定不变的数据,且前端经常需要使用到的,
            可以考虑将其加入到全局作用域中,方便使用,且不用频繁和数据库做交互
         */
        System.out.println("服务器缓存处理数据字典开始...");
        ServletContext application = sce.getServletContext();
        /*
            此处不能将ds定义为 @Autowired private DicService ds;
            否则项目根本运行不了
            原因:spring的监听器ContextLoaderListener先执行,SystemInitListener后执行
         */
        DicService ds = (WebApplicationContextUtils.getWebApplicationContext(application)).getBean(DicServiceImpl.class);
        Map<String, List<DicValue>> map = ds.getAllDicValues();
        Set<String> set = map.keySet();
        for (String s : set) {
            application.setAttribute(s,map.get(s));
        }
        System.out.println("服务器缓存处理数据字典结束...");

        /*
            处理stage2Possibility.properties(前端交易页面 处理阶段与可能性之间的键值对关系)
            在jdk/bin目录下有个工具native2ascii.exe 可以将中文转码
            注意:IDEA中properties文件是不支持中文的,因此要将中文转码(IDEA配置全局UTF-8也可)
            而java.util.ResourcesBundle类读取properties文件时,会自动解析ASCII码
         */
        ResourceBundle rb = ResourceBundle.getBundle("conf/stage2Possibility");
        Enumeration<String> e = rb.getKeys();
        Map<String,String> pMap = new HashMap<String, String>();
        /*
            迭代器在遍历中是最快的
            对于数据量较大的,采用迭代器的方式遍历集合
            若数据量比较小的话,采用foreach比较方便
         */
        while (e.hasMoreElements()) {
            String stage = e.nextElement();
            String possibility = rb.getString(stage);
            pMap.put(stage,possibility);
        }
        application.setAttribute("pMap",pMap);
        System.out.println(pMap);
    }

}
