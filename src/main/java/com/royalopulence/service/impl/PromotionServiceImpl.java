package com.royalopulence.service.impl;

import com.royalopulence.dto.PromotionRequest;
import com.royalopulence.dto.PromotionResponse;
import com.royalopulence.model.core.Promotion;
import com.royalopulence.repository.PromotionRepository;
import com.royalopulence.service.base.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    public PromotionResponse createPromotion(PromotionRequest request) {

        Promotion promotion = new Promotion(
                request.getTitle(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.isActive(),
                request.getTargetUserId());

        Promotion saved = promotionRepository.save(promotion);
        return mapToResponse(saved);
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll()
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PromotionResponse> getPromotionsForUser(String userId) {
        return promotionRepository
                .findByTargetUserIdOrTargetUserIdIsNull(userId)
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PromotionResponse getPromotionById(String id) {
        Promotion p = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
        return mapToResponse(p);
    }

    private PromotionResponse mapToResponse(Promotion p) {
        PromotionResponse resp = new PromotionResponse();
        resp.setId(p.getId());
        resp.setTitle(p.getTitle());
        resp.setDescription(p.getDescription());
        resp.setStartDate(p.getStartDate());
        resp.setEndDate(p.getEndDate());
        resp.setActive(p.isActive());
        resp.setTargetUserId(p.getTargetUserId());
        return resp;
    }
}
