package pl.dernovyi.coushgameback.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dernovyi.coushgameback.domain.HttpResponse;
import pl.dernovyi.coushgameback.exception.EmailExistException;
import pl.dernovyi.coushgameback.exception.UserNotFoundException;
import pl.dernovyi.coushgameback.model.game_components.*;
import pl.dernovyi.coushgameback.service.GameService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = {"/game-editor"})
public class GameEditorController {

    public static final String GAME_WAS_DELETED = " игра была удалена";
    public static final String JUDGMENT_WAS_DELETED = " послание было удалено";
    private final GameService gameService;

    public GameEditorController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/add")
    public ResponseEntity<Game>  gameAdd(@RequestParam("loggedEmail") String loggedEmail,
                                         @RequestParam("nameGame") String nameGame
//                                         ,@RequestParam(value = "lengthGame", required = false) String lengthGame
    ) throws UserNotFoundException, EmailExistException {

        Game game = gameService.saveGame(loggedEmail, nameGame);
        return new ResponseEntity<>(game, OK);
    }

    @PostMapping("/add-judgment")
    public ResponseEntity<Step>  addJudgment(@RequestParam("loggedEmail") String loggedEmail,
                                             @RequestParam("stepId") String stepId,
                                             @RequestParam("text") String text
    ) throws UserNotFoundException, EmailExistException {

        Step step = gameService.saveJudgment(loggedEmail, Long.valueOf(stepId) , text);
        return new ResponseEntity<>(step, OK);
    }

    @PostMapping("/edit-judgment")
    public ResponseEntity<Judgment>  editJudgment(@RequestParam("judgmentId") String judgmentId,
                                             @RequestParam("text") String text) throws UserNotFoundException, EmailExistException {

        Judgment judgment = gameService.editJudgment(Long.valueOf(judgmentId) , text);
        return new ResponseEntity<>(judgment, OK);
    }

    @PostMapping("/add-deck-to-step")
    public ResponseEntity<Step> editStep(@RequestParam("email") String email,
                                         @RequestParam("currentStepId") String currentStepId,
                                         @RequestParam("currentDeckId") String currentDeckId,
                                         @RequestParam("titleForStep") String titleForStep) throws UserNotFoundException, EmailExistException {
        Step step = gameService.editStep(email, Long.valueOf(currentStepId) , Long.valueOf(currentDeckId) , titleForStep );
        return new ResponseEntity<>(step, OK);
    }
    @DeleteMapping("/delete/{email}/{gameId}")
    public ResponseEntity<HttpResponse> deleteDeck(@PathVariable("email") String email, @PathVariable("gameId") String gameId) throws  UserNotFoundException, EmailExistException {
        String message = gameService.deleteGame(email, Long.valueOf(gameId));

        return response(OK, message + GAME_WAS_DELETED);
    }

    @DeleteMapping("/delete-judgment/{judgmentId}")
    public ResponseEntity<HttpResponse> deleteJudgment(@PathVariable("judgmentId") String judgmentId){
        gameService.deleteJudgment(Long.valueOf(judgmentId));

        return response(OK,  JUDGMENT_WAS_DELETED);
    }

    @GetMapping("/getGames/{email}")
    public ResponseEntity<List<Game>> getGames(@PathVariable("email") String email) throws UserNotFoundException, EmailExistException {

        List<Game> list =  gameService.getGames(email);

        return new ResponseEntity<>(list, OK);
    }

    @GetMapping("/get-judgments/{stepId}")
    public ResponseEntity<List<Judgment>> getJudgments(@PathVariable("stepId") String stepId) throws UserNotFoundException, EmailExistException {

        List<Judgment> list =  gameService.getJudgment(Long.valueOf(stepId));

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
