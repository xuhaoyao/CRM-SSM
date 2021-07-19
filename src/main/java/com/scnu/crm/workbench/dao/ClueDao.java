package com.scnu.crm.workbench.dao;

import com.scnu.crm.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

public interface ClueDao {


    int save(Clue clue);

    Clue findById(String id);

    int getTotalByCondition(Map<String, Object> map);

    List<Clue> getDataListByCondition(Map<String, Object> map);

    Clue getClueById(String clueId);

    int deleteByClueId(String clueId);
}
