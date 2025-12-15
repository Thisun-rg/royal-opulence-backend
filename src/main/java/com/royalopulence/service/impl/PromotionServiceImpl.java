package com.royalopulence.service.impl;

import com.royalopulence.dto.PromotionRequest;
import com.royalopulence.dto.PromotionResponse;
import com.royalopulence.model.core.Promotion;
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

        // ✅ EXPIRY DATE
        promotion.setExpiryDate(request.getExpiryDate());

        // ✅ CREATED TIME
        promotion.setCreatedAt(LocalDateTime.now());

        Promotion saved = promotionRepository.save(promotion);
        return mapToResponse(saved);
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PromotionResponse getPromotionById(String id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
        return mapToResponse(promotion);
    }

    @Override
    public List<PromotionResponse> getPromotionsForUser(String userId) {
        return promotionRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🔁 ENTITY → DTO MAPPER
    private PromotionResponse mapToResponse(Promotion promotion) {

        PromotionResponse response = new PromotionResponse();
        response.setId(promotion.getId());
        response.setTitle(promotion.getTitle());
        response.setDescription(promotion.getDescription());
        response.setDiscount(promotion.getDiscount());
        response.setUserId(promotion.getUserId());
        response.setExpiryDate(promotion.getExpiryDate());
        response.setCreatedAt(promotion.getCreatedAt());

        return response;
    }
}
