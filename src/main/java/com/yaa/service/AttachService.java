package com.yaa.service;

import com.github.pagehelper.PageInfo;
import com.yaa.model.Attach;
import com.yaa.model.bo.ResponseBo;


public interface AttachService {

    PageInfo<Attach> index(int page, int limit);

    int save(String fname, String fkey, String ftype, Integer author);

    ResponseBo delete(Integer id);

}
