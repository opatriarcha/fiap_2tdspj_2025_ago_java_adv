package br.com.fiap.tds._tdsq.Library.service;

import br.com.fiap.tds._tdsq.Library.domainmodel.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    public List<User> findAll();

    public User findById(UUID id);

    public User create(User user);

    public boolean existsById(UUID id);

    public void removeById(UUID id);

    public void remove(User user);

    public User partialUpdate(UUID id, User user);

}
