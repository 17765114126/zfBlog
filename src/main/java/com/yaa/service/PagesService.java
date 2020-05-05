package com.yaa.service;

import com.github.pagehelper.PageInfo;
import com.yaa.model.Contents;
import com.yaa.model.Users;
import com.yaa.model.bo.ResponseBo;

import javax.servlet.http.HttpServletRequest;

/**
 * admin
 */
public interface PagesService {

    PageInfo<Contents> selectPages(int page, int limit);

    ResponseBo deletePages(Integer id);

    void editPages(Integer id, HttpServletRequest request);

    void goPublish(HttpServletRequest request);

    ResponseBo updatePage(Contents contents);

    ResponseBo publishPage(Contents contents, Users users);


}
