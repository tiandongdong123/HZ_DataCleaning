package com.wanfang.datacleaning.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/9/22 15:50 
 *  @Version  V1.0   
 */
@SpringBootApplication(exclude = {
        // 禁掉自带的DataSourceAutoConfiguration，因为它会读取application.properties文件的spring.datasource.*属性并自动配置单数据源
        DataSourceAutoConfiguration.class
})
@EnableTransactionManagement
public class HandlerApplication {
    private static final Logger logger = LoggerFactory.getLogger(HandlerApplication.class);

    public static void main(String[] args) {
        // 启动服务
        ConfigurableApplicationContext context = SpringApplication.run(HandlerApplication.class, args);
        Environment env = context.getBean(Environment.class);
        logger.info("【{}】服务启动成功！tomcat的端口：【{}】", env.getProperty("spring.application.name"), env.getProperty("server.port"));
    }

    /**
     * 配置定时任务task的线程池
     *
     * @return TaskScheduler
     */
    @Bean
    public TaskScheduler taskScheduler() {
        // single threaded by default
//        return new ConcurrentTaskScheduler();

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        // 线程池大小(根据定时任务并发执行的实际情况，配置线程池大小)
        taskScheduler.setPoolSize(10);
        // 线程前缀
        taskScheduler.setThreadNamePrefix("taskScheduler_");
        return taskScheduler;
    }
}
