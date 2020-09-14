package com.hlx.vbblog.controller.admin;

import com.hlx.vbblog.anntation.OperationLog;
import com.hlx.vbblog.model.User;
import com.hlx.vbblog.service.*;
import com.hlx.vbblog.vo.IndexVO;
import com.hlx.vbblog.vo.InitInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Api(tags = "后台：控制面板")
@Controller
@RequestMapping("/admin")
public class IndexController {

    @Autowired
    private MenuService menuService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TagService tagService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private AccessLogService accessLogService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private OperationLogService operationLogService;
    @Autowired
    private NoticeService noticeService;

    @ApiOperation("初始化菜单")
    @ResponseBody
    @GetMapping("/init")
    public ResponseEntity<Object> init(HttpSession session, HttpServletRequest request) {
        Long userId = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("userId")) {
                userId = Long.valueOf(cookie.getValue());
                break;
            }
        }
        if (userId == null) {
            Object o = session.getAttribute("user");
            if (o != null) {
                User user = (User) o;
                userId = user.getId();
            }
        }
        if (userId != null) {
            InitInfoVO initInfoVO = menuService.menu(userId);
            return new ResponseEntity<>(initInfoVO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("当前用户未登录，菜单初始化失败", HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation("访问后台首页")
    @OperationLog("访问后台首页")
    @GetMapping
    public String toIndex() {
        return "admin/home/index";
    }

    @ApiOperation("查询控制面板数据")
    @ResponseBody
    @GetMapping("/indexData")
    public ResponseEntity<Object> index() {
        IndexVO indexVO = new IndexVO();
        indexVO.setArticleCount(articleService.countAll());
        indexVO.setCategoryCount(categoryService.countAll());
        indexVO.setTagCount(tagService.countAll());
        indexVO.setCommentCount(commentService.countAll());
        indexVO.setUserCount(userService.countAll());
        indexVO.setVisitorCount(commentService.countAll());
        indexVO.setViewCount(accessLogService.countAll());
        indexVO.setMessageCount(messageService.countAll());
        indexVO.setAccessLogs(accessLogService.listNewest());
        indexVO.setOperationLogs(operationLogService.listNewest());
        indexVO.setComments(commentService.listNewest());
        indexVO.setMessages(messageService.listNewest());
        indexVO.setArticles(articleService.listNewest());
        indexVO.setFrontViews(accessLogService.countByLast7Days());
        indexVO.setBackViews(operationLogService.countByLast7Days());
        indexVO.setIncreasedViews(accessLogService.countByThirtyDays());
        indexVO.setIncreasedArticles(articleService.countByThirtyDays());
        indexVO.setIncreasedMessages(messageService.countByThirtyDays());
        indexVO.setIncreasedComments(commentService.countByThirtyDays());
        indexVO.setNotices(noticeService.listNewest());
        return new ResponseEntity<>(indexVO, HttpStatus.OK);
    }

}
