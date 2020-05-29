package com.yaa.model.bo;

import lombok.Data;

import java.io.Serializable;
@Data
public class InstallBo implements Serializable {

    private String siteName;
    private String siteUrl;
    private String email;
    private String account;
    private String password;
    private String confirmPwd;

}
