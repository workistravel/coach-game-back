package pl.dernovyi.coushgameback.service;

import pl.dernovyi.coushgameback.exception.EmailExistException;
import pl.dernovyi.coushgameback.exception.UserNotFoundException;
import pl.dernovyi.coushgameback.model.game_components.Game;
import pl.dernovyi.coushgameback.model.game_components.Judgment;
import pl.dernovyi.coushgameback.model.game_components.Step;

import java.util.List;

public interface GameService {
    Game saveGame(String loggedEmail, String nameGame) throws UserNotFoundException, EmailExistException;

    List<Game> getGames(String email) throws UserNotFoundException, EmailExistException;

    String deleteGame(String email, Long gameId) throws UserNotFoundException, EmailExistException;

    Step editStep(String email, Long currentStepId, Long currentDeckId, String titleForStep) throws UserNotFoundException, EmailExistException;

    Step saveJudgment(String loggedEmail, Long valueOf, String text) throws UserNotFoundException, EmailExistException;

    List<Judgment> getJudgment(Long valueOf);

    void deleteJudgment(Long valueOf);

    Judgment editJudgment(Long judgmentId, String text);
}
