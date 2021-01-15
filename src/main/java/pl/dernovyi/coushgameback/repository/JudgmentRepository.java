package pl.dernovyi.coushgameback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dernovyi.coushgameback.model.Judgment;

import java.util.Optional;
@Repository
public interface JudgmentRepository extends JpaRepository<Judgment, Long> {
    Optional<Judgment> findById(Long id);
    void deleteById(Long id);
}
