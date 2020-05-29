package com.yaa.service.impl;

import com.yaa.constant.WebConst;
import com.yaa.model.bo.InstallBo;
import com.yaa.model.bo.ResponseBo;
import com.yaa.service.InstallService;
import com.yaa.service.OptionsServer;
import com.yaa.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class InstallServiceImpl implements InstallService {

    @Resource
    private OptionsServer optionsServer;
    @Resource
    private UserService userService;

    @Override
    public ResponseBo saveInstall(InstallBo installBo) {
        if(installBo == null){
            return ResponseBo.fail("参数错误");
        }
        if(StringUtils.isBlank(installBo.getSiteName())){
            return ResponseBo.fail("网站名称不能为空");
        }
        if(StringUtils.isBlank(installBo.getSiteUrl())){
            return ResponseBo.fail("网站地址不能为空");
        }
        if(!installBo.getSiteUrl().startsWith("http://") && !installBo.getSiteUrl().startsWith("https://")){
            return ResponseBo.fail("网站地址请加：http://或者https://");
        }
        if(StringUtils.isBlank(installBo.getEmail())){
            return ResponseBo.fail("邮箱不能为空");
        }
        if(StringUtils.isBlank(installBo.getPassword())){
            return ResponseBo.fail("密码不能为空");
        }
        if(installBo.getPassword().length() < 6){
            return ResponseBo.fail("密码不能小于6位");
        }
        if(StringUtils.isBlank(installBo.getConfirmPwd())){
            return ResponseBo.fail("确认密码不能为空");
        }
        if(!StringUtils.equals(installBo.getPassword(),installBo.getConfirmPwd())){
            return ResponseBo.fail("两次密码不同");
        }
        int count = optionsServer.updateOptions(installBo);
        if(count > 0){
            count += userService.createUser(installBo);
            if(count > 0){
                //更新参数
                WebConst.initConfig = optionsServer.getOptionsConfig();
                return ResponseBo.ok("安装博客成功！");
            }
        }
        return ResponseBo.fail("系统错误");
    }
}
