package com.yaa.service.impl;

import com.yaa.constant.WebConst;
import com.yaa.mapper.UsersMapper;
import com.yaa.model.Users;
import com.yaa.model.bo.InstallBo;
import com.yaa.model.bo.ResponseBo;
import com.yaa.model.vo.UsersExample;
import com.yaa.model.vo.UsersExample.Criteria;
import com.yaa.service.UserService;
import com.yaa.util.BlogUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {


    @Resource
    private UsersMapper usersMapper;

    @Override
    public Users getUsers(int id) {
        return usersMapper.selectByPrimaryKey(id);
    }

    @Override
    public ResponseBo login(String username, String password , HttpServletRequest request) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return ResponseBo.fail("用户名和密码不能为空");
        }
        UsersExample example = new UsersExample();
        Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        long count = usersMapper.countByExample(example);
        if (count < 1) {
            return ResponseBo.fail("不存在该用户");
        }
        String pwd = BlogUtils.MD5encode(username + password);
        criteria.andPasswordEqualTo(pwd);
        List<Users> userVos = usersMapper.selectByExample(example);
        if (userVos.size() != 1) {
            ResponseBo responseBo = ResponseBo.fail(-5);
            responseBo.setMsg("用户名或密码错误");
            return responseBo;
        }
        request.getSession().setAttribute(WebConst.LOGIN_SESSION_KEY, userVos.get(0));
        return ResponseBo.ok();
    }

    @Override
    public int createUser(InstallBo installBo) {
        String username = installBo.getAccount();
        String password = installBo.getPassword();
        String pwd = BlogUtils.MD5encode(username + password);
        Users user = new Users();
        user.setEmail(installBo.getEmail());
        user.setUsername(username);
        user.setPassword(pwd);
        return usersMapper.insert(user);
    }
}
