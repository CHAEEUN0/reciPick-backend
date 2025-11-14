package team3.recipick.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team3.recipick.domain.Fridge;
import team3.recipick.domain.FridgeIngredient;
import team3.recipick.domain.Ingredient;
import team3.recipick.domain.Member;
import team3.recipick.dto.FridgeIngredientRequest;
import team3.recipick.dto.FridgeIngredientResponse;
import team3.recipick.dto.FridgeIngredientUpdateRequest;
import team3.recipick.repository.FridgeIngredientRepository;
import team3.recipick.repository.FridgeRepository;
import team3.recipick.repository.IngredientRepository;
import team3.recipick.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FridgeService {

    private final FridgeRepository fridgeRepository;
    private final MemberRepository memberRepository;
    private final IngredientRepository ingredientRepository;
    private final FridgeIngredientRepository fridgeIngredientRepository;

    @Transactional(readOnly = true)
    public Page<FridgeIngredientResponse> getIngredients(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        Fridge fridge = fridgeRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("냉장고가 존재하지 않습니다."));

        return fridgeIngredientRepository.findAllByFridgeOrderByCreatedAt(fridge, pageable)
                .map(FridgeIngredientResponse::from);
    }

    @Transactional
    public Page<FridgeIngredientResponse> addIngredient(FridgeIngredientRequest request, Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        Fridge fridge = fridgeRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("냉장고가 존재하지 않습니다."));

        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 재료입니다."));

        FridgeIngredient fridgeIngredient = new FridgeIngredient(fridge, ingredient, request.getCount(), request.getMemo());
        fridge.addFridgeIngredient(fridgeIngredient);

        return fridgeIngredientRepository.findAllByFridgeOrderByCreatedAt(fridge, pageable)
                .map(FridgeIngredientResponse::from);
    }

    @Transactional
    public Page<FridgeIngredientResponse> updateIngredients(List<FridgeIngredientUpdateRequest> requests, Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        Fridge fridge = fridgeRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("냉장고가 존재하지 않습니다."));

        List<Long> ids = requests.stream().map(FridgeIngredientUpdateRequest::getFridgeIngredientId).toList();
        List<FridgeIngredient> ingredients = fridgeIngredientRepository.findAllById(ids);

        for (FridgeIngredient fi : ingredients) {
            if (!fi.getFridge().getId().equals(fridge.getId())) {
                throw new RuntimeException("해당 재료는 회원의 냉장고에 속하지 않습니다.");
            }

            requests.stream()
                    .filter(req -> req.getFridgeIngredientId().equals(fi.getId()))
                    .findFirst()
                    .ifPresent(req -> fi.update(req.getCount(), req.getMemo()));
        }

        return fridgeIngredientRepository.findAllByFridgeOrderByCreatedAt(fridge, pageable)
                .map(FridgeIngredientResponse::from);
    }

    @Transactional
    public Page<FridgeIngredientResponse> deleteIngredient(Long id, Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        Fridge fridge = fridgeRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("냉장고가 존재하지 않습니다."));

        FridgeIngredient fridgeIngredient = fridgeIngredientRepository.findByIdAndFridge(id, fridge)
                .orElseThrow(() -> new RuntimeException("해당 재료가 냉장고에 존재하지 않습니다."));

        fridge.deleteFridgeIngredient(fridgeIngredient);

        return fridgeIngredientRepository.findAllByFridgeOrderByCreatedAt(fridge, pageable)
                .map(FridgeIngredientResponse::from);
    }
}
