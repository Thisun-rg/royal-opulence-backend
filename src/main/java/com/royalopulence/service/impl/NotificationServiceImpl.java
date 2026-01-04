package com.royalopulence.service.impl;

import com.royalopulence.dto.NotificationRequest;
import com.royalopulence.dto.NotificationResponse;
import com.royalopulence.model.Notification;
import com.royalopulence.repository.NotificationRepository;
import com.royalopulence.service.base.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public NotificationResponse createNotification(NotificationRequest request) {
        Notification notification = new Notification(
                request.getTitle(),
                request.getMessage(),
                request.getUserId());
        Notification saved = notificationRepository.save(notification);

        NotificationResponse response = new NotificationResponse();
        response.setId(saved.getId());
        response.setTitle(saved.getTitle());
        response.setMessage(saved.getMessage());
        response.setUserId(saved.getUserId());
        response.setCreatedAt(saved.getCreatedAt());
        return response;
    }

    @Override
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll().stream().map(n -> {
            NotificationResponse resp = new NotificationResponse();
            resp.setId(n.getId());
            resp.setTitle(n.getTitle());
            resp.setMessage(n.getMessage());
            resp.setUserId(n.getUserId());
            resp.setCreatedAt(n.getCreatedAt());
            return resp;
        }).collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getUserNotifications(String userId) {
        return notificationRepository.findByUserId(userId).stream().map(n -> {
            NotificationResponse resp = new NotificationResponse();
            resp.setId(n.getId());
            resp.setTitle(n.getTitle());
            resp.setMessage(n.getMessage());
            resp.setUserId(n.getUserId());
            resp.setCreatedAt(n.getCreatedAt());
            return resp;
        }).collect(Collectors.toList());
    }
}
