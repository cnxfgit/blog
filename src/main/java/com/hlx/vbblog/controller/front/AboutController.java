package com.hlx.vbblog.controller.front;

import com.hlx.vbblog.anntation.AccessLog;
import com.hlx.vbblog.service.ArticleService;
import com.hlx.vbblog.service.CategoryService;
import com.hlx.vbblog.service.TagService;
import com.hlx.vbblog.utils.DateUtil;
import com.hlx.vbblog.vo.AboutVO;
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

@Api(tags = "前台：关于我页面")
@RestController
public class AboutController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @ApiOperation("查询关于我页面数据")
    @AccessLog("访问关于我页面")
    @GetMapping("/about")
    public ResponseEntity<Object> about(@RequestParam(value = "dateType", required = false) Integer dateFilterType) {
        AboutVO aboutVO = new AboutVO();
        aboutVO.setArticleCount(articleService.countAll());
        aboutVO.setCategoryCount(categoryService.countAll());
        aboutVO.setTagCount(tagService.countAll());
        aboutVO.setCategories(categoryService.listByArticleCount());
        aboutVO.setTags(tagService.listByArticleCount());
        List<ArticleDateVO> articleDates = articleService.countByDate(dateFilterType);
        for (ArticleDateVO articleDate : articleDates) {
            articleDate.setDate(DateUtil.formatDate(articleDate.getYear(), articleDate.getMonth(), articleDate.getDay()));
            articleDate.setYear(null);
            articleDate.setMonth(null);
            articleDate.setDay(null);
        }
        aboutVO.setArticleDates(articleDates);
        return new ResponseEntity<>(aboutVO, HttpStatus.OK);
    }
}
