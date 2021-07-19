package com.scnu.crm.workbench.service.impl;

import com.scnu.crm.exceptions.ClueException;
import com.scnu.crm.exceptions.TranException;
import com.scnu.crm.utils.SqlSessionUtil;
import com.scnu.crm.utils.UUIDUtil;
import com.scnu.crm.vo.PaginationVo;
import com.scnu.crm.workbench.dao.*;
import com.scnu.crm.workbench.domain.*;
import com.scnu.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ClueServiceImpl implements ClueService {

    @Autowired
    private ClueDao clueDao;
    @Autowired
    private ClueActivityRelationDao clueActivityRelationDao;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private ContactsDao contactsDao;
    @Autowired
    private ClueRemarkDao clueRemarkDao;
    @Autowired
    private CustomerRemarkDao customerRemarkDao;
    @Autowired
    private ContactsRemarkDao contactsRemarkDao;
    @Autowired
    private ContactsActivityRelationDao contactsActivityRelationDao;
    @Autowired
    private TranDao tranDao;
    @Autowired
    private TranHistoryDao tranHistoryDao;

    @Transactional
    public boolean save(Clue clue) {
        boolean flag =  (1 == clueDao.save(clue));
        if(!flag)
            throw new ClueException("添加线索失败");
        return flag;
    }

    public Clue findById(String id) {
        return clueDao.findById(id);
    }

    public PaginationVo<Clue> pageList(Map<String, Object> map) {
        PaginationVo<Clue> vo = new PaginationVo<Clue>();
        int total = clueDao.getTotalByCondition(map);
        List<Clue> dataList = clueDao.getDataListByCondition(map);
        vo.setTotal(total);
        vo.setDataList(dataList);
        return vo;
    }

    public boolean disssociation(String id) throws ClueException {
        boolean flag =  (1 == clueActivityRelationDao.disssociation(id));
        if(!flag)
            throw new ClueException("解除市场活动关联失败!");
        return flag;
    }

    public boolean association(Map<String, Object> map) throws ClueException {
        String clueId = (String)map.get("clueId");
        String[] activityIds = (String[]) map.get("activityIds");
        int count = 0;
        for (String activityId : activityIds) {
            String id = UUIDUtil.getUUID();
            ClueActivityRelation car = new ClueActivityRelation();
            car.setId(id);
            car.setClueId(clueId);
            car.setActivityId(activityId);
            count += clueActivityRelationDao.association(car);
        }
        boolean flag =  (count == activityIds.length);
        if(!flag)
            throw new ClueException("关联市场活动失败!");
        return flag;
    }

    @Transactional
    public boolean convert(String clueId, Tran t, String createBy, String createTime) throws ClueException {
        //1、通过线索的id得到线索对象,由线索对象可以获取线索的相关信息
        Clue c = clueDao.getClueById(clueId);
        //2、通过线索对象提取客户信息(要根据公司名精确匹配),当客户不存在时,新建
        String company = c.getCompany();
        Customer cus = customerDao.getCustomerByName(company);
        //如果cus = null 新建客户
        if(cus == null){
            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setOwner(c.getOwner());
            cus.setAddress(c.getAddress());
            cus.setName(c.getCompany());
            cus.setWebsite(c.getWebsite());
            cus.setPhone(c.getPhone());
            cus.setCreateBy(createBy);
            cus.setCreateTime(createTime);
            cus.setDescription(c.getDescription());
            cus.setNextContactTime(c.getNextContactTime());
            cus.setContactSummary(c.getContactSummary());
            if(customerDao.save(cus) != 1)
                throw new ClueException("线索转换失败-->新建客户出错");
        }
        //3、通过线索对象提取联系人信息,保存联系人
        Contacts con = new Contacts();
        con.setId(UUIDUtil.getUUID());
        con.setAddress(c.getAddress());
        con.setAppellation(c.getAppellation());
        con.setContactSummary(c.getContactSummary());
        con.setCreateBy(createBy);
        con.setCreateTime(createTime);
        con.setCustomerId(cus.getId());
        con.setFullname(c.getFullname());
        con.setEmail(c.getEmail());
        con.setSource(c.getSource());
        con.setOwner(c.getOwner());
        con.setNextContactTime(c.getNextContactTime());
        con.setMphone(c.getMphone());
        con.setJob(c.getJob());
        con.setDescription(c.getDescription());
        if(contactsDao.save(con) != 1)
            throw new ClueException("线索转换失败-->保存联系人出错");

        //4、将线索备注转换为联系人备注和客户备注
        List<ClueRemark> clueRemarks = clueRemarkDao.getByClueId(clueId);
        for (ClueRemark clueRemark : clueRemarks) {
            CustomerRemark cr = new CustomerRemark();
            cr.setId(UUIDUtil.getUUID());
            cr.setCreateBy(createBy);
            cr.setCreateTime(createTime);
            cr.setEditFlag("0");
            cr.setNoteContent(clueRemark.getNoteContent());
            cr.setCustomerId(cus.getId());
            if(customerRemarkDao.save(cr) != 1)
                throw new ClueException("线索转换失败-->线索备注转换为客户备注出错");

            ContactsRemark cor = new ContactsRemark();
            cor.setId(UUIDUtil.getUUID());
            cor.setCreateBy(createBy);
            cor.setCreateTime(createTime);
            cor.setEditFlag("0");
            cor.setNoteContent(clueRemark.getNoteContent());
            cor.setContactsId(con.getId());
            if (contactsRemarkDao.save(cor) != 1)
                throw new ClueException("线索转换失败-->线索备注转换为联系人备注出错");

        }

        //5、线索和市场活动的关系转换到联系人和市场活动的关系
        List<String> activityIds = clueActivityRelationDao.getByClueId(clueId);
        for (String activityId : activityIds) {
            ContactsActivityRelation car = new ContactsActivityRelation();
            car.setId(UUIDUtil.getUUID());
            car.setContactsId(con.getId());
            car.setActivityId(activityId);
            if(contactsActivityRelationDao.association(car) != 1)
                throw new ClueException("线索转换失败-->线索和市场活动的关系转换到联系人和市场活动的关系出错");
        }

        //6、如果有创建交易的需求，创建一条交易
        //根据线索中的信息先完善交易信息
        if(t != null){
            t.setNextContactTime(c.getNextContactTime());
            t.setOwner(c.getOwner());
            t.setContactsId(con.getId());
            t.setCustomerId(cus.getId());
            t.setSource(c.getSource());
            t.setDescription(c.getDescription());
            t.setContactSummary(c.getContactSummary());
            if(tranDao.save(t) != 1)
                throw new ClueException("线索转换失败-->创建交易出错");

            //7、如果创建了交易，建立一条交易历史
            TranHistory th = new TranHistory();
            th.setId(UUIDUtil.getUUID());
            th.setCreateBy(createBy);
            th.setCreateTime(createTime);
            th.setMoney(t.getMoney());
            th.setStage(t.getStage());
            th.setTranId(t.getId());
            th.setExpectedDate(t.getExpectedDate());
            if(tranHistoryDao.save(th) != 1)
                throw new ClueException("线索转换失败-->创建交易历史出错");
        }
        //8、删除线索备注
        if(clueRemarks.size() != clueRemarkDao.deleteByClueId(clueId))
            throw new ClueException("线索转换失败-->删除线索备注出错");

        //9、删除线索和市场活动的关系
        if(activityIds.size() != clueActivityRelationDao.deleteByClueId(clueId))
            throw new ClueException("线索转换失败-->删除线索和市场活动的关系出错");

        //10、删除线索
        if(1 != clueDao.deleteByClueId(clueId))
            throw new ClueException("线索转化失败-->删除线索出错");

        return true;
    }
}
