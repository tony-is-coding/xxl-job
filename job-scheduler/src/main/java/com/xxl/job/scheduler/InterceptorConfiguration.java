package com.xxl.job.scheduler;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @desc TODO
 *
 * @author tony
 * @createDate 2024/4/26 5:07 下午
 */
//@Configuration
public class InterceptorConfiguration implements InitializingBean {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        sqlSessionFactory.getConfiguration().addInterceptor(new SqlLoggingInterceptor());
    }
}
