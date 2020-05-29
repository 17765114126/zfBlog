package com.yaa.controller.admin;

import com.github.pagehelper.PageInfo;
import com.yaa.config.PathConfig;
import com.yaa.constant.WebConst;
import com.yaa.dto.Types;
import com.yaa.model.Attach;
import com.yaa.model.Users;
import com.yaa.model.bo.ResponseBo;
import com.yaa.service.AttachService;
import com.yaa.util.BlogUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping(value = "/admin/files")
public class AttachController {

    @Resource
    private PathConfig pathConfig;

    @Resource
    private AttachService attachService;

    /**
     * 文件管理页
     * @return
     */
    @RequestMapping(value = "")
    public String files(@RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "6") int limit,
                        HttpServletRequest request){
        PageInfo<Attach> attachs = attachService.index(page,limit);
        request.setAttribute("active","files");
        request.setAttribute("attachs",attachs);
        return "admin/files";
    }

    /**
     * 上传文件接口
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/upload")
    public ResponseBo upload(HttpServletRequest request, @RequestParam("file") MultipartFile multipartFile) {
        UUID uuid = UUID.randomUUID();
        Users users = BlogUtils.getLoginUser(request);
        Integer uid = users.getUid();
        try {
            String fname = multipartFile.getOriginalFilename();
            if (multipartFile.getSize() <= WebConst.MAX_FILE_SIZE) {
                String fKey = uuid + "." +BlogUtils.getFileExt(fname);
                String fType = BlogUtils.isImage(multipartFile.getInputStream()) ? Types.IMAGE.getType() : Types.FILE.getType();
                String filePath = BlogUtils.getFilePath(fKey,pathConfig.getFilePath());
                File file = new File(filePath);
                try {
                    FileCopyUtils.copy(multipartFile.getInputStream(), new FileOutputStream(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int count = attachService.save(fname, fKey, fType, uid);
                if(count > 0) {
                    return ResponseBo.ok("上传文件成功！");
                }
            }
        } catch (Exception e) {
            ExceptionUtils.getStackTrace(e);
        }
        return ResponseBo.fail("上传文件失败！");
    }

    @ResponseBody
    @PostMapping(value = "/delete")
    public ResponseBo delete(@RequestParam Integer id){
        return attachService.delete(id);
    }

}
