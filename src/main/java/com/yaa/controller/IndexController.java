package com.yaa.controller;

import com.github.pagehelper.PageInfo;
import com.vdurmont.emoji.EmojiParser;
import com.yaa.constant.WebConst;
import com.yaa.controller.base.BaseController;
import com.yaa.dto.ErrorCode;
import com.yaa.dto.Types;
import com.yaa.model.Comments;
import com.yaa.model.Contents;
import com.yaa.model.bo.*;
import com.yaa.service.CommentService;
import com.yaa.service.ContentService;
import com.yaa.service.InstallService;
import com.yaa.service.MetasService;
import com.yaa.util.BlogUtils;
import com.yaa.util.DateKit;
import com.yaa.util.IPUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
@RequestMapping(value = "")
@Controller
public class IndexController extends BaseController{

    private Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private ContentService contentService;
    @Autowired
    private MetasService metasService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private InstallService installService;


    /**
     * 安装页
     */
    @RequestMapping(value = "/install")
    public String install(HttpServletRequest request){
        if("1".equals(WebConst.initConfig.get("allow_install"))){
            return this.render404();
        }
        return this.render("install");
    }

    /**
     * 安装保存数值
     */
    @ResponseBody
    @RequestMapping(value = "/install/save")
    public ResponseBo saveInstall(InstallBo install){
        return installService.saveInstall(install);
    }

    /**
     * 首页
     * @param request
     * @param limit
     * @return
     */
    @RequestMapping(value = "/index")
    public String indexPage(HttpServletRequest request,@RequestParam(value = "limit", defaultValue = "12") int limit){
        return this.index(request, 1, limit);
    }

    /**
     * 首页分页
     *
     * @param request request
     * @param p       第几页
     * @param limit   每页大小
     * @return 主页
     */
    @RequestMapping(value = "/page/{p}")
    public String index(HttpServletRequest request, @PathVariable int p, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        p = p < 0 || p > WebConst.MAX_PAGE ? 1 : p;
        PageInfo<Contents> articles = contentService.getContents(p, limit);
        request.setAttribute("articles", articles);
        if (p > 1) {
            this.title(request, "第" + p + "页");
        }
        return this.render("index");
    }

    /**
     * 文章详情
     * @param request
     * @param cid
     * @return
     */
    @RequestMapping(value = "/article/{cid}")
    public String getArticle(HttpServletRequest request, @PathVariable Integer cid) {
        Contents contents = contentService.getContents(cid);
        if (null == contents || "draft".equals(contents.getStatus())) {
            return this.render404();
        }
        completeArticle(request, contents);
        request.setAttribute("article", contents);
        request.setAttribute("is_post", true);
        if (!checkHitsFrequency(request, cid)) {
            updateArticleHit(contents.getCid(), contents.getHits());
        }
        this.title(request,contents.getTitle());
        return this.render("article");

    }

    /**
     * 自定义页面
     * @param request
     * @param slug
     * @return
     */
    @RequestMapping(value = "/pages/{slug}")
    public String otherPage(HttpServletRequest request,@PathVariable String slug) {
        Contents contents = contentService.getContents(slug);
        if (null == contents || "draft".equals(contents.getStatus())) {
            return this.render404();
        }
        completeArticle(request,contents);
        request.setAttribute("article", contents);
        request.setAttribute("is_post", false);
        this.title(request,contents.getTitle());
        return this.render("page");
    }

    /**
     * 文章归档
     * @param request
     * @return
     */
    @RequestMapping(value = "/archives")
    public String archives(HttpServletRequest request) {
        List<ArchiveBo> archives = contentService.getArchives();
        request.setAttribute("archives", archives);
        this.title(request,"文章归档");
        return this.render("archives");
    }

    /**
     * 搜索页
     * @param request
     * @return
     */
    @RequestMapping(value = "/search")
    public String search(HttpServletRequest request){
        List<MetasBo> metas = metasService.getAllMetas();
        this.title(request,"搜索");
        request.setAttribute("metas",metas);
        return this.render("search");
    }

    /**
     * 搜索文章
     * @param request
     * @param keyword
     * @return
     */
    @RequestMapping(value = "/search/{keyword}")
    public String search(HttpServletRequest request,@PathVariable String keyword){
        List<Contents> articles = contentService.getContentsByKeyword(keyword);
        this.title(request,keyword);
        request.setAttribute("type","关键字");
        request.setAttribute("key",keyword);
        request.setAttribute("articles",articles);
        this.title(request,keyword);
        return this.render("result");
    }

