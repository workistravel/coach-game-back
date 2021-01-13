package pl.dernovyi.coushgameback.service;

import com.microsoft.azure.storage.StorageException;
import pl.dernovyi.coushgameback.exception.EmailExistException;
import pl.dernovyi.coushgameback.exception.UserNotFoundException;
import pl.dernovyi.coushgameback.model.game_components.Deck;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.List;

public interface DeckService {
    Deck saveDeck(String email, String name, String imageBackCard) throws UserNotFoundException, EmailExistException;

    List<Deck> getDecks(String email) throws UserNotFoundException, EmailExistException;

    String deleteDeck(String email, String deckId) throws IOException, StorageException, InvalidKeyException, URISyntaxException, UserNotFoundException, EmailExistException;

    void changePicture(String deckId, String newUrl) throws IOException, StorageException, InvalidKeyException, URISyntaxException;

    void changeNameDeck(String deckId,  String newName);


}
