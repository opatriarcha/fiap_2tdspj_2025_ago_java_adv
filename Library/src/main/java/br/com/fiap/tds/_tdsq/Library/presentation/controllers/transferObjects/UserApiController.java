package br.com.fiap.tds._tdsq.Library.presentation.controllers.transferObjects;

import br.com.fiap.tds._tdsq.Library.domainmodel.User;
import br.com.fiap.tds._tdsq.Library.service.UserService;
import br.com.fiap.tds._tdsq.Library.service.UserServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")//http://localhost:8080/api/users
public class UserApiController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> findAll(){
        return ResponseEntity.ok(this.userService.findAll());
    }

    @GetMapping("/{id}") //http://localhost:8080/api/users/1
    public ResponseEntity<User> findById(@PathVariable("id") UUID id){
        User user = this.userService.findById(id);
        if(user == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> save(@RequestBody User user){
//        if( this.userService.exists(user) )
        User newUser = this.userService.create(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteById(@RequestBody UUID id){
        if( !this.userService.existsById(id ))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        this.userService.removeById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/removeObject")
    public ResponseEntity<Void> delete(@RequestBody User user){
        if(!this.userService.existsById(user.getId()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        this.userService.remove(user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable("id") UUID id, @RequestBody User user){
        if( !this.userService.existsById(id) )
            return ResponseEntity.notFound().build();
//        User userFromDatabase = this.userService.findById(id);
        user.setId(id);
        return new ResponseEntity<>(
                this.userService.create(user),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> partialUpdate(@PathVariable("id") UUID id, @RequestBody User user) {
        User updatedUser = null;
        try {
            updatedUser = this.userService.findById(id);
            return new ResponseEntity<>(
                    updatedUser,
                    HttpStatus.CREATED
            );
        } catch (IllegalArgumentException ex) {
            log.error(ex.getMessage(), ex);
            return ResponseEntity.notFound().build();
        }
    }
}
