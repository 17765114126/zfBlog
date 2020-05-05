package com.yaa.service;

import com.yaa.model.Users;
import com.yaa.model.bo.InstallBo;
import com.yaa.model.bo.ResponseBo;

import javax.servlet.http.HttpServletRequest;

/**
 * admin
 */
public interface UserService {

    Users getUsers(int id);

    ResponseBo login(String username, String password, HttpServletRequest request);

    int createUser(InstallBo installBo);

}
