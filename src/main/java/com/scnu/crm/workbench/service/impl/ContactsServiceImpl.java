package com.scnu.crm.workbench.service.impl;

import com.scnu.crm.utils.SqlSessionUtil;
import com.scnu.crm.workbench.dao.ContactsDao;
import com.scnu.crm.workbench.domain.Contacts;
import com.scnu.crm.workbench.service.ContactsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactsServiceImpl implements ContactsService {
    @Autowired
    private ContactsDao contactsDao;

    public List<Contacts> getContactsListByName(String fullname) {
        return contactsDao.getContactsListByName(fullname);
    }
}
