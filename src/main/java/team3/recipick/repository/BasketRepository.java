package team3.recipick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team3.recipick.domain.Basket;
import team3.recipick.domain.Member;

import java.util.Optional;

public interface BasketRepository extends JpaRepository<Basket, Long> {
    Optional<Basket> findByMember(Member member);
}
