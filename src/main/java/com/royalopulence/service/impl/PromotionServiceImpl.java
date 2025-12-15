package com.royalopulence.service.impl;

import com.royalopulence.dto.marketing.PromotionRequest;
import com.royalopulence.dto.marketing.PromotionResponse;
import com.royalopulence.model.marketing.Promotion;
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
                request.isActive()
        );
        Promotion saved = promotionRepository.save(promotion);

        PromotionResponse response = new PromotionResponse();
        response.setId(saved.getId());
        response.setTitle(saved.getTitle());
        response.setDescription(saved.getDescription());
        response.setStartDate(saved.getStartDate());
        response.setEndDate(saved.getEndDate());
        response.setActive(saved.isActive());
        return response;
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll().stream().map(p -> {
            PromotionResponse resp = new PromotionResponse();
            resp.setId(p.getId());
            resp.setTitle(p.getTitle());
            resp.setDescription(p.getDescription());
            resp.setStartDate(p.getStartDate());
            resp.setEndDate(p.getEndDate());
            resp.setActive(p.isActive());
            return resp;
        }).collect(Collectors.toList());
    }

    @Override
    public PromotionResponse getPromotionById(String id) {
        Promotion p = promotionRepository.findById(id).orElseThrow(() -> new RuntimeException("Promotion not found"));
        PromotionResponse resp = new PromotionResponse();
        resp.setId(p.getId());
        resp.setTitle(p.getTitle());
        resp.setDescription(p.getDescription());
        resp.setStartDate(p.getStartDate());
        resp.setEndDate(p.getEndDate());
        resp.setActive(p.isActive());
        return resp;
    }
}
