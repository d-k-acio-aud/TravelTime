package com.example.travel_time.controller;

import com.example.travel_time.service.TripService;
import com.example.travel_time.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final TripService tripService;
    private final UserService userService;

    @GetMapping("/home")
    public String homePage(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        model.addAttribute("username", username);
        model.addAttribute("trips", tripService.getUserTrips(username));
        model.addAttribute("tripCount", tripService.getUserTripCount(username));
        return "home";
    }
}