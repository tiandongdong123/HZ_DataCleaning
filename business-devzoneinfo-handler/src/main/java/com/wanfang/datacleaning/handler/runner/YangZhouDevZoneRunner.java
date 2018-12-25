package com.wanfang.datacleaning.handler.runner;

import com.wanfang.datacleaning.handler.service.DevZoneTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author yifei
 * @date 2018/12/19
 */
@Component
@Order(1)
public class YangZhouDevZoneRunner implements CommandLineRunner {

    @Autowired
    private DevZoneTestService devZoneTestService;

    @Override
    public void run(String... strings) throws Exception {
        devZoneTestService.findDevZoneInfo();
    }
}
