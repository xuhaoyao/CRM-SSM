package com.scnu.crm.workbench.web.controller;

import com.scnu.crm.exceptions.ClueException;
import com.scnu.crm.settings.domain.User;
import com.scnu.crm.settings.service.UserService;
import com.scnu.crm.utils.DateTimeUtil;
import com.scnu.crm.utils.UUIDUtil;
import com.scnu.crm.vo.PaginationVo;
import com.scnu.crm.workbench.domain.Activity;
import com.scnu.crm.workbench.domain.Clue;
import com.scnu.crm.workbench.domain.Tran;
import com.scnu.crm.workbench.service.ActivityService;
import com.scnu.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workbench/clue")
public class ClueController {

    @Autowired
    private UserService us;
    @Autowired
    private ClueService cs;
    @Autowired
    private ActivityService as;

    @RequestMapping("/convert.do")
    private String convert(HttpServletRequest request) {
        String method = request.getMethod();
        String clueId = request.getParameter("clueId");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession(false).getAttribute("user")).getName();
        Tran t = null;
        if("POST".equals(method)){
            t = new Tran();
            t.setId(UUIDUtil.getUUID());
            t.setMoney(request.getParameter("money"));
            t.setName(request.getParameter("name"));
            t.setExpectedDate(request.getParameter("expectedDate"));
            t.setStage(request.getParameter("stage"));
            t.setActivityId(request.getParameter("activityId"));
            t.setCreateBy(createBy);
            t.setCreateTime(createTime);
        }
        try {
            cs.convert(clueId,t,createBy,createTime);
        } catch (ClueException e) {
            request.setAttribute("errMsg",e.getMessage());
            return "forward:/workbench/clue/error.jsp";
        }
        return "redirect:/workbench/clue/index.jsp";
       //     response.sendRedirect(request.getContextPath() + "/workbench/clue/index.jsp");
    }

    @GetMapping("/getActivityListByName1.do")
    @ResponseBody
    private List<Activity> getActivityListByName1(String name) {
        List<Activity> aList = as.getActivityListByName1(name);
        return aList;
    }

    @PostMapping("/association.do")
    @ResponseBody
    private Map<String,Object> association(String clueId,String[] activityId) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("clueId",clueId);
        map.put("activityIds",activityId);
        boolean flag = false;
        Map<String,Object> aMap = new HashMap<>();
        try {
            flag = cs.association(map);
        } catch (ClueException e) {
            aMap.put("errMsg",e.getMessage());
        }
        aMap.put("success",flag);
        return aMap;
    }

    @GetMapping("/getActivityListByName.do")
    @ResponseBody
    private List<Activity> getActivityListByName(String name, String clueId) {
        Map<String,String> map = new HashMap<String, String>();
        map.put("name",name);
        map.put("clueId",clueId);
        List<Activity> aList = as.getActivityListByName(map);
        return aList;
    }

    @PostMapping("/Disassociation.do")
    @ResponseBody
    private Map<String,Object> disssociation(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        boolean flag = false;
        Map<String,Object> map = new HashMap<>();
        try {
            flag = cs.disssociation(id);
        } catch (ClueException e) {
            map.put("errMsg",e.getMessage());
        }
        map.put("success",flag);
        return map;
    }

    @GetMapping("/getActivityListByClueId.do")
    @ResponseBody
    private List<Activity> getActivityListByClueId(String clueId) {
        List<Activity> aList = as.getActivityListByClueId(clueId);
        return aList;
    }

    @GetMapping("/pageList.do")
    @ResponseBody
    private PaginationVo<Clue> pageList(Clue clue, HttpServletRequest request) {
        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        int pageNo = Integer.valueOf(pageNoStr);
        int pageSize = Integer.valueOf(pageSizeStr);
        int skipCount = (pageNo - 1) * pageSize;

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("fullname",clue.getFullname());
        map.put("owner",clue.getOwner());
        map.put("company",clue.getCompany());
        map.put("phone",clue.getPhone());
        map.put("mphone",clue.getMphone());
        map.put("state",clue.getState());
        map.put("source",clue.getSource());
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);

        PaginationVo<Clue> vo = cs.pageList(map);

        return vo;
    }

    @GetMapping("/detail.do")
    private String detail(HttpServletRequest request) {
        String id = request.getParameter("id");
        Clue clue = cs.findById(id);
        request.setAttribute("c",clue);
        return "clue/detail";
    }

    @PostMapping("/save.do")
    @ResponseBody
    private Map<String,Object> save(Clue clue, HttpServletRequest request) {
        String id = UUIDUtil.getUUID();
        String createBy = ((User)request.getSession(false).getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        clue.setCreateBy(createBy);
        clue.setCreateTime(createTime);
        clue.setId(id);
        boolean flag = false;
        Map<String,Object> map = new HashMap<>();
        try {
            flag = cs.save(clue);
        } catch (ClueException e) {
            map.put("errMsg",e.getMessage());
        }
        map.put("success",flag);
        return map;
    }

    @GetMapping("/getUserList.do")
    @ResponseBody
    private List<User> getUserList() {
        List<User> users = us.getUserList();
        System.out.println(users);
        return users;
    }

}
