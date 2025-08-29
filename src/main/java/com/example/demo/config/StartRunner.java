package com.example.demo.config;

import com.example.demo.service.HookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartRunner implements CommandLineRunner {

    @Autowired
    private HookService hookService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("**********22BBS0064 -- Application started********");
        Thread.sleep(2000);
        hookService.processQualifier();
    }
}