package com.yaa.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yaa.config.PathConfig;
import com.yaa.dto.Types;
import com.yaa.mapper.AttachMapper;
import com.yaa.model.Attach;
import com.yaa.model.bo.ResponseBo;
import com.yaa.model.vo.AttachExample;
import com.yaa.service.AttachService;
import com.yaa.util.DateKit;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

@Service
public class AttachServiceImpl implements AttachService {

    @Resource
    private AttachMapper attachMapper;
    @Resource
    private PathConfig pathConfig;

    @Override
    public PageInfo<Attach> index(int page,int limit) {
        AttachExample example = new AttachExample();
        example.setOrderByClause("created desc");
        example.createCriteria().andFtypeEqualTo(Types.IMAGE.getType());
        PageHelper.startPage(page, limit);
        List<Attach> attaches = attachMapper.selectByExample(example);
        return new PageInfo<>(attaches);
    }

    @Override
    public int save(String fname, String fkey, String ftype, Integer author) {
        if(fname.length()>10){
            fname = fname.substring(0,9);
        }
        Attach attach = new Attach();
        attach.setFname(fname);
        attach.setAuthorId(author);
        attach.setFkey(fkey);
        attach.setFtype(ftype);
        attach.setCreated(DateKit.getCurrentUnixTime());
        return attachMapper.insertSelective(attach);
    }

    @Override
    public ResponseBo delete(Integer id) {
        Attach attach = attachMapper.selectByPrimaryKey(id);
        if(attach == null){
            return ResponseBo.fail("附件不存在！");
        }
        int count = attachMapper.deleteByPrimaryKey(id);
        if(count > 0){
            new File(pathConfig.getFilePath() + "/" + attach.getFkey()).delete();
            return ResponseBo.ok("删除附件成功");
        }
        return ResponseBo.fail("删除附件失败！");
    }
}
