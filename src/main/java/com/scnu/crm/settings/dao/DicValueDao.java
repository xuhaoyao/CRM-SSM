package com.scnu.crm.settings.dao;

import com.scnu.crm.settings.domain.DicValue;

import java.util.List;

public interface DicValueDao {
    List<DicValue> getAllValues(String s);
}
