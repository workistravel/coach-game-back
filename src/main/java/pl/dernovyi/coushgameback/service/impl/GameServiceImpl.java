package pl.dernovyi.coushgameback.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import pl.dernovyi.coushgameback.exception.CardLimitException;
import pl.dernovyi.coushgameback.exception.EmailExistException;
import pl.dernovyi.coushgameback.exception.UserNotFoundException;
import pl.dernovyi.coushgameback.model.*;
import pl.dernovyi.coushgameback.repository.CardRepository;
import pl.dernovyi.coushgameback.repository.DeckRepository;
import pl.dernovyi.coushgameback.repository.StepRepository;
import pl.dernovyi.coushgameback.service.GameService;
import pl.dernovyi.coushgameback.service.UserService;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static pl.dernovyi.coushgameback.constant.GameConstant.CARD_LIMIT;

@Service
public class GameServiceImpl implements GameService {
    private final StepRepository stepRepository;
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final UserService userService;
    @Autowired
    public GameServiceImpl(StepRepository stepRepository, DeckRepository deckRepository, CardRepository cardRepository, UserService userService) {
        this.stepRepository = stepRepository;
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
        this.userService = userService;
    }






    @Override
    public StepForGame getStep(Long stepId) throws CardLimitException {
        StepForGame stepForGame = new StepForGame();
        Step step = this.stepRepository.getById(stepId);
        Judgment j = getRandomJudgment(step);
        Card card = getRandomCard(step);
        stepForGame.setUrlPicture(card.getPictureUrl());
        stepForGame.setJudgment(j.getJudgment());
        return stepForGame;
    }

    @Override
    public void resetUsed(String email) throws UserNotFoundException, EmailExistException {
        User user = this.userService.validateNewEmailAndOldEmail(email, null);
        List<Deck> desks = user.getDesks();
        for (Deck desk : desks) {
            desk.getCards().forEach(card -> card.setUsed(false));
            deckRepository.save(desk);
        }
    }

    private Judgment getRandomJudgment(Step step) {
        Random random = new Random();
        List<Judgment> judgments = step.getJudgments();
        if(judgments.size() < 1){
            return new Judgment();
        }
        int i = random.nextInt(judgments.size());
        return judgments.get(i);
    }

    public Card getRandomCard(Step step) throws CardLimitException {
        Optional<Deck> deck = this.deckRepository.findById(step.getDeckId());
        List<Card> cards = deck.get().getCards();
        Random random = new Random();

            while (true) {
                int i = random.nextInt(cards.size());
                Card temp = cards.get(i);
                if(!temp.isUsed()){
                    temp.setUsed(true);
                    this.deckRepository.save(deck.get());
                    this.cardRepository.save(temp);
                    return temp;
                }
                if (cards.stream().allMatch(Card::isUsed)){
                    throw new CardLimitException(CARD_LIMIT);
                }

            }


    }
}
