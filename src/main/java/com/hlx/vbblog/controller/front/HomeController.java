package com.hlx.vbblog.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.anntation.AccessLog;
import com.hlx.vbblog.common.Constant;
import com.hlx.vbblog.dto.ArticleDocument;
import com.hlx.vbblog.exception.BadRequestException;
import com.hlx.vbblog.model.Article;
import com.hlx.vbblog.service.ArticleService;
import com.hlx.vbblog.vo.HomeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Api(tags = "前台：首页")
@RestController
public class HomeController {

    @Autowired
    private ArticleService articleService;

    @ApiOperation("查询首页数据")
    @AccessLog("访问首页")
    @GetMapping("/home")
    public ResponseEntity<Object> home() {
        HomeVO homeVo = new HomeVO();
        homeVo.setTopArticles(articleService.listTop());
        homeVo.setRecommendArticles(articleService.listUpdate());
        homeVo.setPageInfo(articleService.listPreviewByPage(1, Integer.parseInt(Constant.PAGE_SIZE)));
        return new ResponseEntity<>(homeVo, HttpStatus.OK);
    }


    @ApiOperation("查询文章")
    @GetMapping("/articles")
    public ResponseEntity<Object> articles(@RequestParam(value = "current", defaultValue = "1") Integer current,
                                           @RequestParam(value = "size", defaultValue = Constant.PAGE_SIZE) Integer size) {
        Page<Article> articlePage = articleService.listPreviewByPage(current, size);
        return new ResponseEntity<>(articlePage, HttpStatus.OK);
    }

    @ApiOperation("搜索文章")
    @GetMapping(value = "/articles/search")
    public ResponseEntity<Object> search(@RequestParam(value = "keyword") String keyword) {
        List<ArticleDocument> articleDocuments;
        try {
            articleDocuments = articleService.listByKeyword(keyword);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadRequestException("搜索失败");
        }
        return new ResponseEntity<>(articleDocuments, HttpStatus.OK);
    }
}
