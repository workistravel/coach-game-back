package pl.dernovyi.coushgameback.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.dernovyi.coushgameback.domain.HttpResponse;
import pl.dernovyi.coushgameback.exception.CardLimitException;
import pl.dernovyi.coushgameback.exception.EmailExistException;
import pl.dernovyi.coushgameback.exception.UserNotFoundException;
import pl.dernovyi.coushgameback.model.Game;
import pl.dernovyi.coushgameback.model.Judgment;
import pl.dernovyi.coushgameback.model.Step;
import pl.dernovyi.coushgameback.model.StepForGame;
import pl.dernovyi.coushgameback.service.GameEditService;
import pl.dernovyi.coushgameback.service.GameService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = {"/game-editor"})
public class GameEditorController {

    public static final String GAME_WAS_DELETED = " игра была удалена";
    public static final String JUDGMENT_WAS_DELETED = " послание было удалено";
    public static final String CARD_USED = " память использованных карт очищена";
    private final GameEditService gameEditService;
    private final GameService gameService;

    public GameEditorController(GameEditService gameEditService, GameService gameService) {
        this.gameEditService = gameEditService;
        this.gameService = gameService;
    }

    @PostMapping("/add")
    public ResponseEntity<Game>  gameAdd(@RequestParam("loggedEmail") String loggedEmail,
                                         @RequestParam("nameGame") String nameGame
    ) throws UserNotFoundException, EmailExistException {

        Game game = gameEditService.saveGame(loggedEmail, nameGame);
        return new ResponseEntity<>(game, OK);
    }

    @PostMapping("/add-judgment")
    public ResponseEntity<Step>  addJudgment(@RequestParam("loggedEmail") String loggedEmail,
                                             @RequestParam("stepId") String stepId,
                                             @RequestParam("text") String text
    ) throws UserNotFoundException, EmailExistException {

        Step step = gameEditService.saveJudgment(loggedEmail, Long.valueOf(stepId) , text);
        return new ResponseEntity<>(step, OK);
    }

    @PostMapping("/edit-judgment")
    public ResponseEntity<Judgment>  editJudgment(@RequestParam("judgmentId") String judgmentId,
                                                  @RequestParam("text") String text) throws UserNotFoundException, EmailExistException {

        Judgment judgment = gameEditService.editJudgment(Long.valueOf(judgmentId) , text);
        return new ResponseEntity<>(judgment, OK);
    }

    @PostMapping("/add-deck-to-step")
    public ResponseEntity<Step> editStep(@RequestParam("email") String email,
                                         @RequestParam("currentStepId") String currentStepId,
                                         @RequestParam("currentDeckId") String currentDeckId,
                                         @RequestParam("titleForStep") String titleForStep) throws UserNotFoundException, EmailExistException {
        Step step = gameEditService.editStep(email, Long.valueOf(currentStepId) , Long.valueOf(currentDeckId) , titleForStep );
        return new ResponseEntity<>(step, OK);
    }
    @DeleteMapping("/delete/{email}/{gameId}")
    public ResponseEntity<HttpResponse> deleteDeck(@PathVariable("email") String email, @PathVariable("gameId") String gameId) throws  UserNotFoundException, EmailExistException {
        String message = gameEditService.deleteGame(email, Long.valueOf(gameId));

        return response(OK, message + GAME_WAS_DELETED);
    }

    @DeleteMapping("/delete-judgment/{judgmentId}")
    public ResponseEntity<HttpResponse> deleteJudgment(@PathVariable("judgmentId") String judgmentId){
        gameEditService.deleteJudgment(Long.valueOf(judgmentId));

        return response(OK,  JUDGMENT_WAS_DELETED);
    }

    @GetMapping("/getGames/{email}")
    public ResponseEntity<List<Game>> getGames(@PathVariable("email") String email) throws UserNotFoundException, EmailExistException {

        List<Game> list =  gameEditService.getGames(email);

        return new ResponseEntity<>(list, OK);
    }

    @GetMapping("/reset-used/{email}")
    public ResponseEntity<HttpResponse> resetUsedCard(@PathVariable("email") String email) throws UserNotFoundException, EmailExistException {

        gameService.resetUsed(email);

        return response(OK, CARD_USED);
    }

    @GetMapping("/get-step/{stepId}")
    public synchronized ResponseEntity<StepForGame> getStep(@PathVariable("stepId") String stepId) throws CardLimitException {
        StepForGame  step =  gameService.getStep(Long.valueOf(stepId));

        return new ResponseEntity<>(step, OK);
    }

    @GetMapping("/get-judgments/{stepId}")
    public ResponseEntity<List<Judgment>> getJudgments(@PathVariable("stepId") String stepId)  throws UserNotFoundException, EmailExistException {

        List<Judgment> list =  gameEditService.getJudgment(Long.valueOf(stepId));

        return new ResponseEntity<>(list, OK);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(
                httpStatus.value(),
                httpStatus,
                httpStatus.getReasonPhrase(),
                message);
        return new ResponseEntity<>(body, httpStatus);
    }

}
