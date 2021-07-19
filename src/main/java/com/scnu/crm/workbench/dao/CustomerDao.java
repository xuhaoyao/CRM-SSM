package com.scnu.crm.workbench.dao;

import com.scnu.crm.workbench.domain.Customer;

import java.util.List;

public interface CustomerDao {

    Customer getCustomerByName(String company);

    int save(Customer cus);

    List<String> getCustomerByName1(String name);

    String getCustomerIdByName(String customerName);
}
