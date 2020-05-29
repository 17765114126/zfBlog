package com.yaa.controller.admin;

import com.github.pagehelper.PageInfo;
import com.yaa.controller.base.BaseController;
import com.yaa.model.Contents;
import com.yaa.model.Users;
import com.yaa.model.bo.ResponseBo;
import com.yaa.service.PagesService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/admin/pages")
public class PagesController extends BaseController {

    @Resource
    private PagesService pagesService;

    /**
     * 页面管理页
     * @return
     */
    @RequestMapping(value = "")
    public String pages(@RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "10") int limit,HttpServletRequest request){
        PageInfo<Contents> pages = pagesService.selectPages(page,limit);
        request.setAttribute("pages",pages);
        request.setAttribute("active","pages");
        return "admin/pages";
    }

    /**
     * 新增页
     * @param request
     * @return
     */
    @GetMapping(value = "/goPublish")
    public String goPublish(HttpServletRequest request){
        pagesService.goPublish(request);
        return "admin/page_edit";
    }

    /**
     * 删除页面
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/delete")
    public ResponseBo deletePage(@RequestParam(value = "id", defaultValue = "0") Integer id){
        return pagesService.deletePages(id);
    }

    /**
     * 修改页面-详情查询
     * @param id
     * @param request
     * @return
     */
    @GetMapping(value = "/edit/{id}")
    public String toSave(@PathVariable Integer id,HttpServletRequest request){
        pagesService.editPages(id,request);
        return "admin/page_edit";
    }

    /**
     * 修改页面
     * @param contents
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/update")
    public ResponseBo toUpdate(Contents contents){
        return pagesService.updatePage(contents);
    }

    /**
     * 新增页面
     * @param contents
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/publish")
    public ResponseBo publishPage(HttpServletRequest request,Contents contents){
        Users users = this.user(request);
        return pagesService.publishPage(contents,users);
    }


}
