package com.hlx.vbblog.controller.admin;

import com.hlx.vbblog.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Api(tags = "后台：页面路由")
@Controller
@RequestMapping("/admin")
public class AdminRouteController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private TagService tagService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private NoticeService noticeService;

    @ApiOperation("页面路由")
    @GetMapping("/page/{moduleName}/{pageName}")
    public ModelAndView getPage(@PathVariable("moduleName") String moduleName, @PathVariable("pageName") String pageName) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/" + moduleName + "/" + pageName);
        return modelAndView;
    }

    @ApiOperation("更新文章页面")
    @PreAuthorize("hasAuthority('blog:article:edit')")
    @GetMapping("/article/{id}")
    public String editArticle(@PathVariable("id") Long id, Model model) {
        model.addAttribute("article", articleService.getById(id));
        model.addAttribute("tagList", tagService.listByArticleId(id));
        return "admin/article/article-edit";
    }
    @ApiOperation("更新分类页面")
    @PreAuthorize("hasAuthority('blog:category:edit')")
    @GetMapping("/category/{id}")
    public String editCategory(@PathVariable("id") Long id, Model model) {
        model.addAttribute("category", categoryService.getById(id));
        return "admin/category/category-edit";
    }

    @ApiOperation("更新标签页面")
    @PreAuthorize("hasAuthority('blog:tag:edit')")
    @GetMapping("/tag/{id}")
    public String editTag(@PathVariable("id") Long id, Model model) {
        model.addAttribute("tag", tagService.getById(id));
        return "admin/tag/tag-edit";
    }

    @ApiOperation("更新用户页面")
    @PreAuthorize("hasAuthority('sys:user:edit')")
    @GetMapping("/user/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", userService.getById(id));
        return "admin/user/user-edit";
    }

    @ApiOperation("更新角色页面")
    @PreAuthorize("hasAuthority('sys:role:edit')")
    @GetMapping("/role/{id}")
    public String editRole(@PathVariable("id") Long id, Model model) {
        model.addAttribute("role", roleService.getById(id));
        return "admin/role/role-edit";
    }

    @ApiOperation("更新菜单页面")
    @PreAuthorize("hasAuthority('sys:menu:edit')")
    @GetMapping("/menu/{id}")
    public String editMenu(@PathVariable("id") Long id, Model model) {
        model.addAttribute("menu", menuService.getById(id));
        return "admin/menu/menu-edit";
    }

    @ApiOperation("回复评论页面")
    @PreAuthorize("hasAuthority('blog:comment:reply')")
    @GetMapping("/comment/add")
    public String replyComment(@RequestParam("pid") Long pid,
                               @RequestParam("aid") Long aid,
                               Model model) {
        model.addAttribute("pid", pid);
        model.addAttribute("aid", aid);
        return "admin/comment/comment-add";
    }

    @ApiOperation("回复留言页面")
    @PreAuthorize("hasAuthority('blog:message:reply')")
    @GetMapping("/message/{pid}")
    public String replyMessage(@PathVariable("pid") Long pid, Model model) {
        model.addAttribute("pid", pid);
        return "admin/message/message-add";
    }

    @ApiOperation("更新友链页面")
    @PreAuthorize("hasAuthority('blog:link:edit')")
    @GetMapping("/link/{id}")
    public String editLink(@PathVariable("id") Long id, Model model) {
        model.addAttribute("link", linkService.getById(id));
        return "admin/link/link-edit";
    }

    @ApiOperation("更新公告页面")
    @PreAuthorize("hasAuthority('sys:notice:edit')")
    @GetMapping("/notice/{id}")
    public String editNotice(@PathVariable("id") Long id, Model model) {
        model.addAttribute("notice", noticeService.getById(id));
        return "admin/notice/notice-edit";
    }
}
