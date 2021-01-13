package pl.dernovyi.coushgameback.controller;

import com.microsoft.azure.storage.StorageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dernovyi.coushgameback.domain.HttpResponse;
import pl.dernovyi.coushgameback.exception.EmailExistException;
import pl.dernovyi.coushgameback.exception.UserNotFoundException;
import pl.dernovyi.coushgameback.model.game_components.Card;
import pl.dernovyi.coushgameback.model.game_components.Deck;
import pl.dernovyi.coushgameback.model.game_components.Game;
import pl.dernovyi.coushgameback.model.game_components.Step;
import pl.dernovyi.coushgameback.service.GameService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = {"/game-editor"})
public class GameEditorController {

    public static final String GAME_WAS_DELETED = " игра была удалена";
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

    @PostMapping("/add-deck-to-step")
    public ResponseEntity<Step> editStep(@RequestParam("email") String email,
                                         @RequestParam("currentStepId") String currentStepId,
                                         @RequestParam("currentDeckId") String currentDeckId,
                                         @RequestParam("titleForStep") String titleForStep) throws UserNotFoundException, EmailExistException {
        Step step = gameService.editStep(email, Long.valueOf(currentStepId) , Long.valueOf(currentDeckId) , titleForStep );
        return new ResponseEntity<>(step, OK);
    }
    @DeleteMapping("/delete/{email}/{gameId}")
    public ResponseEntity<HttpResponse> deleteDeck(@PathVariable("email") String email, @PathVariable("gameId") String gameId) throws URISyntaxException, StorageException, InvalidKeyException, IOException, UserNotFoundException, EmailExistException {
        String message = gameService.deleteGame(email, Long.valueOf(gameId));

        return response(OK, message + GAME_WAS_DELETED);
    }

    @GetMapping("/getGames/{email}")
    public ResponseEntity<List<Game>> getGames(@PathVariable("email") String email) throws UserNotFoundException, EmailExistException {

        List<Game> list =  gameService.getGames(email);

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
