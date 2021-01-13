package pl.dernovyi.coushgameback.service.impl;

import com.microsoft.azure.storage.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.dernovyi.coushgameback.exception.EmailExistException;
import pl.dernovyi.coushgameback.exception.UserNotFoundException;
import pl.dernovyi.coushgameback.model.game_components.Card;
import pl.dernovyi.coushgameback.model.game_components.Deck;
import pl.dernovyi.coushgameback.model.User;
import pl.dernovyi.coushgameback.repository.CardRepository;
import pl.dernovyi.coushgameback.repository.DeckRepository;
import pl.dernovyi.coushgameback.repository.UserRepository;
import pl.dernovyi.coushgameback.service.CardService;
import pl.dernovyi.coushgameback.service.UserService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.Optional;
@Service
public class CardServiceImpl implements CardService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final StorageService storageService;
    @Autowired
    public CardServiceImpl(UserService userService, UserRepository userRepository, DeckRepository deckRepository, CardRepository cardRepository, StorageService storageService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
        this.storageService = storageService;
    }




    @Override
    public Card saveCard(String email, Long idDeck, boolean parseBoolean, String imageCard) throws UserNotFoundException, EmailExistException {
        User user = this.userService.validateNewEmailAndOldEmail(email, null);
        List<Deck> desks = user.getDesks();
        Optional<Deck> first = desks.stream()
                .filter(deck -> deck.getId().equals(idDeck))
                .findFirst();
        Card card = new Card();
        if(first.isPresent()){
            card.setPictureUrl(imageCard);
            card.setHorizon(parseBoolean);
            card.setUsed(false);
            first.get().getCards().add(card);
            this.cardRepository.save(card);
            this.deckRepository.save(first.get());
        }
        this.userRepository.save(user);
        return card;
    }

    @Override
    public void removeCard(String loggedEmail, Long deckId, Long cardId) throws UserNotFoundException, EmailExistException, IOException, StorageException, InvalidKeyException, URISyntaxException {
        this.userService.validateNewEmailAndOldEmail(loggedEmail, null);
        Optional<Deck> deckById = this.deckRepository.findById(Long.valueOf(deckId));
        Optional<Card> cardById = deckById.get().getCards().stream()
                .filter(card -> card.getId().equals(cardId))
                .findFirst();
        this.storageService.removeInStorage(cardById.get().getPictureUrl());
        deckById.get().getCards().remove(cardById.get());
        this.cardRepository.deleteById(cardById.get().getId());
        this.deckRepository.save(deckById.get());

    }

    @Override
    public List<Card> getCardsByIdDeck(String email, Long deckId) throws UserNotFoundException, EmailExistException {
        this.userService.validateNewEmailAndOldEmail(email, null);
        Optional<Deck> deck = this.deckRepository.findById(Long.valueOf(deckId));
        List<Card> list = deck.get().getCards();
        return list;
    }
}
