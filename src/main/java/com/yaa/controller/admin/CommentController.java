package com.yaa.controller.admin;

import com.github.pagehelper.PageInfo;
import com.yaa.controller.base.BaseController;
import com.yaa.model.Comments;
import com.yaa.model.Users;
import com.yaa.model.bo.ResponseBo;
import com.yaa.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/admin/comments")
public class CommentController extends BaseController {

    @Resource
    private CommentService commentService;

    /**
     * 评论管理页
     * @return
     */
    @GetMapping(value = "")
    public String comments(@RequestParam(value = "page", defaultValue = "1") int page,
                           @RequestParam(value = "limit", defaultValue = "10") int limit,
                           HttpServletRequest request){
        Users users = this.user(request);
        PageInfo<Comments> comments = commentService.getCommentsWithPage(users,page,limit);
        request.setAttribute("comments",comments);
        request.setAttribute("active","true");
        return "admin/comments";
    }

    @ResponseBody
    @PostMapping(value = "/delete")
    public ResponseBo deleteComments(@RequestParam(value = "id", defaultValue = "0") Integer id){
        return commentService.deleteComments(id);
    }



}
