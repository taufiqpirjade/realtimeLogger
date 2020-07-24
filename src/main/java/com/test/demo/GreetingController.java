package com.test.demo;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class GreetingController {
	
	@Autowired
	private SimpMessagingTemplate template;
	
	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public Greeting greeting(HelloMessage message) throws Exception {
		Thread.sleep(1000); // simulated delay
		return new Greeting("Hello,!");
	}
	
	
	/*@PostMapping("/publishnew")
	public void send(@RequestBody HelloMessage message) throws Exception {
		fireGreeting(message.getName());
	}*/
	
	public void fireGreeting() throws IOException {
		Runnable r = new Runnable() {
			public void run() {
				File file = new File("E:\\usr\\mjjars\\log\\petrosmart.log");
		        System.out.println(file.getAbsolutePath());
		        if(file.exists() && file.canRead()) {
		            long fileLength = file.length();
		            try {
						readFile(file,0L);
					} catch (IOException e) {
						e.printStackTrace();
					}
		            while(true) {
		                if(fileLength<file.length()) {
		                    try {
								readFile(file,fileLength);
							} catch (IOException e) {
								e.printStackTrace();
							}
		                    fileLength=file.length();
		                }
		            }
		        }
			}
		};
 
		Thread t = new Thread(r);
		// Lets run Thread in background..
		// Sometimes you need to run thread in background for your Timer application..
		t.start(); // starts thread in background..
		System.out.println("Started Thread for File Listener");
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
}
