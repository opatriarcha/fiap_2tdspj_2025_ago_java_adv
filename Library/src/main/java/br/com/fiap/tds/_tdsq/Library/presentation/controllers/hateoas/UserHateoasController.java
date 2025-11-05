package br.com.fiap.tds._tdsq.Library.presentation.controllers.hateoas;

import br.com.fiap.tds._tdsq.Library.domainmodel.Profile;
import br.com.fiap.tds._tdsq.Library.domainmodel.User;
import br.com.fiap.tds._tdsq.Library.domainmodel.repositories.UserRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/hateoas/users")
public class UserHateoasController {

    private final UserRepository repository;

    public UserHateoasController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public EntityModel<User> getUser(@PathVariable UUID id){
        User user = this.repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        //return ResponseEntity.ok(user);
        return EntityModel.of(user,
            linkTo(methodOn(UserHateoasController.class).getUser(id)).withSelfRel(),
            linkTo(methodOn(ProfileHateoasController.class).getProfileByUser(id)).withRel("profile")
//                linkTo(methodOn(PostHateoasController.class).getPostsByUser(id)).withRel("posts")

        );
    }

    @GetMapping
    public CollectionModel<EntityModel<User>> getAllUsers(){
        List<EntityModel<User>> users = this.repository.findAll()
                .stream()
                .map(user -> EntityModel.of( user,
                        linkTo(methodOn(UserHateoasController.class).getUser(user.getId())).withSelfRel()
                )).toList();
        return CollectionModel.of(users, linkTo(methodOn(UserHateoasController.class).getAllUsers()).withSelfRel());
    }


}
