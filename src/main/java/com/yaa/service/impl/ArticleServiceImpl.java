package com.yaa.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yaa.dto.Types;
import com.yaa.mapper.ContentsMapper;
import com.yaa.mapper.MetasMapper;
import com.yaa.model.Contents;
import com.yaa.model.Metas;
import com.yaa.model.Users;
import com.yaa.model.bo.ResponseBo;
import com.yaa.model.vo.ContentsExample;
import com.yaa.model.vo.MetasExample;
import com.yaa.service.ArticleService;
import com.yaa.util.DateKit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;


@Service
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ContentsMapper contentsMapper;
    @Resource
    private MetasMapper metasMapper;

    @Override
    public PageInfo<Contents> selectArticlePage(int page,int limit) {
        ContentsExample example = new ContentsExample();
        example.setOrderByClause("created desc");
        example.createCriteria().andTypeEqualTo(Types.ARTICLE.getType());
        PageHelper.startPage(page, limit);
        List<Contents> contents = contentsMapper.selectByExample(example);
        return new PageInfo<>(contents);
    }

    @Override
    public ResponseBo deleteArticle(Integer id) {
        if(id==0){
            return ResponseBo.fail("参数错误");
        }

        int count = contentsMapper.deleteByPrimaryKey(id);
        if(count>0){
            return ResponseBo.ok("删除成功！");
        }
        return ResponseBo.fail("");
    }

    @Override
    public void editArticle(Integer id , HttpServletRequest request) {
        Contents contents = contentsMapper.selectByPrimaryKey(id);
        MetasExample example = new MetasExample();
        example.createCriteria().andTypeEqualTo(Types.CATEGORY.getType());
        List<Metas> categories = metasMapper.selectByExample(example);
        if(contents!=null){
            request.setAttribute("categories",categories);
            request.setAttribute("contents",contents);
        }
        request.setAttribute("active","articles");//菜单选中
    }

    @Override
    public void goPublish(HttpServletRequest request) {
        MetasExample example = new MetasExample();
        example.createCriteria().andTypeEqualTo(Types.CATEGORY.getType());
        List<Metas> categories = metasMapper.selectByExample(example);
        request.setAttribute("categories",categories);
        request.setAttribute("active","edit");//菜单选中
    }

    @Override
    public ResponseBo updateArticle(Contents contents) {
        if(contents==null){
            return ResponseBo.fail("参数错误");
        }
        validateTags(contents);
        contents.setModified(DateKit.getCurrentUnixTime());
        int count = contentsMapper.updateByPrimaryKeySelective(contents);
        if(count > 0){
            return ResponseBo.ok("保存成功！");
        }
        return ResponseBo.fail("error");
    }

    @Override
    public ResponseBo addArticle(Contents contents, Users users) {
        if(contents==null){
            return ResponseBo.fail("参数错误");
        }
        validateTags(contents);
        contents.setAuthorId(users.getUid());
        contents.setCreated(DateKit.getCurrentUnixTime());
        contents.setType(Types.ARTICLE.getType());
        contents.setHits(0);
        contents.setCommentsNum(0);
        contents.setFmtType("markdown");
        if (StringUtils.isBlank(contents.getCategories())) {
            contents.setCategories("默认分类");
        }
        int count = contentsMapper.insertSelective(contents);
        if(count > 0){
            ResponseBo responseBo = ResponseBo.ok("保存成功");
            responseBo.setPayload(contents);
            return responseBo;
        }
        return ResponseBo.fail("error");
    }

    private void validateTags(Contents contents){
        List<String> tags = Arrays.asList(contents.getTags().split(","));
        if(tags!= null && tags.size()>0){
            for(String tag : tags){
                MetasExample tagex = new MetasExample();
                tagex.createCriteria().andTypeEqualTo(Types.TAG.getType()).andNameEqualTo(tag);
                int count = metasMapper.countByExample(tagex);
                if(count <= 0){
                    Metas metas = new Metas();
                    metas.setType(Types.TAG.getType());
                    metas.setName(tag);
                    metas.setSlug(tag);
                    metasMapper.insertSelective(metas);
                }
            }
        }
    }
}
