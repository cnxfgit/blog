package com.hlx.vbblog.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.anntation.OperationLog;
import com.hlx.vbblog.common.JsonResult;
import com.hlx.vbblog.common.TableResult;
import com.hlx.vbblog.model.Notice;
import com.hlx.vbblog.query.NoticeQuery;
import com.hlx.vbblog.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Api(tags = "后台：公告管理")
@RestController
@RequestMapping("/admin/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @ApiOperation("查询公告")
    @PreAuthorize("hasAuthority('sys:notice:query')")
    @OperationLog("查询公告")
    @GetMapping
    public TableResult listTableByPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                       @RequestParam(value = "limit", defaultValue = "15") Integer limit,
                                       NoticeQuery noticeQuery) {
        Page<Notice> pageInfo = noticeService.listTableByPage(page, limit, noticeQuery);
        return TableResult.tableOk(pageInfo.getRecords(), pageInfo.getTotal());
    }

    @ApiOperation("删除公告")
    @PreAuthorize("hasAuthority('sys:notice:delete')")
    @OperationLog("删除公告")
    @DeleteMapping("/{id}")
    public JsonResult remove(@NotNull @PathVariable("id") Long id) {
        noticeService.removeById(id);
        return JsonResult.ok();
    }

    @ApiOperation("批量删除公告")
    @PreAuthorize("hasAuthority('sys:notice:delete')")
    @OperationLog("批量删除公告")
    @DeleteMapping
    public JsonResult batchRemove(@NotEmpty @RequestBody List<Long> idList) {
        noticeService.removeByIdList(idList);
        return JsonResult.ok();
    }

    @ApiOperation("新增公告")
    @PreAuthorize("hasAuthority('sys:notice:add')")
    @OperationLog("新增公告")
    @PostMapping
    public JsonResult save(@Validated @RequestBody Notice notice) {
        notice.setDisplay(notice.getDisplay() != null);
        notice.setCreateTime(new Date());
        notice.setUpdateTime(notice.getCreateTime());
        noticeService.saveOfUpdate(notice);
        return JsonResult.ok();
    }

    @ApiOperation("更新公告")
    @PreAuthorize("hasAuthority('sys:notice:edit')")
    @OperationLog("更新公告")
    @PutMapping
    public JsonResult update(@Validated @RequestBody Notice notice) {
        notice.setDisplay(notice.getDisplay() != null);
        notice.setUpdateTime(new Date());
        noticeService.saveOfUpdate(notice);
        return JsonResult.ok();
    }

}
