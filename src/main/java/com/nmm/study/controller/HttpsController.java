package com.nmm.study.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author nmm 2018/6/22
 * @description
 */
@Controller
public class HttpsController {

    @ResponseBody
    @RequestMapping("/login")
    public String login(){
        return "login";
    }
    @ResponseBody
    @RequestMapping("/conn")
    public String conn(){
        return "conn";
    }
}
