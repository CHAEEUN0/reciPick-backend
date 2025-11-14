package team3.recipick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team3.recipick.domain.Fridge;
import team3.recipick.domain.Member;

import java.util.Optional;

public interface FridgeRepository extends JpaRepository<Fridge, Long> {

    Optional<Fridge> findByMember(Member member);


}
