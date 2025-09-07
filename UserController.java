package learning.outcome.Controller;

import learning.outcome.Entity.appUser;
import learning.outcome.Service.UserService;
import learning.outcome.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @PostMapping("/createuser")
    public ResponseEntity<appUser>createUser(@RequestBody appUser user) {
       try{
           if(userService.emailExists(user.getEmail()))
           {
               return ResponseEntity.status(HttpStatus.CONFLICT).build();

           }
           appUser users=userService.createuser(user);
           return ResponseEntity.status(HttpStatus.CREATED).body(users);

       }
       catch(Exception e)
       {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
       }

    }

    @GetMapping("/getallusers")
    public ResponseEntity<List<appUser>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());

    }
    @GetMapping("/getuserbyid/{userid}")
    public ResponseEntity<appUser> getUserById(@PathVariable("userid") long userid) {
      return ResponseEntity.ok(userService.getuserbyid(userid));
    }
    @GetMapping("/{email}")
    public ResponseEntity<appUser> getUserByEmail(@PathVariable("email") String email) {
        if(userService.emailExists(email))
        {
            return ResponseEntity.ok(userService.getbyemail(email));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @PutMapping("{id}")
    public ResponseEntity<?>update(@PathVariable long id,@RequestBody appUser user)
    {
      appUser users=  userService.getuserbyid(id);
      if(users!=null)
      {
          appUser user1=users;
          user1.setFirstName(user.getFirstName());
          user1.setLastName(user.getLastName());
          user1.setEmail(user.getEmail());
        appUser updateuser = userService.updateuser(user1);
        return ResponseEntity.ok(updateuser);

      }

      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();


    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.getuserbyid(id)!=null) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Search users by name
    @GetMapping("/search")
    public ResponseEntity<List<appUser>> searchUsers(@RequestParam String name) {
        List<appUser> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }






}
