package team3.recipick.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team3.recipick.domain.FridgeIngredient;
import team3.recipick.dto.FridgeIngredientRequest;
import team3.recipick.dto.FridgeIngredientResponse;
import team3.recipick.dto.FridgeIngredientUpdateRequest;
import team3.recipick.jwt.MemberDetails;
import team3.recipick.service.FridgeService;

import java.util.List;

@RestController
@RequestMapping("/api/fridge/ingredients")
@RequiredArgsConstructor
public class FridgeController {

    private final FridgeService fridgeService;

    @GetMapping
    public Page<FridgeIngredientResponse> getIngredients(
            @AuthenticationPrincipal MemberDetails member,
            Pageable pageable) {
        return fridgeService.getIngredients(member.getId(), pageable);
    }

    @PostMapping
    public Page<FridgeIngredientResponse> addIngredient(
            @RequestBody FridgeIngredientRequest request,
            @AuthenticationPrincipal MemberDetails member,
            Pageable pageable) {
        return fridgeService.addIngredient(request, member.getId(), pageable);
    }

    @PatchMapping
    public Page<FridgeIngredientResponse> updateIngredients(
            @RequestBody List<FridgeIngredientUpdateRequest> requests,
            @AuthenticationPrincipal MemberDetails member,
            Pageable pageable) {
        return fridgeService.updateIngredients(requests, member.getId(), pageable);
    }

    @DeleteMapping("/{fridgeIngredientId}")
    public Page<FridgeIngredientResponse> deleteIngredient(
            @PathVariable Long fridgeIngredientId,
            @AuthenticationPrincipal MemberDetails member,
            Pageable pageable) {
        return fridgeService.deleteIngredient(fridgeIngredientId, member.getId(), pageable);
    }
}
