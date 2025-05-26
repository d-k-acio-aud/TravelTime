package com.example.travel_time.controller;

import com.example.travel_time.model.Trip;
import com.example.travel_time.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@RequiredArgsConstructor
@Controller
public class ViewController {


    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Travel Time - Your Journey Begins Here");
        return "index"; // Теперь используем index.html вместо home.html
    }

//    @GetMapping("/home")
//    public String home(Model model) {
//        model.addAttribute("title", "Главная страница");
//        return "home";
//    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Вход");
        return "login"; // Файл: src/main/resources/templates/login.html
    }

    // Страница входа


    // Страница регистрации
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("title", "Регистрация");
        return "register"; // Файл: src/main/resources/templates/register.html
    }

    // Профиль пользователя
    @GetMapping("/profile/{username}")
    public String profile(@PathVariable String username, Model model) {
        model.addAttribute("title", "Профиль: " + username);
        model.addAttribute("username", username);
        return "profile"; // Файл: src/main/resources/templates/profile.html
    }

    // Страница друзей
    @GetMapping("/friends")
    public String friends(Model model) {
        model.addAttribute("title", "Друзья");
        return "friends"; // Файл: src/main/resources/templates/friends.html
    }

    @GetMapping("/trips/new")
    public String showAddTripForm(Model model) {
        model.addAttribute("trip", new Trip());
        return "add-trip";  // имя вашего HTML-шаблона
    }


}