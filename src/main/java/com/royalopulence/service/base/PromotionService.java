
package com.royalopulence.service.base;

import com.royalopulence.dto.PromotionRequest;
import com.royalopulence.dto.PromotionResponse;

import java.util.List;

public interface PromotionService {
    PromotionResponse createPromotion(PromotionRequest request);

    List<PromotionResponse> getAllPromotions();

    PromotionResponse getPromotionById(String id);

    List<PromotionResponse> getPromotionsForUser(String userId);
}
