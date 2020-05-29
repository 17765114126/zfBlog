package com.yaa.controller.admin;

import com.github.pagehelper.PageInfo;
import com.yaa.controller.base.BaseController;
import com.yaa.model.Contents;
import com.yaa.model.Users;
import com.yaa.model.bo.ResponseBo;
import com.yaa.service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/admin/articles")
public class ArticleController extends BaseController {

    @Resource
    private ArticleService articleService;

    /**
     * 文章管理页
     * @return
     */
    @GetMapping(value = "")
    public String index(@RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "10") int limit, HttpServletRequest request) {
        PageInfo<Contents> contentsPageInfo = articleService.selectArticlePage(page,limit);
        request.setAttribute("articles", contentsPageInfo);
        request.setAttribute("active","articles");
        return "admin/articles";
    }

    /**
     * 发布文章页
     * @return
     */
    @RequestMapping(value = "/goPublish")
    public String goPublish(HttpServletRequest request){
        articleService.goPublish(request);
        return "admin/article_edit";
    }

    /**
     * 修改文章-详情查询
     * @param id
     * @param request
     * @return
     */
    @GetMapping(value = "/edit/{id}")
    public String toSave(@PathVariable Integer id,HttpServletRequest request){
        articleService.editArticle(id,request);
        return "admin/article_edit";
    }

    /**
     * 删除文章
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/delete")
    public ResponseBo deleteArt(@RequestParam(value = "id", defaultValue = "0") Integer id){
        return articleService.deleteArticle(id);
    }


    /**
     * 修改文章
     * @param contents
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/update")
    public ResponseBo toUpdate(Contents contents){
        return articleService.updateArticle(contents);
    }


    /**
     * 发布文章
     * @param contents
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/publish")
    public ResponseBo addArticle(HttpServletRequest request,Contents contents){
        Users users = this.user(request);
        return articleService.addArticle(contents,users);
    }



}
