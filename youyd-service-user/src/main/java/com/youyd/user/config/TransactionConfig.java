package com.youyd.user.config;

import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置全局事务处理
 * {@link http://www.cnblogs.com/guozp/articles/7446477.html}
 * @author: LGG
 * @create: 2019-01-23
 **/
@Configuration
public class TransactionConfig {

	private static final int TX_METHOD_TIMEOUT = 5;
	private static final String AOP_POINTCUT_EXPRESSION = "execution (* com.youyd.user..*.*(..))";

	/**
	 * 配置事务拦截类型
	 * @return TransactionAttributeSource
	 */
	@Bean("txSource")
	public TransactionAttributeSource transactionAttributeSource() {
		NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
		/*只读配置类*/
		RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
		readOnlyTx.setReadOnly(true);
		readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
		RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED,
				Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
		requiredTx.setTimeout(TX_METHOD_TIMEOUT);
		Map<String, TransactionAttribute> txMap = new HashMap<>(9);
		txMap.put("add*", requiredTx);
		txMap.put("save*", requiredTx);
		txMap.put("insert*", requiredTx);
		txMap.put("update*", requiredTx);
		txMap.put("delete*", requiredTx);
		txMap.put("get*", readOnlyTx);
		txMap.put("find*", readOnlyTx);
		txMap.put("query*", readOnlyTx);
		source.setNameMap(txMap);

		return source;
	}

	/**
	 * 切面拦截规则 参数会自动从容器中注入
	 */
	@Bean
	public AspectJExpressionPointcutAdvisor pointcutAdvisor(TransactionInterceptor txInterceptor) {
		AspectJExpressionPointcutAdvisor pointcutAdvisor = new AspectJExpressionPointcutAdvisor();
		pointcutAdvisor.setAdvice(txInterceptor);
		pointcutAdvisor.setExpression(AOP_POINTCUT_EXPRESSION);
		return pointcutAdvisor;
	}

	/**
	 * 事务拦截器
	 * @param tx:注入的事务管理器
	 * @return TransactionInterceptor
	 */
	@Bean("txInterceptor")
	TransactionInterceptor getTransactionInterceptor(PlatformTransactionManager tx) {
		return new TransactionInterceptor(tx, transactionAttributeSource());
	}
}