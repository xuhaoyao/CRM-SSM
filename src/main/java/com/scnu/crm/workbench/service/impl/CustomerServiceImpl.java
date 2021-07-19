package com.scnu.crm.workbench.service.impl;

import com.scnu.crm.utils.SqlSessionUtil;
import com.scnu.crm.workbench.dao.CustomerDao;
import com.scnu.crm.workbench.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerDao customerDao;

    public List<String> getCustomerName(String name) {
        return customerDao.getCustomerByName1(name);
    }
}
