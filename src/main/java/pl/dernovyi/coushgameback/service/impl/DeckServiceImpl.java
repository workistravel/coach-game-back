package pl.dernovyi.coushgameback.service.impl;

import com.microsoft.azure.storage.StorageException;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.dernovyi.coushgameback.exception.EmailExistException;
import pl.dernovyi.coushgameback.exception.UserNotFoundException;
import pl.dernovyi.coushgameback.model.Card;
import pl.dernovyi.coushgameback.model.Deck;
import pl.dernovyi.coushgameback.model.User;
import pl.dernovyi.coushgameback.repository.CardRepository;
import pl.dernovyi.coushgameback.repository.DeckRepository;
import pl.dernovyi.coushgameback.repository.UserRepository;
import pl.dernovyi.coushgameback.service.DeckService;
import pl.dernovyi.coushgameback.service.StorageService;
import pl.dernovyi.coushgameback.service.UserService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.Optional;

@Service
public class DeckServiceImpl implements DeckService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final StorageService storageService;
    @Autowired
    public DeckServiceImpl(UserRepository userRepository, UserService userService, DeckRepository deckRepository, StorageService storageService, CardRepository cardRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
        this.storageService = storageService;
    }

    @Override
    public Deck saveDeck(String email, String name, String imageBackCard) {
        User user = this.userService.findUserByEmail(email);
        Deck deck = new Deck();
        deck.setBackOfCardUrl(imageBackCard);
        deck.setName(name);
        this.deckRepository.save(deck);
        user.getDesks().add(deck);
        userRepository.save(user);
        return deck;
    }
    @Override
    public List<Deck> getDecks(String email) throws UserNotFoundException, EmailExistException {
        User user = this.userService.validateNewEmailAndOldEmail(email, null);
        List<Deck> list =  user.getDesks();
        return list;
    }

    @Override
    public String deleteDeck(String email, String deckId) throws IOException, StorageException, InvalidKeyException, URISyntaxException, UserNotFoundException, EmailExistException {
        User user = this.userService.validateNewEmailAndOldEmail(email, null);
        Optional<Deck> deckForDelete = this.deckRepository.findById(Long.valueOf(deckId));
        List<Card> list = deckForDelete.get().getCards();

        for (Card card : list) {
            this.storageService.removeInStorage(card.getPictureUrl());
            this.cardRepository.deleteById(card.getId());
        }

        user.getDesks().remove(deckForDelete.get());
        this.userRepository.save(user);
        this.deckRepository.deleteById(Long.valueOf(deckId));
        this.storageService.removeInStorage(deckForDelete.get().getBackOfCardUrl());
        return deckForDelete.get().getName();
    }

    @Override
    public void changePicture(String deckId,  String newUrl){
        Optional<Deck> deck = this.deckRepository.findById(Long.valueOf(deckId));
        deck.get().setBackOfCardUrl(newUrl);
        this.deckRepository.save(deck.get());
    }

    @Override
    public void changeNameDeck(String deckId,  String newName) {
        Optional<Deck> deck = this.deckRepository.findById(Long.valueOf(deckId));
        deck.get().setName(newName);
        this.deckRepository.save(deck.get());
    }




}
