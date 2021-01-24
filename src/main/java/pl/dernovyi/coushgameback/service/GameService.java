package pl.dernovyi.coushgameback.service;

import pl.dernovyi.coushgameback.exception.CardLimitException;
import pl.dernovyi.coushgameback.exception.EmailExistException;
import pl.dernovyi.coushgameback.exception.UserNotFoundException;
import pl.dernovyi.coushgameback.model.StepForGame;

public interface GameService {
    StepForGame getStep(Long stepId) throws CardLimitException;

    void resetUsed(String email) throws UserNotFoundException, EmailExistException;
}
