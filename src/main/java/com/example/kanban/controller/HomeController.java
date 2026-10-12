package com.example.kanban.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/boards";
        }
        return "index";
    }

    @GetMapping("favicon.ico")
    public org.springframework.http.ResponseEntity<Void> favicon() {
        return org.springframework.http.ResponseEntity.noContent().build();
    }
}
