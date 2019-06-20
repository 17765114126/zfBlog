package com.yaa.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yaa.dto.Types;
import com.yaa.mapper.ContentsMapper;
import com.yaa.model.Contents;
import com.yaa.model.bo.ArchiveBo;
import com.yaa.model.vo.ContentsExample;
import com.yaa.model.vo.ContentsExample.Criteria;
import com.yaa.service.ContentService;
import com.yaa.util.DateKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentsMapper contentsMapper;

    @Override
    public PageInfo<Contents> getContents(Integer p, Integer limit) {
        ContentsExample example = new ContentsExample();
        example.setOrderByClause("created desc");
        example.createCriteria().andTypeEqualTo(Types.ARTICLE.getType()).andStatusEqualTo(Types.PUBLISH.getType());
        PageHelper.startPage(p, limit);
        List<Contents> data = contentsMapper.selectByExampleWithBLOBs(example);
        PageInfo<Contents> pageInfo = new PageInfo<>(data);
        return pageInfo;
    }

    @Override
    public Contents getContents(Integer cid) {
        Contents contents = contentsMapper.selectByPrimaryKey(cid);
        return contents;
    }

    @Override
    public Contents getContents(String slug) {
        ContentsExample example = new ContentsExample();
        example.setOrderByClause("created desc");
        example.createCriteria().andTypeEqualTo(Types.PAGE.getType()).andStatusEqualTo(Types.PUBLISH.getType()).andSlugEqualTo(slug);
        List<Contents> contents = contentsMapper.selectByExampleWithBLOBs(example);
        if(contents!=null) {
            return contents.get(0);
        }
        return null;
    }

    @Override
    public void updateContentByCid(Contents contents) {
        if (null != contents && null != contents.getCid()) {
            contentsMapper.updateByPrimaryKeySelective(contents);
        }
    }

    @Override
    public List<Contents> getContentsByKeyword(String keyword) {
        ContentsExample example = new ContentsExample();
        Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo(Types.ARTICLE.getType());
        criteria.andTitleLike("%"+keyword+"%");
        example.setOrderByClause("created desc");
        return contentsMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public List<Contents> getContentsByTags(String tag) {
        ContentsExample example = new ContentsExample();
        example.createCriteria().andStatusEqualTo(Types.PUBLISH.getType()).andTypeEqualTo(Types.ARTICLE.getType())
                .andTagsLike("%"+tag+"%");
        example.setOrderByClause("created desc");
        return contentsMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public List<ArchiveBo> getArchives() {
        List<ArchiveBo> archives = contentsMapper.findReturnArchiveBo();
        if (null != archives) {
            archives.forEach(archive -> {
                ContentsExample example = new ContentsExample();
                ContentsExample.Criteria criteria = example.createCriteria().andTypeEqualTo(Types.ARTICLE.getType()).andStatusEqualTo(Types.PUBLISH.getType());
                example.setOrderByClause("created desc");
                String date = archive.getDate();
                Date sd = DateKit.dateFormat(date, "yyyy年MM月");
                int start = DateKit.getUnixTimeByDate(sd);
                int end = DateKit.getUnixTimeByDate(DateKit.dateAdd(DateKit.INTERVAL_MONTH, sd, 1)) - 1;
                criteria.andCreatedGreaterThan(start);
                criteria.andCreatedLessThan(end);
                List<Contents> contents = contentsMapper.selectByExample(example);
                archive.setArticles(contents);
            });
        }
        return archives;
    }

    @Override
    public Contents getNhContent(String type, Integer created) {
        PageHelper.startPage(1, 1);
        ContentsExample example = new ContentsExample();
        Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo(Types.ARTICLE.getType()).andStatusEqualTo(Types.PUBLISH.getType());
        if (Types.NEXT.getType().equals(type)) {
            criteria.andCreatedGreaterThan(created);
            example.setOrderByClause("created asc");
        }
        if (Types.PREV.getType().equals(type)) {
            criteria.andCreatedLessThan(created);
            example.setOrderByClause("created desc");
        }
        List<Contents> contents = contentsMapper.selectByExampleWithBLOBs(example);
        if(contents.size()>0){
            return contents.get(0);
        }
        return null;
    }

    @Override
    public List<Contents> getNewContents(Integer limit) {
        PageHelper.startPage(1, limit);
        ContentsExample example = new ContentsExample();
        example.createCriteria().andStatusEqualTo(Types.PUBLISH.getType()).andTypeEqualTo(Types.ARTICLE.getType());
        example.setOrderByClause("created desc");
        List<Contents> contents = contentsMapper.selectByExample(example);
        return contents;
    }

    @Override
    public List<Contents> getAllowFeedContents() {
        ContentsExample example = new ContentsExample();
        example.createCriteria().andStatusEqualTo(Types.PUBLISH.getType()).andTypeEqualTo(Types.ARTICLE.getType()).andAllowFeedEqualTo(1);
        example.setOrderByClause("created desc");
        List<Contents> contents = contentsMapper.selectByExampleWithBLOBs(example);
        return contents;
    }

    @Override
    public List<Contents> getContentsByCategories(String keyword) {
        ContentsExample example = new ContentsExample();
        example.createCriteria().andCategoriesEqualTo(keyword).andTypeEqualTo(Types.ARTICLE.getType());
        List<Contents> contents = contentsMapper.selectByExample(example);
        return contents;
    }
}
