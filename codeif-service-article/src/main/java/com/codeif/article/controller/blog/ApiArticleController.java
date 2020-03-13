package com.codeif.article.controller.blog;

import com.codeif.article.service.blog.ApiArticleService;
import com.codeif.constant.ArticleConst;
import com.codeif.constant.CommonConst;
import com.codeif.constant.RedisConstant;
import com.codeif.db.redis.service.RedisService;
import com.codeif.pojo.QueryVO;
import com.codeif.pojo.article.Article;
import com.codeif.utils.JsonData;
import com.codeif.utils.JsonUtil;
import com.querydsl.core.QueryResults;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "前台网站文章")
@RestController
@RequestMapping(value = "/api/ar/article", produces = "application/json")
public class ApiArticleController {

    @Autowired
    private ApiArticleService articleService;
    @Autowired
    private RedisService redisService;

    @ApiOperation(value = "查询集合", notes = "Article")
    @GetMapping
    public JsonData<Object> findArticleByCondition(Article article,String categoryId ,QueryVO queryVO) {
        if (ArticleConst.SORT_TYPE_HOT.equals(queryVO.getSortType())) {
            List<Object> hotList = redisService.lGet("1", 1, 1);
            return JsonData.success(hotList);
        }
	    QueryResults<Article> result = articleService.findArticleByCondition(article,categoryId,queryVO);
        return JsonData.success(result);
    }

    @GetMapping(value = "/{articleId}")
    public JsonData<Article> findArticleByPrimaryKey(@PathVariable String articleId) {
        Object mapJson = redisService.get(RedisConstant.REDIS_KEY_ARTICLE + articleId);
        if (true) {
	        Article articleResult = articleService.findArticleById(articleId);
	        redisService.set(RedisConstant.REDIS_KEY_ARTICLE + articleId, JsonUtil.toJsonString(articleResult), CommonConst.TIME_OUT_DAY);
            return JsonData.success(articleResult);
        }
	    Article article = JsonUtil.jsonToPojo(mapJson, Article.class);
        return JsonData.success(article);
    }


}
