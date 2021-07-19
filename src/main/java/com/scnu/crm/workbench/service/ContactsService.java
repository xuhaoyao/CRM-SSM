package com.scnu.crm.workbench.service;

import com.scnu.crm.workbench.domain.Contacts;

import java.util.List;

public interface ContactsService {
    List<Contacts> getContactsListByName(String fullname);
}
