package com.royalopulence.service.base;

import com.royalopulence.dto.NotificationRequest;
import com.royalopulence.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    NotificationResponse createNotification(NotificationRequest request);

    List<NotificationResponse> getAllNotifications();

    List<NotificationResponse> getUserNotifications(String userId);
}
