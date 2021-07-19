package com.scnu.crm.workbench.service.impl;

import com.scnu.crm.exceptions.ActivityException;
import com.scnu.crm.settings.dao.UserDao;
import com.scnu.crm.settings.domain.User;
import com.scnu.crm.vo.PaginationVo;
import com.scnu.crm.workbench.dao.ActivityDao;
import com.scnu.crm.workbench.dao.ActivityRemarkDao;
import com.scnu.crm.workbench.domain.Activity;
import com.scnu.crm.workbench.domain.ActivityRemark;
import com.scnu.crm.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private ActivityRemarkDao activityRemarkDao;
    @Autowired
    private UserDao userDao;

    @Transactional
    public boolean save(Activity activity) throws ActivityException{
        boolean flag =  (activityDao.save(activity) == 1);
        if(!flag)
            throw new ActivityException("添加市场活动失败");
        return flag;
    }

    public PaginationVo<Activity> pageList(Map<String, Object> map) {
        //取得total
        int total = activityDao.getTotalByCondition(map);
        //取dataList
        List<Activity> dataList = activityDao.getDataListByCondition(map);
        //封装到vo
        PaginationVo<Activity> vo = new PaginationVo<Activity>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        return vo;
    }

    @Transactional
    public boolean delete(String[] ids) throws ActivityException {
        boolean flag = false;
        //删除市场活动之前 首先删除关联表的数据
        int count1 = activityRemarkDao.getCountByIds(ids);
        int count2 = activityRemarkDao.deleteByIds(ids);
        flag = (count1 == count2);
        if(flag){
            int count3 = activityDao.deleteByIds(ids);
            flag = (count3 == ids.length);
            if(!flag)
                throw new ActivityException("删除市场活动失败");
        }
        else
            throw new ActivityException("删除关联的备注表失败");
        return flag;
    }

    public Map<String, Object> getUserListAndActivity(String id) {
        Map<String,Object> map = new HashMap<String, Object>();
        List<User> users = userDao.getUserList();
        Activity a = activityDao.getActivityById(id);
        map.put("users",users);
        map.put("a",a);
        return map;
    }

    @Transactional
    public boolean update(Activity activity) throws ActivityException {
        boolean flag = (1 == activityDao.update(activity));
        if(!flag)
            throw new ActivityException("更新市场活动失败");
        return flag;
    }

    public Activity detail(String id) {
        return activityDao.detail(id);
    }

    public List<ActivityRemark> getRemarkList(String activityId) {
        return activityRemarkDao.getRemarkList(activityId);
    }

    @Transactional
    public boolean deleteRemark(String id) throws ActivityException {
        boolean flag =  (1 == activityRemarkDao.deleteRemark(id));
        if(!flag)
            throw new ActivityException("删除备注失败");
        return flag;
    }

    @Transactional
    public boolean saveRemark(ActivityRemark ar) throws ActivityException {
        boolean flag = (1 == activityRemarkDao.saveRemark(ar));
        if(!flag)
            throw new ActivityException("添加备注失败");
        return flag;
    }

    @Transactional
    public boolean updateRemark(ActivityRemark ar) throws ActivityException {
        boolean flag = (1 == activityRemarkDao.updateRemark(ar));
        if(!flag)
            throw new ActivityException("更新备注失败");
        return flag;
    }

    public List<Activity> getActivityListByClueId(String clueId) {
        return activityDao.getActivityListByClueId(clueId);
    }

    public List<Activity> getActivityListByName(Map<String, String> map) {
        return activityDao.getActivityListByName(map);
    }

    public List<Activity> getActivityListByName1(String name) {
        return activityDao.getActivityListByName1(name);
    }
}
