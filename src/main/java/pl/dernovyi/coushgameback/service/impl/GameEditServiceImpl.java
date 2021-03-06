package pl.dernovyi.coushgameback.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.dernovyi.coushgameback.exception.EmailExistException;
import pl.dernovyi.coushgameback.exception.UserNotFoundException;
import pl.dernovyi.coushgameback.model.*;
import pl.dernovyi.coushgameback.repository.*;
import pl.dernovyi.coushgameback.service.GameEditService;
import pl.dernovyi.coushgameback.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GameEditServiceImpl implements GameEditService {

    private final UserService userService;
    private final GameRepository gameRepository;
    private final StepRepository stepRepository;
    private final UserRepository userRepository;
    private final JudgmentRepository judgmentRepository;
    @Autowired
    public GameEditServiceImpl(UserService userService, GameRepository gameRepository, StepRepository stepRepository, UserRepository userRepository, JudgmentRepository judgmentRepository) {
        this.userService = userService;
        this.gameRepository = gameRepository;
        this.stepRepository = stepRepository;
        this.userRepository = userRepository;
        this.judgmentRepository = judgmentRepository;
    }

    @Override
    public Game saveGame(String loggedEmail, String nameGame) throws UserNotFoundException, EmailExistException {
        User user = this.userService.validateNewEmailAndOldEmail(loggedEmail, null);
        Game game = new Game();
        game.setName(nameGame.toUpperCase());
        List<Step> steps = new ArrayList<>();
        for (int i = 0; i < nameGame.length(); i++) {
            Step step = new Step();
            step.setName(String.valueOf(nameGame.charAt(i)));
            steps.add(step);
        }
        Step step = new Step();
        step.setName("Resource");
        steps.add(step);
        game.setSteps(steps);
        this.gameRepository.save(game);
        user.getGames().add(game);
        this.userRepository.save(user);
        return game;
    }

    @Override
    public List<Game> getGames(String email) throws UserNotFoundException, EmailExistException {
        User user = this.userService.validateNewEmailAndOldEmail(email, null);
        List<Game> games =  user.getGames();
        return games;
    }

    @Override
    public String deleteGame(String email, Long gameId) throws UserNotFoundException, EmailExistException {
        User user = this.userService.validateNewEmailAndOldEmail(email, null);
        Optional<Game> gameForDelete = this.gameRepository.findById(gameId);
        List<Step> stepsForDelete = gameForDelete.get().getSteps();

        for (Step step : stepsForDelete) {
            List<Judgment> judgmentsForDelete = step.getJudgments();
            for (Judgment judgment : judgmentsForDelete) {
                this.judgmentRepository.deleteById(judgment.getId());
            }
            this.stepRepository.deleteById(step.getId());
        }
        this.gameRepository.deleteById(gameForDelete.get().getId());
        user.getGames().remove(gameForDelete.get());
        userRepository.save(user);
        return gameForDelete.get().getName();
    }

    @Override
    public Step editStep(String email, Long currentStepId, Long currentDeckId, String titleForStep) throws UserNotFoundException, EmailExistException {
        this.userService.validateNewEmailAndOldEmail(email, null);
        Step stepById = this.stepRepository.getById(currentStepId);
        stepById.setTitle(titleForStep);
        stepById.setDeckId(currentDeckId);
        this.stepRepository.save(stepById);
        return stepById;
    }

    @Override
    public Step saveJudgment(String email, Long stepId, String text) throws UserNotFoundException, EmailExistException {
        this.userService.validateNewEmailAndOldEmail(email, null);
        Step step = this.stepRepository.getById(stepId);
        Judgment judgment = new Judgment();
        judgment.setJudgment(text);
        this.judgmentRepository.save(judgment);
        step.getJudgments().add(judgment);
        this.stepRepository.save(step);
        return step;
    }

    @Override
    public List<Judgment> getJudgment(Long stepId) {
        Step step = this.stepRepository.getById(stepId);
        List<Judgment> judgments = step.getJudgments();
        return judgments;
    }

    @Override
    public void deleteJudgment(Long valueOf) {
        this.judgmentRepository.deleteById(valueOf);
    }

    @Override
    public Judgment editJudgment(Long judgmentId, String text) {
        Optional<Judgment> judgment = this.judgmentRepository.findById(judgmentId);
        judgment.get().setJudgment(text);
        this.judgmentRepository.save(judgment.get());
        return judgment.get();
    }


}
