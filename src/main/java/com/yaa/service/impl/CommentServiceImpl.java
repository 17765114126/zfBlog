package com.yaa.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yaa.mapper.CommentsMapper;
import com.yaa.model.Comments;
import com.yaa.model.Users;
import com.yaa.model.bo.CommentBo;
import com.yaa.model.bo.ResponseBo;
import com.yaa.model.vo.CommentsExample;
import com.yaa.service.CommentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentsMapper commentsMapper;

    @Override
    public PageInfo<CommentBo> getComments(Integer cid, int page, int limit) {
        if (null != cid) {
            PageHelper.startPage(page, limit);
            CommentsExample pExample = new CommentsExample();
            pExample.createCriteria().andCidEqualTo(cid).andParentEqualTo(0);
            pExample.setOrderByClause("coid desc");
            List<Comments> parents = commentsMapper.selectByExampleWithBLOBs(pExample);
            PageInfo<Comments> parentPage = new PageInfo<>(parents);
            PageInfo<CommentBo> returnBo = setPageInfo(parentPage);
            if (parents.size() != 0) {
                List<CommentBo> commentsBo = new ArrayList<>(parents.size());
                parents.forEach(parent -> {
                    CommentBo bo = new CommentBo(parent);
                    CommentsExample cExample = new CommentsExample();
                    cExample.createCriteria().andOwnerIdEqualTo(parent.getCoid());
                    cExample.setOrderByClause("coid asc");
                    List<Comments> children = commentsMapper.selectByExampleWithBLOBs(cExample);
                    if(children != null){
                        bo.setLevels(1);
                    }
                    bo.setChildren(children);
                    commentsBo.add(bo);
                });
                returnBo.setList(commentsBo);
            }
            return returnBo;
        }
        return null;
    }

    @Override
    public Comments getComments(Integer coid) {
        CommentsExample example = new CommentsExample();
        example.createCriteria().andCoidEqualTo(coid);
        List<Comments> comments = commentsMapper.selectByExample(example);
        if(comments != null){
            return comments.get(0);
        }
        return null;
    }

    @Override
    public int insertComments(Comments comments) {
        int count = commentsMapper.insertSelective(comments);
        return count;
    }

    @Override
    public List<Comments> recentComments(Integer limit) {
        PageHelper.startPage(1,limit);
        CommentsExample example =new CommentsExample();
        example.setOrderByClause("created desc");
        return commentsMapper.selectByExampleWithBLOBs(example);
    }

    //admin
    @Override
    public PageInfo<Comments> getCommentsWithPage(Users user, int page, int limit) {
        CommentsExample example = new CommentsExample();
        example.setOrderByClause("coid desc");
        example.createCriteria().andAuthorIdNotEqualTo(user.getUid());
        PageHelper.startPage(page, limit);
        List<Comments> comments = commentsMapper.selectByExampleWithBLOBs(example);
        PageInfo<Comments> pageInfo = new PageInfo<>(comments);
        return pageInfo;
    }

    @Override
    public ResponseBo deleteComments(Integer id) {
        if(id==0){
            return ResponseBo.fail("参数错误");
        }
        int count = commentsMapper.deleteByPrimaryKey(id);
        if(count>0){
            return ResponseBo.ok("删除成功！");
        }
        return ResponseBo.fail("删除失败");
    }

    /**
     * copy原有的分页信息，除数据
     * 返回格式不同，所以需要把父级的分页信息copy
     * @param ordinal
     * @param <T>
     * @return
     */
    private <T> PageInfo<T> setPageInfo(PageInfo ordinal) {
        PageInfo<T> returnBo = new PageInfo<T>();
        returnBo.setPageSize(ordinal.getPageSize());
        returnBo.setPageNum(ordinal.getPageNum());
        returnBo.setEndRow(ordinal.getEndRow());
        returnBo.setTotal(ordinal.getTotal());
        returnBo.setHasNextPage(ordinal.isHasNextPage());
        returnBo.setHasPreviousPage(ordinal.isHasPreviousPage());
        returnBo.setIsFirstPage(ordinal.isIsFirstPage());
        returnBo.setIsLastPage(ordinal.isIsLastPage());
        returnBo.setNavigateFirstPage(ordinal.getNavigateFirstPage());
        returnBo.setNavigateLastPage(ordinal.getNavigateLastPage());
        returnBo.setNavigatepageNums(ordinal.getNavigatepageNums());
        returnBo.setSize(ordinal.getSize());
        returnBo.setPrePage(ordinal.getPrePage());
        returnBo.setNextPage(ordinal.getNextPage());
        return returnBo;
    }
}
