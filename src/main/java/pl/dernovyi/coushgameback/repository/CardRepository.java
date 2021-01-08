package pl.dernovyi.coushgameback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.dernovyi.coushgameback.model.Card;
import pl.dernovyi.coushgameback.model.Deck;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findById(Long id);
    void deleteById(Long id);
}
