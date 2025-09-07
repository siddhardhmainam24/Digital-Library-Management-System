package learning.outcome.Controller;

import learning.outcome.DTO.LoginRequest;
import learning.outcome.DTO.LoginResponse;
import learning.outcome.DTO.UserRegistrationRequest;
import learning.outcome.DTO.UserResponse;
import learning.outcome.Service.UserService;
import learning.outcome.utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class authController {
    private final UserService userService;

    public authController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>>register(@RequestBody UserRegistrationRequest userRegistrationRequest) {


      ApiResponse<UserResponse> userResponseApiResponse = userService.register(userRegistrationRequest);
      if(userResponseApiResponse.getSucess()=="true") {
          return ResponseEntity.status(HttpStatus.CREATED).body(userResponseApiResponse);
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userResponseApiResponse);


    }
    @PostMapping("/login")
public ResponseEntity<ApiResponse<LoginResponse>>loginUser(@RequestBody LoginRequest loginRequest) {
        ApiResponse<LoginResponse>loginResponse=userService.loginUser(loginRequest);
        if(loginResponse.getSucess()=="true") {
            return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loginResponse);
    }

}
