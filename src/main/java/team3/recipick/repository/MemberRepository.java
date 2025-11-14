package team3.recipick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team3.recipick.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);
}
