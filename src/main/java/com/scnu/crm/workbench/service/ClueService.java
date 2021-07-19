package com.scnu.crm.workbench.service;

import com.scnu.crm.vo.PaginationVo;
import com.scnu.crm.workbench.domain.Clue;
import com.scnu.crm.workbench.domain.Tran;

import java.util.Map;

public interface ClueService {
    boolean save(Clue clue);

    Clue findById(String id);

    PaginationVo<Clue> pageList(Map<String, Object> map);

    boolean disssociation(String id);

    boolean association(Map<String, Object> map);

    boolean convert(String clueId, Tran t, String createBy, String createTime);
}
