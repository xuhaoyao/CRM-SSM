package com.scnu.crm.workbench.dao;

import com.scnu.crm.settings.domain.User;
import com.scnu.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityDao {
    List<User> getUserList();

    int save(Activity activity);

    List<Activity> getDataListByCondition(Map<String, Object> map);

    int getTotalByCondition(Map<String, Object> map);

    int deleteByIds(String[] ids);

    Activity getActivityById(String id);

    int update(Activity activity);

    Activity detail(String id);

    List<Activity> getActivityListByClueId(String clueId);

    List<Activity> getActivityListByName(Map<String, String> map);

    List<Activity> getActivityListByName1(String name);
}
