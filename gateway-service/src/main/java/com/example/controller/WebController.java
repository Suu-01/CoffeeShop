package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "MSA 프로젝트에 오신 것을 환영합니다");
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("message", "로그인");
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("message", "회원 가입");
        return "register";
    }

    @GetMapping("/posts")
    public String posts(Model model) {
        model.addAttribute("message", "게시글 목록");
        return "post";
    }

    @GetMapping("/posts/{id}")
    public String postDetail(@PathVariable String id, Model model) {
        model.addAttribute("message", "게시글 상세");
        return "detail";
    }

    @GetMapping("/orders")
    public String products(Model model) {
        model.addAttribute("message", "주문 목록");
        return "orders";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("message", "관리자 페이지");
        return "admin";
    }

    @GetMapping("/customers")
    public String customer(Model model) {
        model.addAttribute("message", "고객 목록");
        return "customers";
    }
}
