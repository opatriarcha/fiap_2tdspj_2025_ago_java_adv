package br.com.fiap.tds._tdsq.Library.service;

import br.com.fiap.tds._tdsq.Library.domainmodel.User;
import br.com.fiap.tds._tdsq.Library.domainmodel.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository<User, UUID> userRepository;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(
                this.userRepository.findAll()
        );
    }

    @Override
    public User findById(UUID id){
        return this.userRepository.findById(id);
    }

    @Override
    public User create(User user) {
        return this.userRepository.create(user);
    }

    @Override
    public boolean existsById(UUID id) {
        return this.userRepository.existsById(id);
    }

    @Override
    public void removeById(UUID id) {
        this.userRepository.removeById(id);
    }
}
