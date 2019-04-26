package com.weapp.quarz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.weapp.service.PublicService;

@Component
@Configurable
@EnableScheduling
public class ScheduledTasks{

	@Autowired
	private PublicService publicService;
    //每小时执行一次
    @Scheduled(cron = "0 */60 *  * * * ")
    public void reportCurrentByCron(){
    	publicService.refeshToken();
    }

}
