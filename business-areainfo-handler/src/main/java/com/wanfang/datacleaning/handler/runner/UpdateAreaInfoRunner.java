package com.wanfang.datacleaning.handler.runner;

import com.wanfang.datacleaning.handler.service.AreaInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/12/28 21:06 
 *  @Version  V1.0   
 */
@Component
@Order(1)
public class UpdateAreaInfoRunner implements CommandLineRunner {

    @Autowired
    private AreaInfoService areaInfoService;

    @Override
    public void run(String... args) {

    }
}
