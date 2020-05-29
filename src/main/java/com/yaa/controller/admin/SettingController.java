package com.yaa.controller.admin;

import com.yaa.config.PathConfig;
import com.yaa.constant.WebConst;
import com.yaa.controller.base.BaseController;
import com.yaa.model.bo.ResponseBo;
import com.yaa.service.SettingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/admin/setting")
public class SettingController extends BaseController {

    @Resource
    private SettingService settingService;
    @Resource
    private PathConfig pathConfig;

    /**
     * 系统设置页
     * @return
     */
    @RequestMapping(value = "")
    public String setting(HttpServletRequest request){
        settingService.index(request);
        return "admin/setting";
    }

    /**
     * 保存网站设置
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/saveSite")
    public ResponseBo saveSite(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> querys = new HashMap<>();
        parameterMap.forEach((key, value) -> {
            querys.put(key, join(value));
            WebConst.initConfig.put(key,join(value));
        });
        return settingService.saveSite(querys);
    }

    /**
     * 保存个人信息
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/savePro")
    public ResponseBo savePro(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> querys = new HashMap<>();
        parameterMap.forEach((key, value) -> {
            querys.put(key, join(value));
            WebConst.initConfig.put(key,join(value));
        });
        return settingService.saveSite(querys);
    }

    @RequestMapping(value = "/backup")
    public void backup(HttpServletResponse response) {
        try {
            //导出数据库
            Runtime rt = Runtime.getRuntime();
            Process child = rt.exec(pathConfig.getMysqlExec() + "");
            InputStream backupIn = child.getInputStream();
            InputStreamReader backupReader = new InputStreamReader(backupIn, "utf8");
            StringBuffer result = new StringBuffer("");
            BufferedReader br = new BufferedReader(backupReader);
            String instr = "";
            while ((instr = br.readLine()) != null) {
                result.append(instr + "\r\n");
            }
            String backupPath = pathConfig.getBackupPath();
            if (!new File(backupPath).exists()) {
                new File(backupPath).mkdirs();
            }
            FileOutputStream fout = new FileOutputStream(backupPath + "backup.sql");
            OutputStreamWriter writer = new OutputStreamWriter(fout, "utf8");
            writer.write(result.toString());
            writer.flush();
            backupIn.close();
            backupReader.close();
            br.close();
            writer.close();
            fout.close();
            //下载文件
            String filePath = pathConfig.getBackupPath();
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=backup.sql");
            byte[] buff = new byte[1024];
            BufferedInputStream bis = null;
            OutputStream os = null;
            os = response.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(new File(filePath + "backup.sql")));
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数组转字符串
     *
     * @param arr
     * @return
     */
    private String join(String[] arr) {
        StringBuilder ret = new StringBuilder();
        String[] var3 = arr;
        int var4 = arr.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            String item = var3[var5];
            ret.append(',').append(item);
        }

        return ret.length() > 0 ? ret.substring(1) : ret.toString();
    }

}
