package pl.dernovyi.coushgameback.controller;

import com.microsoft.azure.storage.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dernovyi.coushgameback.domain.HttpResponse;
import pl.dernovyi.coushgameback.exception.EmailExistException;
import pl.dernovyi.coushgameback.exception.UserNotFoundException;
import pl.dernovyi.coushgameback.model.game_components.Card;
import pl.dernovyi.coushgameback.service.CardService;
import pl.dernovyi.coushgameback.service.DeckService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = {"/card"})
public class CardController {
    public static final String CARD_WAS_DELETED = " карта была удалена";
    private final CardService cardService;
    private final DeckService deckService;
    @Autowired
    public CardController(CardService cardService, DeckService deckService) {
        this.cardService = cardService;
        this.deckService = deckService;
    }




    @PostMapping("/add")
    public ResponseEntity<Card>  cardAdd(@RequestParam("loggedEmail") String loggedEmail,
                                         @RequestParam("deckId") String deckId,
                                         @RequestParam("imageCard") String imageCard,
                                         @RequestParam("horizon") String horizon) throws UserNotFoundException, EmailExistException {

        Card card = cardService.saveCard(loggedEmail, Long.valueOf(deckId), Boolean.parseBoolean(horizon), imageCard);
        return new ResponseEntity<>(card, OK);
    }
    @PostMapping("/removePictureFromDB")
    public ResponseEntity<HttpResponse> deleteCard(@RequestParam("loggedEmail") String loggedEmail,
                                                   @RequestParam("deckId") String deckId,
                                                   @RequestParam("cardId") String cardId) throws UserNotFoundException, EmailExistException, URISyntaxException, StorageException, InvalidKeyException, IOException {
        this.cardService.removeCard(loggedEmail, Long.valueOf(deckId), Long.valueOf(cardId));
        return response(OK, CARD_WAS_DELETED );
    }

    @GetMapping("/getCards/{email}/{deckId}")
    public ResponseEntity<List<Card>> getCards(@PathVariable("email") String email,
                                               @PathVariable("deckId") String deckId) throws UserNotFoundException, EmailExistException {
        List<Card> list = this.cardService.getCardsByIdDeck(email, Long.valueOf(deckId));
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
