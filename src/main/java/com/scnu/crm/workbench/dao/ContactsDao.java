package com.scnu.crm.workbench.dao;

import com.scnu.crm.workbench.domain.Contacts;

import java.util.List;

public interface ContactsDao {

    int save(Contacts con);

    List<Contacts> getContactsListByName(String fullname);
}
