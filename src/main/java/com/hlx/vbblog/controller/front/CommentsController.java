package com.hlx.vbblog.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.common.Constant;
import com.hlx.vbblog.common.JsonResult;
import com.hlx.vbblog.model.Comment;
import com.hlx.vbblog.service.CommentService;
import com.hlx.vbblog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Api(tags = "前台：评论页面")
@RestController
@RequestMapping("/comments")
public class CommentsController {

    @Autowired
    private CommentService commentService;

    @ApiOperation("新增评论")
    @PostMapping
    public JsonResult save(@Validated @RequestBody Comment comment, HttpServletRequest request) {
        if(comment.getNickname()==null||"".equals(comment.getNickname())){
            comment.setNickname("匿名");
        }
        comment.setAvatar(Constant.DEFAULT_AVATAR);
        comment.setCreateTime(new Date());
        comment.setBrowser(StringUtils.getBrowser(request));
        comment.setOs(StringUtils.getClientOS(request));
        comment.setRequestIp(StringUtils.getIp(request));
        comment.setAddress(StringUtils.getCityInfo(comment.getRequestIp()));
        commentService.save(comment);
        return JsonResult.ok();
    }

    @ApiOperation("查询文章评论")
    @GetMapping("/listByArticleId/{articleId}")
    public ResponseEntity<Object> listByArticleId(@PathVariable("articleId") Long articleId,
                                                  @RequestParam(value = "current", defaultValue = "1") Integer current,
                                                  @RequestParam(value = "size", defaultValue = Constant.COMMENT_PAGE_SIZE) Integer size) {
        Page<Comment> pageInfo = commentService.listByArticleId(articleId, current, size);
        return new ResponseEntity<>(pageInfo, HttpStatus.OK);
    }


}
