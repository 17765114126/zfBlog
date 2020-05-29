package com.yaa.controller.admin;

import com.yaa.controller.base.BaseController;
import com.yaa.model.Users;
import com.yaa.model.bo.ResponseBo;
import com.yaa.service.UserService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/admin")
public class LoginController extends BaseController {

    @Resource
    private UserService userService;

    @ResponseBody
    @PostMapping(value = "/login")
    public ResponseBo doLogin(Users users,HttpServletRequest request) {
        Integer error_count = cache.get("login_error_count");
        if (error_count==null || error_count < 3) {
            ResponseBo responseBo = userService.login(users.getUsername(), users.getPassword(), request);
            if (responseBo.getCode() == -5) {
                error_count = null == error_count ? 1 : error_count + 1;
                cache.set("login_error_count", error_count, 10 * 60);
            }
            return responseBo;
        }
        return ResponseBo.fail("您输入密码已经错误超过3次，请10分钟后尝试");
    }

}
