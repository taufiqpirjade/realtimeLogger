package com.test.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class Scheduler implements SchedulingConfigurer {
	
	@Autowired
	private SimpMessagingTemplate template;
	
	long  fileLength = 0L;
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		File file = new File("E:\\usr\\mjjars\\log\\petrosmart.log");
        if(file.exists() && file.canRead()) {
            fileLength = file.length();
            try {
				readFile(file,0L);
			} catch (IOException e) {
				e.printStackTrace();
			}
	     taskRegistrar.addTriggerTask(new Runnable() {
	            @Override
	            public void run() {
	               System.out.println("Running Schedular");
	               if(fileLength<file.length()) {
	                    try {
							readFile(file,fileLength);
						} catch (IOException e) {
							e.printStackTrace();
						}
	                    fileLength=file.length();
	                }
	               
	            }
	        }, new Trigger() {
	            @Override
	            public Date nextExecutionTime(TriggerContext triggerContext) {
	                Calendar nextExecutionTime = new GregorianCalendar();
	                Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
	                nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
	                nextExecutionTime.add(Calendar.MILLISECOND, getNewExecutionTime());
	                return nextExecutionTime.getTime();
	            }
	        });
        }
	}
	
	private int getNewExecutionTime() {
		//Load Your execution time from database or property file
		return 1000;
	}
	
	public void readFile(File file,Long fileLength) throws IOException {
        String line = null;
        BufferedReader in = new BufferedReader(new java.io.FileReader(file));
        in.skip(fileLength);
        while((line = in.readLine()) != null) {
        	this.template.convertAndSend("/topic/greetings", new Greeting(line));
        }
        in.close();
    }
	
	
	@Bean
    public TaskScheduler poolScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        scheduler.setPoolSize(1);
        scheduler.initialize();
        return scheduler;
    }

}
