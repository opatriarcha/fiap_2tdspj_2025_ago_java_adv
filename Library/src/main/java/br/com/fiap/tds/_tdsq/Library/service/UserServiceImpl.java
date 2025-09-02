package br.com.fiap.tds._tdsq.Library.service;

import br.com.fiap.tds._tdsq.Library.domainmodel.User;
import br.com.fiap.tds._tdsq.Library.domainmodel.repositories.UserRepository;
import br.com.fiap.tds._tdsq.Library.domainmodel.repositories.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepositoryImpl userRepository;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(
                this.userRepository.findAll()
        );
    }

    @Override
    public User findById(UUID id){
        return this.userRepository.findById(id).orElse(null);
    }

    @Override
    public User create(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public boolean existsById(UUID id) {
        return this.userRepository.existsById(id);
    }

    @Override
    public void removeById(UUID id) {
        this.userRepository.deleteById(id);
    }

    @Override
    public void remove(User user) {
        this.removeById(user.getId());
    }

    public User partialUpdate(UUID id, User user) {
        if( !this.userRepository.existsById(id) )
            throw new IllegalArgumentException("Entity not found");
        User userFromDatabase = this.userRepository.findById(id).orElse(null);

        if(!userFromDatabase.getName().equals(user.getName()))
            userFromDatabase.setName(user.getName());
        if(!userFromDatabase.getEmail().equals(user.getEmail()))
            userFromDatabase.setEmail(user.getEmail());
        if(!userFromDatabase.getPassword().equals(user.getPassword()))
            userFromDatabase.setPassword(user.getPassword());
        return this.create(userFromDatabase);
    }
}
