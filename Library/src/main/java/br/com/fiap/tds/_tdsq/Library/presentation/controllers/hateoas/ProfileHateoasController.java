package br.com.fiap.tds._tdsq.Library.presentation.controllers.hateoas;

import br.com.fiap.tds._tdsq.Library.domainmodel.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/hateoas/profiles")
@RequiredArgsConstructor
public class ProfileHateoasController {


    private final ProfileRepository profileRepository;

    @GetMapping("/user/{userId}")
    public EntityModel<Profile> getProfileByUser(@PathVariable UUID userId) {
        Profile profile = this.profileRepository.getProfileByUser(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(profile,
                linkTo(methodOn(ProfileHateoasController.class)
                        .getProfileByUser(userId)).withSelfRel(),
        linkTo(methodOn(UserHateoasController.class).getUser(userId)).withRel("user"));
    }
}
