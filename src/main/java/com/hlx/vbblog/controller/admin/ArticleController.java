package com.hlx.vbblog.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.anntation.OperationLog;
import com.hlx.vbblog.common.Constant;
import com.hlx.vbblog.common.JsonResult;
import com.hlx.vbblog.common.TableResult;
import com.hlx.vbblog.model.Article;
import com.hlx.vbblog.query.ArticleQuery;
import com.hlx.vbblog.service.ArticleService;
import com.hlx.vbblog.utils.UserInfoUtil;
import com.hlx.vbblog.vo.ArticleVO;
import com.hlx.vbblog.vo.AuditVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Api(tags = "后台：文章管理")
@RestController
@RequestMapping("/admin/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @ApiOperation("查询文章")
    @PreAuthorize("hasAuthority('blog:article:query')")
    @OperationLog("查询文章")
    @GetMapping
    public TableResult listTableByPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                       @RequestParam(value = "limit", defaultValue = "15") Integer limit,
                                       ArticleQuery articleQuery) {
        Page<Article> pageInfo = articleService.listTableByPage(page, limit, articleQuery);
        return TableResult.tableOk(pageInfo.getRecords(), pageInfo.getTotal());
    }

    @ApiOperation("新增文章")
    @PreAuthorize("hasAuthority('blog:article:add')")
    @OperationLog("新增文章")
    @PostMapping
    public JsonResult save(@Validated @RequestBody Article article) {
        article.setViews(0);
        article.setLikes(0);
        article.setComments(0);
        article.setCreateTime(new Date());
        article.setUpdateTime(article.getCreateTime());
        article.setAuthorId(UserInfoUtil.getId());
        article.setStatus(Constant.AUDIT_WAIT);
        articleService.saveOrUpdate(article);
        return JsonResult.ok();
    }

    @ApiOperation("更新文章")
    @PreAuthorize("hasAuthority('blog:article:edit')")
    @OperationLog("更新文章")
    @PutMapping
    public JsonResult update(@Validated @RequestBody ArticleVO articleVo) {
        articleVo.setUpdateTime(new Date());
        articleVo.setStatus(Constant.AUDIT_PASS);  // 更新文章不需要重新审核
        articleService.saveOrUpdate(articleVo);
        return JsonResult.ok();
    }

    @ApiOperation("删除文章")
    @PreAuthorize("hasAuthority('blog:article:delete')")
    @OperationLog("删除文章")
    @DeleteMapping("/{id}")
    public JsonResult remove(@NotNull @PathVariable("id") Long id) {
        articleService.removeById(id);
        return JsonResult.ok();
    }

    @ApiOperation("批量删除文章")
    @PreAuthorize("hasAuthority('blog:article:delete')")
    @OperationLog("批量删除文章")
    @DeleteMapping
    public JsonResult batchRemove(@RequestBody List<Long> idList) {
        articleService.removeByIdList(idList);
        return JsonResult.ok();
    }

    @ApiOperation("审核文章")
    @PreAuthorize("hasAuthority('blog:article:audit')")
    @OperationLog("审核文章")
    @PutMapping("/audit")
    public JsonResult audit(@Validated @RequestBody AuditVO auditVO) {
        articleService.audit(auditVO);
        return JsonResult.ok();
    }
}
