package com.royalopulence.controller;

import com.royalopulence.dto.NotificationRequest;
import com.royalopulence.dto.NotificationResponse;
import com.royalopulence.service.base.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public NotificationResponse createNotification(@RequestBody NotificationRequest request) {
        return notificationService.createNotification(request);
    }

    @GetMapping
    public List<NotificationResponse> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/{userId}")
    public List<NotificationResponse> getUserNotifications(@PathVariable String userId) {
        return notificationService.getUserNotifications(userId);
    }
}
