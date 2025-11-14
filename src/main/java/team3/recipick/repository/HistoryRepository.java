package team3.recipick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team3.recipick.domain.History;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
