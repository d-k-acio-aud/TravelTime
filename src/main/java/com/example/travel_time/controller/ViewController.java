package com.example.travel_time.controller;

import com.example.travel_time.model.Trip;
import com.example.travel_time.model.User;
import com.example.travel_time.service.FriendshipService;
import com.example.travel_time.service.PhotoService;
import com.example.travel_time.service.TripService;
import com.example.travel_time.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@RequiredArgsConstructor
@Controller
public class ViewController {

    private final UserService userService;
    private final TripService tripService;
    private final PhotoService photoService;
    private final FriendshipService friendshipService;
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Travel Time - Your Journey Begins Here");
        return "index"; // Теперь используем index.html вместо home.html
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Вход");
        return "login"; // Файл: src/main/resources/templates/login.html
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("title", "Регистрация");
        return "register"; // Файл: src/main/resources/templates/register.html
    }

    @GetMapping("/profile")
    public String profilePage(Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();

        int tripCount = tripService.getUserTripsCount(user.getId());
        long photoCount = photoService.getUserPhotosCount(user.getId());
        int friendsCount = friendshipService.getFriendsCount(user.getId());

        model.addAttribute("title", "Profile: " + username);
        model.addAttribute("username", username);
        model.addAttribute("user", user);
        model.addAttribute("tripCount", tripCount);
        model.addAttribute("photoCount", photoCount);
        model.addAttribute("friendsCount", friendsCount);

        return "profile";
    }



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
    @GetMapping("/photos")
    public String photos(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        model.addAttribute("title", "Галерея всех фото");
        return "photos";
    }
    @GetMapping("/friend-profile/{username}")
    public String friendProfile(@PathVariable String username, Model model) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("username", user.getUsername());
        model.addAttribute("title", "Профиль друга: " + user.getUsername());
        model.addAttribute("trips", user.getTrips());
        model.addAttribute("tripCount", user.getTrips().size());

        return "friend-profile";
    }

    @GetMapping("/trips/edit/{id}")
    public String showEditTripForm(@PathVariable Long id, Model model, Authentication authentication) {
        Trip trip = tripService.getTripById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        User currentUser = (User) authentication.getPrincipal();
        if (!trip.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only edit your own trips");
        }

        model.addAttribute("trip", trip);
        model.addAttribute("editMode", true); // Флаг для режима редактирования
        return "add-trip";
    }


}