    /**
     * 标签搜索
     * @param request
     * @param name
     * @return
     */
    @RequestMapping(value = "/tag/{name}")
    public String searchTag(HttpServletRequest request,@PathVariable String name){
        try {
            name = URLDecoder.decode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        List<Contents> articles = contentService.getContentsByTags(name);
        request.setAttribute("type","标签");
        request.setAttribute("key",name);
        request.setAttribute("articles",articles);
        this.title(request,name);
        return this.render("result");
    }

    /**
     * 分类
     * @param request
     * @param keyword
     * @return
     */
    @RequestMapping(value = "/category/{keyword}")
    public String searchCategories(HttpServletRequest request,@PathVariable String keyword){
        List<Contents> articles = contentService.getContentsByCategories(keyword);
        this.title(request,keyword);
        request.setAttribute("type","分类");
        request.setAttribute("key",keyword);
        request.setAttribute("articles",articles);
        this.title(request,keyword);
        return this.render("result");
    }

    /**
     * 游客评论
     * @param request
     * @param response
     * @param comments
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    public ResponseBo comment(HttpServletRequest request, HttpServletResponse response,Comments comments) {
        if (comments == null) {
            return ResponseBo.fail("请输入完整后评论");
        }
        String ref = request.getHeader("Referer");
        if (StringUtils.isBlank(ref)) {
            return ResponseBo.fail(ErrorCode.BAD_REQUEST);
        }
        if (comments.getCoid() != null) {
            comments.setParent(comments.getCoid());
        }
        if(comments.getAuthorId() == null){
            comments.setStatus("1");
        }
        if(StringUtils.isBlank(comments.getAuthor())){
            return ResponseBo.fail("姓名不能为空");
        }
        if (comments.getAuthor().length() > 12) {
            return ResponseBo.fail("姓名不能超过12个字符");
        }

        if (StringUtils.isBlank(comments.getMail()) && !BlogUtils.isEmail(comments.getMail())) {
            return ResponseBo.fail("请输入正确的邮箱格式");
        }

        if (StringUtils.isNotBlank(comments.getUrl()) && !BlogUtils.isURL(comments.getUrl())) {
            return ResponseBo.fail("请输入正确的URL格式");
        }

        if (comments.getContent() == null || comments.getContent().length() > 200) {
            return ResponseBo.fail("请输入200个字符以内的评论");
        }
        String val = IPUtils.getIpAddrByRequest(request) + ":" + comments.getCid();
        Integer count = cache.hget(Types.COMMENTS_FREQUENCY.getType(), val);
        if (null != count && count > 0) {
            return ResponseBo.fail("您发表评论太快了，请过会再试");
        }
        String author = BlogUtils.cleanXSS(comments.getAuthor());
        String text = BlogUtils.cleanXSS(comments.getContent());

        author = EmojiParser.parseToAliases(author);
        text = EmojiParser.parseToAliases(text);

        //未读
        comments.setStatus("0");
        comments.setCoid(null);
        comments.setAuthor(author);
        comments.setContent(text);
        comments.setIp(request.getRemoteAddr());
        comments.setCreated(DateKit.getCurrentUnixTime());
        try {
            String result = commentService.insertComments(comments) > 0 ? "SUCCESS" : "FAIL";
            BlogUtils.cookie("blog_remember_author", URLEncoder.encode(author, "UTF-8"), 7 * 24 * 60 * 60, response);
            BlogUtils.cookie("blog_remember_mail", URLEncoder.encode(comments.getMail(), "UTF-8"), 7 * 24 * 60 * 60, response);
            if(StringUtils.isNotBlank(comments.getUrl())){
                BlogUtils.cookie("blog_remember_url", URLEncoder.encode(comments.getUrl(), "UTF-8"), 7 * 24 * 60 * 60, response);
            }
            cache.hset(Types.COMMENTS_FREQUENCY.getType(), val, 1, 60);
            if (!WebConst.SUCCESS_RESULT.equals(result)) {
                return ResponseBo.fail(result);
            }
        }catch (Exception e){
            return ResponseBo.fail("发布文章评论失败");
        }
        return ResponseBo.ok();
    }

    /**
     * 获取RSS输出
     * @param response
     */
    @RequestMapping(value = "/feed")
    public void feed(HttpServletResponse response){
        List<Contents> articles = contentService.getAllowFeedContents();
        try {
            String xml = BlogUtils.getRssXml(articles);
            response.setContentType("text/xml; charset=utf-8");
            PrintWriter printWriter = response.getWriter();
            printWriter.write(xml);
        } catch (Exception e) {
            logger.error("feed article fail");
        }
    }

    /**
     * 获取文章评论
     * @param request
     * @param contents
     */
    private void completeArticle(HttpServletRequest request, Contents contents) {
        if (contents.getComment()) {
            String cp = request.getParameter("cp");
            if (StringUtils.isBlank(cp)) {
                cp = "1";
            }
            request.setAttribute("cp", cp);
            PageInfo<CommentBo> comments = commentService.getComments(contents.getCid(), Integer.parseInt(cp), 6);
            request.setAttribute("comments", comments);
        }
    }

    /**
     * 更新文章的点击率
     *
     * @param cid
     * @param chits
     */
    private void updateArticleHit(Integer cid, Integer chits) {
        Integer hits = cache.hget("article" + cid, "hits");
        if (chits == null) {
            chits = 0;
        }
        hits = null == hits ? 1 : hits + 1;
        if (hits >= WebConst.HIT_EXCEED) {
            Contents temp = new Contents();
            temp.setCid(cid);
            temp.setHits(chits + hits);
            contentService.updateContentByCid(temp);
            cache.hset("article" + cid, "hits", 1);
        } else {
            cache.hset("article" + cid, "hits", hits);
        }
    }

}
