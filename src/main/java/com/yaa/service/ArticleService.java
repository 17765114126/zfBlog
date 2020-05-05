package com.yaa.service;

import com.github.pagehelper.PageInfo;
import com.yaa.model.Contents;
import com.yaa.model.Users;
import com.yaa.model.bo.ResponseBo;

import javax.servlet.http.HttpServletRequest;

/**
 * admin
 */
public interface ArticleService {

    PageInfo<Contents> selectArticlePage(int page, int limit);

    ResponseBo deleteArticle(Integer id);

    void editArticle(Integer id, HttpServletRequest request);

    void goPublish(HttpServletRequest request);

    ResponseBo updateArticle(Contents contents);

    ResponseBo addArticle(Contents contents, Users users);

}
