package com.royalopulence.service.impl;

import com.royalopulence.dto.PromotionRequest;
import com.royalopulence.dto.PromotionResponse;
import com.royalopulence.model.core.Promotion;
import com.royalopulence.model.core.PromotionStatus;
import com.royalopulence.repository.PromotionRepository;
import com.royalopulence.service.base.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    public PromotionResponse createPromotion(PromotionRequest request) {

        Promotion promotion = new Promotion();
        promotion.setTitle(request.getTitle());
        promotion.setDescription(request.getDescription());
        promotion.setDiscount(request.getDiscount());
        promotion.setUserId(request.getUserId());
        promotion.setExpiryDate(request.getExpiryDate());
        promotion.setCreatedAt(LocalDateTime.now());

        // ✅ AUTO STATUS
        promotion.setStatus(calculateStatus(request.getExpiryDate()));

        Promotion saved = promotionRepository.save(promotion);
        return mapToResponse(saved);
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll()
                .stream()
                .map(promotion -> {
                    promotion.setStatus(calculateStatus(promotion.getExpiryDate()));
                    return mapToResponse(promotion);
                })
                .collect(Collectors.toList());
    }

    @Override
    public PromotionResponse getPromotionById(String id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));

        promotion.setStatus(calculateStatus(promotion.getExpiryDate()));
        return mapToResponse(promotion);
    }

    @Override
    public List<PromotionResponse> getPromotionsForUser(String userId) {
        return promotionRepository.findByUserId(userId)
                .stream()
                .map(promotion -> {
                    promotion.setStatus(calculateStatus(promotion.getExpiryDate()));
                    return mapToResponse(promotion);
                })
                .collect(Collectors.toList());
    }

    // 🔁 ENTITY → DTO
    private PromotionResponse mapToResponse(Promotion promotion) {

        PromotionResponse response = new PromotionResponse();
        response.setId(promotion.getId());
        response.setTitle(promotion.getTitle());
        response.setDescription(promotion.getDescription());
        response.setDiscount(promotion.getDiscount());
        response.setUserId(promotion.getUserId());
        response.setExpiryDate(promotion.getExpiryDate());
        response.setCreatedAt(promotion.getCreatedAt());
        response.setStatus(promotion.getStatus());

        return response;
    }

    // 🧠 STATUS CALCULATION
    private PromotionStatus calculateStatus(LocalDateTime expiryDate) {
        if (expiryDate == null) {
            return PromotionStatus.ACTIVE;
        }
        return expiryDate.isBefore(LocalDateTime.now())
                ? PromotionStatus.EXPIRED
                : PromotionStatus.ACTIVE;
    }
}
