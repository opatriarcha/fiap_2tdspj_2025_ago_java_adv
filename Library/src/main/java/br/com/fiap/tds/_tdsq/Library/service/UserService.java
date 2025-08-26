package br.com.fiap.tds._tdsq.Library.service;

import br.com.fiap.tds._tdsq.Library.domainmodel.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<User> findAll();

    User findById(UUID id);

    User create(User user);

    boolean existsById(UUID id);

    void removeById(UUID id);
}
