package pl.dernovyi.coushgameback.controller;

import com.microsoft.azure.storage.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.dernovyi.coushgameback.domain.HttpResponse;
import pl.dernovyi.coushgameback.model.User;
import pl.dernovyi.coushgameback.service.impl.StorageService;
import pl.dernovyi.coushgameback.service.impl.UserServiceImpl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import static org.springframework.http.HttpStatus.OK;

@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/drop")
public class AzureBlobController {
    private final StorageService storageService;
    private final UserServiceImpl userService;
    @Autowired
    public AzureBlobController(StorageService storageService, UserServiceImpl userService) {
        this.storageService = storageService;
        this.userService = userService;
    }


    @PostMapping(path = "/addPicture")
    public  ResponseEntity<HttpResponse> get(@RequestParam("file") MultipartFile multipartFile,
                                             @RequestParam("currentEmail") String email) throws IOException, StorageException, InvalidKeyException, URISyntaxException {
        User user = this.userService.findUserByEmail(email);
        URI uri = storageService.saveToStorage(multipartFile, user.getUserId());
        String message = uri.toString();
        return response(OK, message);
    }

    @PostMapping(path = "/removePicture")
    public  ResponseEntity<HttpResponse> removePicture(@RequestParam("urlPicture") String urlPicture,
                                                       @RequestParam("currentEmail") String email) throws IOException, StorageException, InvalidKeyException, URISyntaxException {
        User user = this.userService.findUserByEmail(email);
        storageService.removeInStorage(urlPicture, user.getUserId());
        return response(OK, "Фото удалено");
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
