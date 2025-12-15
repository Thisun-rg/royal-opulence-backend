package com.royalopulence.controller;

import com.royalopulence.dto.PromotionRequest;
import com.royalopulence.dto.PromotionResponse;
import com.royalopulence.service.base.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotion")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    public PromotionResponse createPromotion(@RequestBody PromotionRequest request) {
        return promotionService.createPromotion(request);
    }

    @GetMapping
    public List<PromotionResponse> getAllPromotions() {
        return promotionService.getAllPromotions();
    }

    @GetMapping("/{id}")
    public PromotionResponse getPromotionById(@PathVariable String id) {
        return promotionService.getPromotionById(id);
    }
    @GetMapping("/user/{userId}")
public List<PromotionResponse> getPromotionsForUser(@PathVariable String userId) {
    return promotionService.getPromotionsForUser(userId);
}

}
