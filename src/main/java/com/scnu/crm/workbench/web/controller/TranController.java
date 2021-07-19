package com.scnu.crm.workbench.web.controller;

import com.scnu.crm.settings.domain.User;
import com.scnu.crm.settings.service.UserService;
import com.scnu.crm.settings.service.impl.UserServiceImpl;
import com.scnu.crm.utils.DateTimeUtil;
import com.scnu.crm.utils.PrintJson;
import com.scnu.crm.utils.ServiceFactory;
import com.scnu.crm.utils.UUIDUtil;
import com.scnu.crm.vo.PaginationVo;
import com.scnu.crm.workbench.domain.Activity;
import com.scnu.crm.workbench.domain.Contacts;
import com.scnu.crm.workbench.domain.Tran;
import com.scnu.crm.workbench.domain.TranHistory;
import com.scnu.crm.workbench.service.ActivityService;
import com.scnu.crm.workbench.service.ContactsService;
import com.scnu.crm.workbench.service.CustomerService;
import com.scnu.crm.workbench.service.TranService;
import com.scnu.crm.workbench.service.impl.ActivityServiceImpl;
import com.scnu.crm.workbench.service.impl.ContactsServiceImpl;
import com.scnu.crm.workbench.service.impl.CustomerServiceImpl;
import com.scnu.crm.workbench.service.impl.TranServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/workbench/tran")
public class TranController {

    @Autowired
    private UserService us;
    @Autowired
    private CustomerService cus;
    @Autowired
    private ActivityService as;
    @Autowired
    private ContactsService cos;
    @Autowired
    private TranService ts;

    /*
    此处返回值写为Map<String,Object>即可
    一开始写成Map<String,List<String>>报错:
    org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver.logException Resolved
     [org.springframework.http.converter.HttpMessageNotWritableException:
      Could not write JSON: java.lang.Long cannot be cast to java.lang.String;
       nested exception is com.fasterxml.jackson.databind.JsonMappingException:
        java.lang.Long cannot be cast to java.lang.String
        (through reference chain: java.util.HashMap["value"]->java.util.ArrayList[0])]
     应该是不支持Map的value是List类型,统一写成Object即可
 */
    @GetMapping("/showEcharts1.do")
    @ResponseBody
    private Map<String,Object> showEcharts1() {
        Map<String,Object> map = ts.showEcharts1();
        return map;
    }

    @GetMapping("/showEcharts.do")
    @ResponseBody
    private Map<String,Object> showEcharts() {
        Map<String,Object> map = ts.showEcharts();
        return map;
    }

    @PostMapping("/changeStage.do")
    @ResponseBody
    private Map<String,Object> changeStage(Tran t,HttpServletRequest request) {
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession(false).getAttribute("user")).getName();
        t.setEditBy(editBy);
        t.setEditTime(editTime);
        Map<String,String> pMap = (Map<String, String>) request.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(t.getStage());
        t.setPossibility(possibility);
        Map<String,Object> map = new HashMap<String, Object>();
        boolean flag = false;
        try {
            flag = ts.changeStage(t);
            map.put("t",t);
        } catch (Exception e) {
            map.put("errMsg",e.getMessage());
        }
        map.put("success",flag);
        return map;
    }

    @GetMapping("/getHistoryList.do")
    @ResponseBody
    private List<TranHistory> getHistoryList(String id, HttpServletRequest request) {
        List<TranHistory> tranHistories = ts.getHistoryList(id);
        Map<String,String> pMap = (Map<String, String>) request.getServletContext().getAttribute("pMap");
        for (TranHistory tranHistory : tranHistories) {
            String stage = tranHistory.getStage();
            String possibility = pMap.get(stage);
            tranHistory.setPossibility(possibility);
        }
        return tranHistories;
    }

    @GetMapping("/detail.do")
    private String detail(String id, HttpServletRequest request) {
        Tran t = ts.detail(id);
        Map<String,String> pMap = (Map<String, String>) request.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(t.getStage());
        t.setPossibility(possibility);
        request.setAttribute("t",t);
        return "forward:/workbench/transaction/detail.jsp";
    }

    @PostMapping("/save.do")
    private String save(Tran t,HttpServletRequest request){
        String id = UUIDUtil.getUUID();
        String customerName = request.getParameter("customerName");
        String createBy = ((User)request.getSession(false).getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        t.setCreateTime(createTime);
        t.setCreateBy(createBy);
        t.setId(id);
        try {
            ts.save(t,customerName);
            return "redirect:/workbench/transaction/index.jsp";
        } catch (Exception e) {
            request.setAttribute("errMsg",e.getMessage());
            return "forward:/workbench/transaction/error.jsp";
        }
    }

    @GetMapping("/getContactsListByName.do")
    @ResponseBody
    private List<Contacts> getContactsListByName(HttpServletRequest request, HttpServletResponse response) {
        String fullname = request.getParameter("fullname");
        List<Contacts> cList = cos.getContactsListByName(fullname);
        return cList;
    }

    @GetMapping("/getActivityListByName1.do")
    @ResponseBody
    private List<Activity> getActivityListByName1(HttpServletRequest request) {
        String name = request.getParameter("name");
        List<Activity> aList = as.getActivityListByName1(name);
        return aList;
    }

    @GetMapping("/getCustomerName.do")
    @ResponseBody
    private List<String> getCustomerName(HttpServletRequest request) {
        String name = request.getParameter("name");
        List<String> customerNames = cus.getCustomerName(name);
        return customerNames;
    }

    @GetMapping("/getUserList.do")
    private String getUserList(HttpServletRequest request){
        List<User> users = us.getUserList();
        request.setAttribute("users",users);
        return "forward:/workbench/transaction/save.jsp";
    }

    @GetMapping("/pageList.do")
    @ResponseBody
    private PaginationVo<Tran> pageList(Tran t,Integer pageNo,Integer pageSize){
        /*
            这里传入的map,最好一定要是<String,Object>类型
            如果传了<String,String>,执行limit的时候,这里出现了这个错误
            check the manual that corresponds to your MySQL server version for the right syntax to use near ''0','3'' at line 6
            就是limit '0','3' 会报错
               limit  0,3 才是正确的
            传入<String,String>的话,MyBatis就不会进行自动类型转换，#{skipCount},#{pageSize}就被转成字符串类型因为报错
         */
        Map<String,Object> map = new HashMap<>();
        Integer skipCount = (pageNo - 1) * pageSize;
        map.put("owner",t.getOwner());
        map.put("name",t.getName());
        map.put("customerId",t.getCustomerId());
        map.put("stage",t.getStage());
        map.put("type",t.getType());
        map.put("source",t.getSource());
        map.put("contactsId",t.getContactsId());
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);
        PaginationVo<Tran> vo = ts.getPageList(map);
        return vo;
    }
}
