package com.yaa.service.impl;

import com.yaa.dto.Types;
import com.yaa.mapper.ContentsMapper;
import com.yaa.mapper.MetasMapper;
import com.yaa.model.Contents;
import com.yaa.model.Metas;
import com.yaa.model.bo.MetasBo;
import com.yaa.model.vo.ContentsExample;
import com.yaa.model.vo.MetasExample;
import com.yaa.service.MetasService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class MetasServiceImpl implements MetasService {

    @Resource
    private MetasMapper metasMapper;
    @Resource
    private ContentsMapper contentsMapper;

    @Override
    public List<MetasBo> getAllMetas() {
        MetasExample example = new MetasExample();
        example.createCriteria().andTypeEqualTo(Types.TAG.getType());
        List<Metas> metas = metasMapper.selectByExample(example);
        List<MetasBo> metasBos = new ArrayList<>();
        metas.forEach(m ->{
            MetasBo bo = new MetasBo();
            ContentsExample exm = new ContentsExample();
            exm.createCriteria().andStatusEqualTo(Types.PUBLISH.getType()).andTypeEqualTo(Types.ARTICLE.getType())
                    .andTagsLike("%"+m.getName()+"%");
            int count = contentsMapper.countByExample(exm);
            bo.setMetas(m);
            bo.setCount(count);
            metasBos.add(bo);
        });
        return metasBos;
    }
}
