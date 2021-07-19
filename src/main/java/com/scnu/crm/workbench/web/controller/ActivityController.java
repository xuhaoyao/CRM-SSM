package com.scnu.crm.workbench.web.controller;

import com.scnu.crm.exceptions.ActivityException;
import com.scnu.crm.settings.domain.User;
import com.scnu.crm.settings.service.UserService;
import com.scnu.crm.settings.service.impl.UserServiceImpl;
import com.scnu.crm.utils.DateTimeUtil;
import com.scnu.crm.utils.PrintJson;
import com.scnu.crm.utils.ServiceFactory;
import com.scnu.crm.utils.UUIDUtil;
import com.scnu.crm.vo.PaginationVo;
import com.scnu.crm.workbench.domain.Activity;
import com.scnu.crm.workbench.domain.ActivityRemark;
import com.scnu.crm.workbench.service.ActivityService;
import com.scnu.crm.workbench.service.impl.ActivityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workbench/activity")
public class ActivityController{

    @Autowired
    private ActivityService as;

    @Autowired
    private UserService us;

    @RequestMapping("/updateActivity.do")
    @ResponseBody
    private Map<String,Object> updateActivity(Activity activity, HttpServletRequest request) {
        String editTime = DateTimeUtil.getSysTime();
        activity.setEditTime(editTime);
        String editBy = ((User)request.getSession(false).getAttribute("user")).getName();
        activity.setEditBy(editBy);
        Map<String,Object> map = new HashMap<String, Object>();
        boolean flag = false;
        try {
            flag = as.update(activity);
            map.put("a",activity);
        } catch (ActivityException e) {
            map.put("errMsg",e.getMessage());
        }
        map.put("success",flag);
        return map;
    }

    @PostMapping("/updateRemark.do")
    @ResponseBody
    private Map<String,Object> updateRemark(ActivityRemark ar, HttpServletRequest request) {
        String editBy = ((User)request.getSession(false).getAttribute("user")).getName();
        String editTime = DateTimeUtil.getSysTime();
        String editFlag = "1";
        ar.setEditBy(editBy);
        ar.setEditTime(editTime);
        ar.setEditFlag(editFlag);
        Map<String,Object> map = new HashMap<>();
        boolean flag = false;
        try {
            flag = as.updateRemark(ar);
            map.put("ar",ar);
        } catch (ActivityException e) {
            map.put("errMsg",e.getMessage());
        }
        map.put("success",flag);
        return map;
    }

    @PostMapping("/saveRemark.do")
    @ResponseBody
    private Map<String,Object> saveRemark(ActivityRemark ar, HttpServletRequest request) {
        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession(false).getAttribute("user")).getName();
        String editFlag = "0";
        ar.setId(id);
        ar.setCreateTime(createTime);
        ar.setCreateBy(createBy);
        ar.setEditFlag(editFlag);
        Map<String,Object> map = new HashMap<String, Object>();
        boolean flag = false;
        try {
            flag = as.saveRemark(ar);
            map.put("ar",ar);
        } catch (ActivityException e) {
            map.put("errMsg",e.getMessage());
        }
        map.put("success",flag);
        return map;
    }

    @PostMapping("/deleteRemark.do")
    @ResponseBody
    private Map<String,Object> deleteRemark(HttpServletRequest request) {
        boolean flag = false;
        Map<String,Object> map = new HashMap<>();
        try {
            flag = as.deleteRemark(request.getParameter("id"));
        } catch (ActivityException e) {
            map.put("errMsg",e.getMessage());
        }
        map.put("success",flag);
        return map;
    }

    @GetMapping("/getRemarkList.do")
    @ResponseBody
    private List<ActivityRemark> getRemarkList(HttpServletRequest request) {
        List<ActivityRemark> activityRemarks = as.getRemarkList(request.getParameter("activityId"));
        return activityRemarks;
    }

    @GetMapping("/detail.do")
    private ModelAndView detail(HttpServletRequest request, String id){
        Activity a = as.detail(id);
        request.setAttribute("a",a);
        //备注页面返回时候保证回到对应页面,且刷新页面(用户可能在备注页面做修改)
        request.setAttribute("pageNo",request.getParameter("pageNo"));
        request.setAttribute("pageSize",request.getParameter("pageSize"));
        request.setAttribute("name",request.getParameter("name"));
        request.setAttribute("owner",request.getParameter("owner"));
        request.setAttribute("startDate",request.getParameter("startDate"));
        request.setAttribute("endDate",request.getParameter("endDate"));
        ModelAndView mv = new ModelAndView();
        mv.addObject("a",a);
        mv.setViewName("activity/detail");
        return mv;
    }

    @PostMapping("/update.do")
    @ResponseBody
    private Map<String,Object> update(Activity activity, HttpServletRequest request) {
        String currentDate = DateTimeUtil.getSysTime();
        activity.setEditTime(currentDate);
        String editBy = ((User)request.getSession(false).getAttribute("user")).getName();
        activity.setEditBy(editBy);
        Map<String,Object> map = new HashMap<>();
        boolean flag = false;
        try {
            flag = as.update(activity);
        } catch (ActivityException e) {
            map.put("errMsg",e.getMessage());
        }
        map.put("success",flag);
        return map;
    }

    @GetMapping("/getUserListAndActivity.do")
    @ResponseBody
    private Map<String,Object> getUserListAndActivity(String id) {
        Map<String,Object> map = as.getUserListAndActivity(id);
        return map;
    }

    @PostMapping("/delete.do")
    @ResponseBody
    private Map<String,Object> delete(HttpServletRequest request) {
        String[] ids = request.getParameterValues("id");
        Map<String,Object> map = new HashMap<>();
        boolean flag = false;
        try {
            flag = as.delete(ids);
        } catch (ActivityException e) {
            map.put("errMsg",e.getMessage());
        }
        map.put("success",flag);
        return map;
    }

    @GetMapping("/pageList.do")
    @ResponseBody
    private PaginationVo<Activity> pageList(HttpServletRequest request) {
        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        int pageNo = Integer.valueOf(pageNoStr);
        int pageSize = Integer.valueOf(pageSizeStr);
        int skipCount = (pageNo - 1) * pageSize;
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);
        /*
            前端要: 市场活动信息列表
                    查询的总条数

            这里用vo(泛型) 以后查询的东西通用。
                PaginationVo<T>
                    private int total;
                    private List<T> dataList;
            分页查询每个模块都有,所以采用一个通用vo
         */
        PaginationVo<Activity> vo = as.pageList(map);

        return vo;

    }

    @PostMapping("/save.do")
    @ResponseBody
    private Map<String,Object> save(Activity activity,HttpServletRequest request) {
        activity.setId(UUIDUtil.getUUID());
        String currentDate = DateTimeUtil.getSysTime();
        activity.setCreateTime(currentDate);
        String createBy = ((User)request.getSession(false).getAttribute("user")).getName();
        activity.setCreateBy(createBy);
        Map<String,Object> map = new HashMap<>();
        boolean flag = false;
        try {
            flag = as.save(activity);
        } catch (ActivityException e) {
            map.put("errMsg",e.getMessage());
        }
        map.put("success",flag);
        return map;
    }

    @GetMapping("/getUserList.do")
    @ResponseBody
    private List<User> getUserList() {
        List<User> users = us.getUserList();
        return users;
    }
}
