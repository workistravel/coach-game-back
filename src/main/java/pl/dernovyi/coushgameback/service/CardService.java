package pl.dernovyi.coushgameback.service;

import com.microsoft.azure.storage.StorageException;
import pl.dernovyi.coushgameback.exception.EmailExistException;
import pl.dernovyi.coushgameback.exception.UserNotFoundException;
import pl.dernovyi.coushgameback.model.Card;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.List;


public interface CardService {
    Card saveCard(String loggedEmail, Long idDeck, boolean parseBoolean, String imageCard) throws UserNotFoundException, EmailExistException;

    void removeCard(String loggedEmail, Long idDeck, Long idCard) throws UserNotFoundException, EmailExistException, IOException, StorageException, InvalidKeyException, URISyntaxException;

    List<Card> getCardsByIdDeck(String email, Long deckId) throws UserNotFoundException, EmailExistException;
}
