package com.example.Ask.Controllers;

import com.example.Ask.Entities.User;
import com.example.Ask.Entities.Role;
import com.example.Ask.Repositories.RoleRepository;
import com.example.Ask.Repositories.UserRepository;
import com.example.Ask.Service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class UserController {

    private UserService userService;

    private RoleRepository roleRepository;

    public UserController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/register")
    public User register() {
        return new User();
    }

    @PostMapping("/saveUser")
    public Map<String, Object> saveUser(@RequestBody User user){
        Integer id = userService.saveUser(user);
        Map<String, Object> response = new HashMap<>();
        response.put("msg", "User '"+id+"' saved successfully !");
        return response;
    }

    @GetMapping("/users")
    public List<User> showUsers(){
        return userService.getUsers();
    }

    @GetMapping("/user/{user_id}")
    public User showUser(@PathVariable Long user_id){
        return userService.getUser(user_id);
    }

    @PostMapping("/user/{user_id}")
    public User saveStudent(@PathVariable Long user_id, @RequestBody User user) {
        User the_user = (User) userService.getUser(user_id);
        the_user.setEmail(user.getEmail());
        the_user.setUsername(user.getUsername());
        userService.updateUser(the_user);
        return the_user;
    }

    @GetMapping("/user/role/delete/{user_id}/{role_id}")
    public User deleteRolefromUser(@PathVariable Long user_id, @PathVariable Integer role_id){
        User user = (User) userService.getUser(user_id);
        Role role = roleRepository.findById(role_id).get();
        user.getRoles().remove(role);
        userService.updateUser(user);
        return user;
    }

    @GetMapping("/user/role/add/{user_id}/{role_id}")
    public User addRoletoUser(@PathVariable Long user_id, @PathVariable Integer role_id){
        User user = (User) userService.getUser(user_id);
        Role role = roleRepository.findById(role_id).get();
        user.getRoles().add(role);
        userService.updateUser(user);
        return user;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> userData) {
        String username = userData.get("username");
        String email = userData.get("email");
        String password = userData.get("password");
        String roleName = userData.getOrDefault("role", "ROLE_USER");
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
        User user = new User(username, email, password);
        user.setRoles(Set.of(role));
        Integer id = userService.saveUser(user, false); // false: don't override roles
        return ResponseEntity.ok(Map.of("message", "Registration successful!", "userId", id));
    }

}