package pl.dernovyi.coushgameback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dernovyi.coushgameback.model.game_components.Deck;
import pl.dernovyi.coushgameback.model.game_components.Game;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository <Game, Long>{
    Optional<Game> findById(Long id);
    void deleteById(Long id);
}
