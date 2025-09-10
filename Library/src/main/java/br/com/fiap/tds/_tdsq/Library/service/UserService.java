package br.com.fiap.tds._tdsq.Library.service;

import br.com.fiap.tds._tdsq.Library.domainmodel.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    public List<User> findAll();

    public Optional<User> findById(UUID id);

    public User create(User user);

    public boolean existsById(UUID id);

    public void removeById(UUID id);

    public void remove(User user);

    public User partialUpdate(UUID id, User user);

    List<? extends User> findByEmail(String email);

    public Page<User> findAllPaged(Pageable pageable);
}
