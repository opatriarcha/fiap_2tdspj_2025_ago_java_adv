package br.com.fiap.tds._tdsq.Library.service;

import br.com.fiap.tds._tdsq.Library.domainmodel.Post;
import br.com.fiap.tds._tdsq.Library.domainmodel.User;
import br.com.fiap.tds._tdsq.Library.domainmodel.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

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

    @Override
    public Collection<? extends User> findByEmail(String email) {
        List<User> users = new LinkedList<>();
        users.addAll(this.userRepository.findByEmail(email));
        return users;
    }


    public Collection<Post> getAllPostsFromUser(User user){
        return user.getPosts();
    }
}
