package com.yaa.service.impl;

import com.yaa.dto.Types;
import com.yaa.mapper.ContentsMapper;
import com.yaa.mapper.MetasMapper;
import com.yaa.model.Contents;
import com.yaa.model.Metas;
import com.yaa.model.bo.MetasBo;
import com.yaa.model.bo.ResponseBo;
import com.yaa.model.vo.ContentsExample;
import com.yaa.model.vo.MetasExample;
import com.yaa.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private MetasMapper metasMapper;
    @Resource
    private ContentsMapper contentsMapper;

    @Override
    public void getAllCategory(HttpServletRequest request) {
        MetasExample example = new MetasExample();
        example.createCriteria().andTypeEqualTo(Types.TAG.getType());
        List<Metas> metas = metasMapper.selectByExample(example);
        List<MetasBo> tags = selectTagCount(metas);
        example = new MetasExample();
        example.createCriteria().andTypeEqualTo(Types.CATEGORY.getType());
        List<Metas> categoryList = metasMapper.selectByExample(example);
        List<MetasBo> category = selectCategoryCount(categoryList);
        request.setAttribute("tags",tags);
        request.setAttribute("category",category);
        request.setAttribute("active","true");
    }

    @Override
    @Transactional
    public ResponseBo updateCategory(Metas metas) {
        if(metas==null){
            return ResponseBo.fail("参数错误");
        }
        if(StringUtils.isBlank(metas.getName())){
            return ResponseBo.fail("类型名称不能为空");
        }
        ContentsExample example = new ContentsExample();
        example.createCriteria().andCategoriesEqualTo(metas.getName());
        List<Contents> list = contentsMapper.selectByExample(example);
        if(list != null) {
            list.forEach(l -> {
                l.setCategories(metas.getName());
                contentsMapper.updateByPrimaryKeySelective(l);
            });
        }
        int count = metasMapper.updateByPrimaryKeySelective(metas);
        if(count > 0){
            return ResponseBo.ok("保存成功！");
        }
        return ResponseBo.fail("error");
    }

    @Override
    public ResponseBo addCategory(Metas metas) {
        if(metas==null){
            return ResponseBo.fail("参数错误");
        }
        if(StringUtils.isBlank(metas.getName())){
            return ResponseBo.fail("类型名称不能为空");
        }
        metas.setType(Types.CATEGORY.getType());
        int count = metasMapper.insert(metas);
        if(count > 0){
            return ResponseBo.ok("保存成功！");
        }
        return ResponseBo.fail("error");
    }

    @Override
    public ResponseBo deleteTag(Integer id) {
        if(id==0){
            return ResponseBo.fail("参数错误");
        }
        int count = metasMapper.deleteByPrimaryKey(id);
        if(count>0){
            return ResponseBo.ok("删除成功！");
        }
        return ResponseBo.fail("");
    }

    @Override
    public ResponseBo deleteCate(Integer id) {
        Metas metas = metasMapper.selectByPrimaryKey(id);
        if(metas == null){
            return ResponseBo.fail("参数错误");
        }
        ContentsExample example = new ContentsExample();
        example.createCriteria().andCategoriesEqualTo(metas.getName());
        List<Contents> list = contentsMapper.selectByExample(example);
        if(list != null) {
            list.forEach(l -> {
                l.setCategories("默认分类");
                contentsMapper.updateByPrimaryKeySelective(l);
            });
        }
        int count = metasMapper.deleteByPrimaryKey(id);
        if(count>0){
            return ResponseBo.ok("删除成功！");
        }
        return ResponseBo.fail("");
    }

    private List<MetasBo> selectTagCount(List<Metas> metas) {
        if (metas == null) {
            return null;
        }
        List<MetasBo> metasBos = new ArrayList<>();
        MetasBo bo = null;
        for (Metas m : metas) {
            bo = new MetasBo();
            ContentsExample exm = new ContentsExample();
            exm.createCriteria().andStatusEqualTo(Types.PUBLISH.getType()).andTypeEqualTo(Types.ARTICLE.getType())
                    .andTagsLike("%" + m.getName() + "%");
            int count = contentsMapper.countByExample(exm);
            bo.setMetas(m);
            bo.setCount(count);
            metasBos.add(bo);
        }
        return metasBos;
    }

    private List<MetasBo> selectCategoryCount(List<Metas> metas) {
        if (metas == null) {
            return null;
        }
        List<MetasBo> metasBos = new ArrayList<>();
        MetasBo bo = null;
        for (Metas m : metas) {
            bo = new MetasBo();
            ContentsExample exm = new ContentsExample();
            exm.createCriteria().andStatusEqualTo(Types.PUBLISH.getType()).andTypeEqualTo(Types.ARTICLE.getType())
                    .andCategoriesEqualTo(m.getName());
            int count = contentsMapper.countByExample(exm);
            bo.setMetas(m);
            bo.setCount(count);
            metasBos.add(bo);
        }
        return metasBos;
    }
}
