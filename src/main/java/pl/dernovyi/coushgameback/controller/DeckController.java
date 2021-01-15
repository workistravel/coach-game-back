package pl.dernovyi.coushgameback.controller;


import com.microsoft.azure.storage.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dernovyi.coushgameback.domain.HttpResponse;
import pl.dernovyi.coushgameback.exception.EmailExistException;
import pl.dernovyi.coushgameback.exception.UserNotFoundException;
import pl.dernovyi.coushgameback.model.game_components.Deck;
import pl.dernovyi.coushgameback.service.DeckService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = {"/deck"})
public class DeckController {

    public static final String DECK_WAS_DELETED = " колода была удалена";
    public static final String DECK_PICTURE_WAS_CHANGED = " картинка была изменена";
    public static final String DECK_NAME_WAS_CHANGED = " имя было изменено";
    public final DeckService deckService;
    @Autowired
    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    @PostMapping("/add")
    public ResponseEntity<Deck>  deckAdd(@RequestParam("loggedEmail") String loggedEmail,
                                         @RequestParam("nameDeck") String nameDeck,
                                         @RequestParam("imageBackCard") String imageBackCard) throws UserNotFoundException, EmailExistException {

        Deck deck = deckService.saveDeck(loggedEmail, nameDeck, imageBackCard);
        return new ResponseEntity<>(deck, OK);
    }

    @DeleteMapping("/delete/{email}/{deckId}")
    public ResponseEntity<HttpResponse> deleteDeck(@PathVariable("email") String email, @PathVariable("deckId") String deckId) throws URISyntaxException, StorageException, InvalidKeyException, IOException, UserNotFoundException, EmailExistException {
        String message = deckService.deleteDeck(email, deckId);

        return response(OK, message + DECK_WAS_DELETED);
    }

    @PostMapping("/change")
    public ResponseEntity<HttpResponse> changePicture(@RequestParam("deckId") String deckId,
                                                      @RequestParam("newUrl") String newUrl) throws URISyntaxException, StorageException, InvalidKeyException, IOException {
        deckService.changePicture(deckId,newUrl );
        return response(OK,DECK_PICTURE_WAS_CHANGED );
    }

    @PostMapping("/change-name")
    public ResponseEntity<HttpResponse> changeName(@RequestParam("deckId") String deckId,
                                                   @RequestParam("newName") String newName){
        deckService.changeNameDeck(deckId, newName);
        return response(OK, DECK_NAME_WAS_CHANGED );
    }

    @GetMapping("/list/{email}")
    public ResponseEntity<List<Deck>> getDecks(@PathVariable("email") String email) throws UserNotFoundException, EmailExistException {

        List<Deck> list =  deckService.getDecks(email);

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
