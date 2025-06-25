package com.example.sortitems.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/scanner")
    public String scannerPage() {
        return "scanner"; // ini akan cari scanner.html di folder /templates
    }
}
