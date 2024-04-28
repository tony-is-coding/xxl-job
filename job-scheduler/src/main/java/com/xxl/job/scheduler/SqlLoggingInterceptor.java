package com.xxl.job.scheduler;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 1.可以用来分析SQL执行效率
 * 2.可以用来获取实际执行的SQL
 */

@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})}
)
public class SqlLoggingInterceptor implements Interceptor {

    private static Logger log = LoggerFactory.getLogger(SqlLoggingInterceptor.class);

    /**
     * 最小打印时间 sql时间超过这个值才打印日志 毫秒
     **/
    private int MIN_SIZE = 0;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        String sqlId = mappedStatement.getId();
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();

        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = invocation.proceed();
        } finally {
            try {
                long sqlCostTime = System.currentTimeMillis() - startTime;
                String sql = getSql(configuration, boundSql);
                formatSqlLog(mappedStatement.getSqlCommandType(), sqlId, sql, sqlCostTime, result);
            } catch (Exception ignored) {
                log.error("SQL插件执行失败 Mapper:{} 参数对象：{}", sqlId, boundSql.getParameterObject(), ignored);
            }
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        if (properties == null) {
            return;
        }
        if (properties.containsKey("minLogSize")) {
            MIN_SIZE = Integer.valueOf(properties.getProperty("minLogSize"));
        }
    }

    private String getSql(Configuration configuration, BoundSql boundSql) {
        // 输入sql字符串空判断
        String sql = boundSql.getSql();
        if (Strings.isBlank(sql)) {
            return "";
        }

        //去掉换行符
        sql = sql.replaceAll("[\\s\n ]+", " ");

        //填充占位符, 目前基本不用mybatis存储过程调用,故此处不做考虑
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (!parameterMappings.isEmpty() && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = this.replacePlaceholder(sql, parameterObject);
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = replacePlaceholder(sql, obj);
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = replacePlaceholder(sql, obj);
                    }
                }
            }
        }
        return sql;
    }

    private String replacePlaceholder(String sql, Object parameterObject) {
        String result;
        if (parameterObject == null) {
            result = "NULL";
        } else if (parameterObject instanceof String) {
            result = String.format("'%s'", parameterObject.toString());
        } else if (parameterObject instanceof Date) {
            result = String.format("'%s'", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(parameterObject));
        } else {
            result = parameterObject.toString();
        }
        return sql.replaceFirst("\\?", result);
    }

    private void formatSqlLog(SqlCommandType sqlCommandType, String sqlId, String sql, long costTime, Object obj) {
        if (costTime > MIN_SIZE) {
            String[] split = sqlId.split("\\.");
            String methodName = split[split.length - 2] + "#" + split[split.length - 1] ;
            if (sqlCommandType == SqlCommandType.UPDATE || sqlCommandType == SqlCommandType.INSERT || sqlCommandType == SqlCommandType.DELETE) {
                log.info("\n({}|{} cost:{}ms,rows:{}) {}", methodName, sqlCommandType, costTime, obj,sql);
            }
            if (sqlCommandType == SqlCommandType.SELECT) {
                log.info("\n({}|{} cost:{}ms,rows:{}) {}",  methodName, sqlCommandType, costTime, ((Collection<?>) obj).size(),sql);
            }
        }
    }
}

