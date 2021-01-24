package pl.dernovyi.coushgameback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.dernovyi.coushgameback.model.Card;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findById(Long id);
    void deleteById(Long id);
}
