package br.com.fiap.tds._tdsq.Library.presentation.controllers;

import br.com.fiap.tds._tdsq.Library.domainmodel.User;
import br.com.fiap.tds._tdsq.Library.presentation.controllers.transferObjects.UserDTO;
import br.com.fiap.tds._tdsq.Library.service.UserService;
import br.com.fiap.tds._tdsq.Library.service.UserServiceImpl;
import jakarta.validation.Valid;
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
    public ResponseEntity<UserDTO> findById(@PathVariable("id") UUID id){
//        User user = this.userService.findById(id);
//        if(user == null)
//            return ResponseEntity.notFound().build();
//        else
//            return ResponseEntity.ok(user);
        return this.userService.findById(id)
                .map(user -> ResponseEntity.ok(UserDTO.fromEntity(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDTO> save( @Valid @RequestBody UserDTO userDto){
        User newUser = this.userService.create(UserDTO.toEntity(userDto));
        return new ResponseEntity<>(UserDTO.fromEntity(newUser), HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteById(@RequestBody UUID id){
        if( !this.userService.existsById(id ))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        this.userService.removeById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/removeObject")
    public ResponseEntity<Void> delete(@RequestBody UserDTO user){
        if(!this.userService.existsById(user.getId()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        this.userService.remove(UserDTO.toEntity(user));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable("id") UUID id, @Valid @RequestBody UserDTO userDto){
        if( !this.userService.existsById(id) )
            return ResponseEntity.notFound().build();
        User user = UserDTO.toEntity(userDto);
        user.setId(id);
        return new ResponseEntity<>(
                UserDTO.fromEntity(this.userService.create(user)),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> partialUpdate(@PathVariable("id") UUID id, @Valid @RequestBody UserDTO userDto) {
        User updatedUser = null;
        try {
            updatedUser = this.userService.findById(userDto.getId()).orElse(null);
            return new ResponseEntity<>(
                    UserDTO.fromEntity(updatedUser),
                    HttpStatus.CREATED
            );
        } catch (IllegalArgumentException ex) {
            log.error(ex.getMessage(), ex);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name")
    public ResponseEntity<List<UserDTO>> findAllByName(@RequestParam String name){
        return null;
    }

    //http://localhost:8080/api/users?email=teste@gmail.com
    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> findAllByEmail(@RequestParam String email){

        return ResponseEntity.ok(
                new ArrayList<>(
                        this.userService.findByEmail(email)
                .stream()
                                .map(UserDTO::fromEntity)
                                .toList()));

    }
}
