package com.youyd.article.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youyd.article.pojo.Article;

import java.util.List;

/**
 * 文章接口
 */
public interface ArticleService {
	IPage<Article> findArticleByCondition(Article article);

	Article findArticleByPrimaryKey(String id);

	void insertArticle(Article article);

	void updateByPrimaryKeySelective(Article article);

	void deleteByIds(List articleIds);

	void examine(String id);

	int updateThumbup(String id);
}