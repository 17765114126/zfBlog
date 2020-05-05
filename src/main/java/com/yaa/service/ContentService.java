package com.yaa.service;

import com.github.pagehelper.PageInfo;
import com.yaa.model.Comments;
import com.yaa.model.Contents;
import com.yaa.model.bo.ArchiveBo;

import java.util.List;

public interface ContentService {

    PageInfo<Contents> getContents(Integer p, Integer limit);

    Contents getContents(Integer cid);

    Contents getContents(String slug);

    void updateContentByCid(Contents contents);

    List<Contents> getContentsByKeyword(String keyword);

    List<Contents> getContentsByTags(String tag);

    //文章归档
    List<ArchiveBo> getArchives();

    Contents getNhContent(String type, Integer created);

    List<Contents> getNewContents(Integer limit);

    List<Contents> getAllowFeedContents();

    List<Contents> getContentsByCategories(String keyword);
}
