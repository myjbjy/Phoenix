package com.myjbjy.service;


import com.myjbjy.pojo.Users;
import com.myjbjy.pojo.bo.ModifyUserBO;

/**
 * @author myj
 */
public interface UserService {

    /**
     * 修改用户信息
     * @param userBO
     */
    public void modifyUserInfo(ModifyUserBO userBO);

    /**
     * 获得用户信息
     * @param uid
     * @return
     */
    public Users getById(String uid);

}
