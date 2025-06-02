package com.flamabrava.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("http://localhost:3000/menu")
    public String home() {
        return "API funcionando"; // o un JSON simple
    }
}
