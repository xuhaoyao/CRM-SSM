package com.scnu.crm.workbench.dao;

import com.scnu.crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranHistoryDao {

    int save(TranHistory th);

    List<TranHistory> getHistoryList(String tranId);
}
