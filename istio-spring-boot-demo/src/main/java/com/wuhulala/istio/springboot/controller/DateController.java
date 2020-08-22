package com.wuhulala.istio.springboot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class DateController {

    @RequestMapping("/curdate")
    public Date getCurDate() {
        return new Date();
    }

}
