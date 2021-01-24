package pl.dernovyi.coushgameback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.dernovyi.coushgameback.domain.HttpResponse;
import pl.dernovyi.coushgameback.exception.*;
import pl.dernovyi.coushgameback.model.User;
import pl.dernovyi.coushgameback.model.UserPrincipal;
import pl.dernovyi.coushgameback.security.JwtTokenProvider;
import pl.dernovyi.coushgameback.service.impl.StorageService;
import pl.dernovyi.coushgameback.service.UserService;

import javax.mail.MessagingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static pl.dernovyi.coushgameback.constant.FileConstant.*;
import static pl.dernovyi.coushgameback.constant.SecurityConstant.JWT_TOKEN_HEADER;


@RestController
@RequestMapping(path = {"/","/user"})
public class UserController extends ExceptionHandling {

    public static final String EMAIL_SENT = "An email with new password was sent to:  ";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";
    public static final String PASSWORD_WAS_CHANGED = "Your password was changed successfully";
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final StorageService storageService;
    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, StorageService storageService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.storageService = storageService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws EmailExistException, UsernameExistException, UserNotFoundException, MessagingException, IOException {
        User newUser =  userService.register(user.getFirstName(), user.getLastName(), user.getEmail());
        return new ResponseEntity<>(newUser, OK);
    }
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
      authenticate(user.getEmail(), user.getPassword());
      User loginUser = userService.findUserByEmail(user.getEmail());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
       return new ResponseEntity<>(loginUser,jwtHeader, OK);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("role") String role,
                                           @RequestParam("email") String email,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNonLocked") String isNonLocked,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile multipartFile) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, MessagingException, NotAnImageFileException {
        User newUser = userService.addNewUser(firstName,lastName,email,role,
                Boolean.parseBoolean(isNonLocked),Boolean.parseBoolean(isActive),multipartFile);
        return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/update")
    public ResponseEntity<User> updateUser(@RequestParam("currentEmail") String currentEmail,
                                           @RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("role") String role,
                                           @RequestParam("email") String email,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNonLocked") String isNonLocked,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        User updatedUser = userService.updateUser(currentEmail, firstName,lastName,email,role,
                Boolean.parseBoolean(isNonLocked),Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(updatedUser, OK);
    }
    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("email") String email){
        User user = userService.findUserByEmail(email);
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getUsers(){
        List<User> list = userService.getUsers();
        return new ResponseEntity<>(list, OK);
    }
    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException, MessagingException, IOException {
       userService.resetPassword(email);

        return response(OK, EMAIL_SENT + email);
    }

    @PostMapping("/updatepassword")
    public ResponseEntity<HttpResponse> updatePassword(@RequestParam("loggedEmail") String loggedEmail,
                                                       @RequestParam("oldPassword") String oldPassword,
                                                       @RequestParam("newPassword") String newPassword) throws EmailNotFoundException, MessagingException, PasswordNotCorrectException, IOException {
        userService.updatePassword(loggedEmail, oldPassword, newPassword);
        return response(OK, PASSWORD_WAS_CHANGED);
    }

    @DeleteMapping("/delete/{email}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("email") String email) throws IOException {
        userService.deleteUser(email);

        return response(OK, USER_DELETED_SUCCESSFULLY);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(@RequestParam("email") String email, @RequestParam(value = "profileImage") MultipartFile multipartFile) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        User user = userService.updateProfileImage(email, multipartFile);
        return new ResponseEntity<>(user, OK);
    }
//для установленного фото
    @GetMapping(path = "/image/{email}/{profileImage}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("email") String email, @PathVariable("profileImage") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + email + FORWARD_SLASH + fileName));
    }

//   для дефолтного фото
    @GetMapping(path = "/image/profile/{email}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTemporaryProfileImage(@PathVariable("email") String email) throws IOException {

        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + email);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(InputStream inputStream = url.openStream()){
            int bytesRead;
            byte[] chunk = new byte[1024];
            while ((bytesRead = inputStream.read(chunk)) > 0){
                byteArrayOutputStream.write(chunk, 0, bytesRead);

            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(
                httpStatus.value(),
                httpStatus,
                httpStatus.getReasonPhrase(),
                message);
        return new ResponseEntity<>(body, httpStatus);
    }


    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return httpHeaders;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }




}
