package com.yaa.service.impl;

import com.yaa.constant.WebConst;
import com.yaa.mapper.OptionsMapper;
import com.yaa.model.Options;
import com.yaa.model.bo.ResponseBo;
import com.yaa.service.SettingService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
public class SettingServiceImpl implements SettingService {

    private Logger logger = LoggerFactory.getLogger(SettingServiceImpl.class);

    @Resource
    private OptionsMapper optionsMapper;

    @Override
    public void index(HttpServletRequest request) {
        Map<String,String> options = WebConst.initConfig;
        request.setAttribute("options",options);
        request.setAttribute("active","setting");
    }


    @Override
    @Transactional
    public ResponseBo saveSite(Map<String, String> params) {
        try {
            if (null != params && !params.isEmpty()) {
                params.forEach(this::insertOption);
            }
            return ResponseBo.ok("保存成功！");
        }catch (Exception e){
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return ResponseBo.fail("保存失败！");
    }

    private void insertOption(String name, String value) {
        logger.info("Enter insertOption method:name={},value={}", name, value);
        Options options = new Options();
        options.setName(name);
        options.setValue(value);
        if (optionsMapper.selectByPrimaryKey(name) == null) {
            optionsMapper.insertSelective(options);
        } else {
            optionsMapper.updateByPrimaryKeySelective(options);
        }
        logger.info("Exit insertOption method.");
    }
}
