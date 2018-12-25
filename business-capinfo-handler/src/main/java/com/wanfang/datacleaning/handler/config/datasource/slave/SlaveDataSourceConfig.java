package com.wanfang.datacleaning.handler.config.datasource.slave;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author luqs
 * @date 2018/5/12
 */
@Configuration
@MapperScan(basePackages = "com.wanfang.datacleaning.dao.dao.slave", sqlSessionTemplateRef = "knowledgeBaseSqlSessionTemplate")
public class SlaveDataSourceConfig {
    @Bean(name = "knowledgeBaseDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.knowledge-base")
    public DataSource setDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "knowledgeBaseTransactionManager")
    public DataSourceTransactionManager setTransactionManager(@Qualifier("knowledgeBaseDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "knowledgeBaseSqlSessionFactory")
    public SqlSessionFactory setSqlSessionFactory(@Qualifier("knowledgeBaseDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:com/wanfang/datacleaning/dao/mapping/slave/*Mapper.xml"));
        return bean.getObject();
    }

    @Bean(name = "knowledgeBaseSqlSessionTemplate")
    public SqlSessionTemplate setSqlSessionTemplate(@Qualifier("knowledgeBaseSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
