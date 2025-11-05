package br.com.fiap.tds._tdsq.Library.presentation.controllers.hateoas;

import br.com.fiap.tds._tdsq.Library.domainmodel.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProfileRepository {
    public Optional<Profile> getProfileByUser(UUID userId){
        return Optional.of(new Profile());
    }
}
