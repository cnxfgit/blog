package com.hlx.vbblog.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.anntation.OperationLog;
import com.hlx.vbblog.common.JsonResult;
import com.hlx.vbblog.common.TableResult;
import com.hlx.vbblog.model.Comment;
import com.hlx.vbblog.model.User;
import com.hlx.vbblog.query.CommentQuery;
import com.hlx.vbblog.service.CommentService;
import com.hlx.vbblog.utils.StringUtils;
import com.hlx.vbblog.vo.ReplyVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Api(tags = "后台：评论管理")
@RestController
@RequestMapping("/admin/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ApiOperation("查询评论")
    @PreAuthorize("hasAuthority('blog:comment:query')")
    @OperationLog("查询评论")
    @GetMapping
    public JsonResult listTableByPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                      @RequestParam(value = "limit", defaultValue = "15") Integer limit,
                                      CommentQuery commentQuery) {
        Page<Comment> pageInfo = commentService.listTableByPage(page, limit, commentQuery);
        return TableResult.tableOk(pageInfo.getRecords(), pageInfo.getTotal());
    }

    @ApiOperation("删除评论")
    @PreAuthorize("hasAuthority('blog:comment:delete')")
    @OperationLog("删除评论")
    @DeleteMapping("/{id}")
    public JsonResult remove(@NotNull @PathVariable("id") Long id) {
        commentService.removeById(id);
        return JsonResult.ok();
    }

    @ApiOperation("批量删除评论")
    @PreAuthorize("hasAuthority('blog:comment:delete')")
    @OperationLog("批量删除评论")
    @DeleteMapping
    public JsonResult batchRemove(@NotEmpty @RequestBody List<Long> idList) {
        commentService.removeByIdList(idList);
        return JsonResult.ok();
    }

    @ApiOperation("回复评论")
    @PreAuthorize("hasAuthority('blog:comment:reply')")
    @OperationLog("回复评论")
    @PostMapping
    public JsonResult reply(@Validated({ReplyVO.CommentReply.class}) @RequestBody ReplyVO replyVO, HttpServletRequest request, HttpSession session) {
        Comment comment = new Comment();
        comment.setPid(replyVO.getPid());
        comment.setAid(replyVO.getAid());
        comment.setContent(replyVO.getReply());
        comment.setBrowser(StringUtils.getBrowser(request));
        comment.setOs(StringUtils.getClientOS(request));
        comment.setRequestIp(StringUtils.getIp(request));
        comment.setAddress(StringUtils.getCityInfo(comment.getRequestIp()));

        comment.setCreateTime(new Date());
        Object o = session.getAttribute("user");
        if (o != null) {
            User user = (User) o;
            comment.setUserId(user.getId());
            comment.setNickname(user.getUsername());
            comment.setAvatar(user.getAvatar());
        }
        commentService.reply(comment);
        return JsonResult.ok();
    }
}
