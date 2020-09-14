package com.hlx.vbblog.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.anntation.AccessLog;
import com.hlx.vbblog.common.Constant;
import com.hlx.vbblog.model.Article;
import com.hlx.vbblog.query.ArchivesQuery;
import com.hlx.vbblog.service.ArticleService;
import com.hlx.vbblog.utils.DateUtil;
import com.hlx.vbblog.vo.ArchivesVO;
import com.hlx.vbblog.vo.ArticleDateVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "前台：归档页面")
@RestController
public class ArchivesController {

    @Autowired
    private ArticleService articleService;

    @ApiOperation("查询归档页面数据")
    @AccessLog("访问归档页面")
    @GetMapping("/archives")
    public ResponseEntity<Object> archives(@RequestParam(value = "dataType", required = false) Integer dateFilterType) {

        List<ArticleDateVO> articleDates = articleService.countByDate(dateFilterType);
        for (ArticleDateVO articleDate : articleDates) {
            articleDate.setDate(DateUtil.formatDate(articleDate.getYear(), articleDate.getMonth(), articleDate.getDay()));
            articleDate.setYear(null);
            articleDate.setMonth(null);
            articleDate.setDay(null);
        }

        Page<Article> pageInfo = articleService.listPreviewPageByDate(1, Integer.parseInt(Constant.PAGE_SIZE), null);

        ArchivesVO archivesVo = new ArchivesVO();
        archivesVo.setArticleDates(articleDates);
        archivesVo.setPageInfo(pageInfo);
        return new ResponseEntity<>(archivesVo, HttpStatus.OK);
    }

    @ApiOperation("查询文章数据")
    @GetMapping("/archives-articles")
    public ResponseEntity<Object> archivesArticles(@RequestParam(value = "current", defaultValue = "1") Integer current,
                                                   @RequestParam(value = "size", defaultValue = Constant.PAGE_SIZE) Integer size,
                                                   ArchivesQuery archivesQuery) {

        Page<Article> pageInfo = articleService.listPreviewPageByDate(current, size, archivesQuery);
        return new ResponseEntity<>(pageInfo, HttpStatus.OK);
    }

}
