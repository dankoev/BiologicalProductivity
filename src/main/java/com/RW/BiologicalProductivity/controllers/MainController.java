package com.RW.BiologicalProductivity.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class MainController {
    @GetMapping(value="/")
    public String index() {
        return "index";
    }
    

}
