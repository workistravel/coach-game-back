package pl.dernovyi.coushgameback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dernovyi.coushgameback.model.game_components.Step;

@Repository
public interface StepRepository extends JpaRepository<Step, Long> {
    void deleteById(Long id);
    Step getById(Long id);
}
