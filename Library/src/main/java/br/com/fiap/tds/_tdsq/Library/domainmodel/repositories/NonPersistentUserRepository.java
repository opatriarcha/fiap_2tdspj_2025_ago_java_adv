package br.com.fiap.tds._tdsq.Library.domainmodel.repositories;

import br.com.fiap.tds._tdsq.Library.domainmodel.User;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.github.javafaker.Faker;


@Component
public class NonPersistentUserRepository implements UserRepository<User, UUID> {

    private List<User> internalData = new ArrayList<>();

    public NonPersistentUserRepository() {
        Faker faker = new Faker();

        for (int i = 0; i < 50; i++) {
            User user = new User(
                    UUID.randomUUID(),
                    faker.name().fullName(),
                    faker.internet().emailAddress(),
                    faker.internet().password(8, 16)
            );
            internalData.add(user);
        }
    }

    @Override
    public List<User> findAll() {
        return this.internalData.stream().toList();
    }

    @Override
    public User findById(UUID uuid) {
        User sampleUser = new User(uuid);

        if (this.internalData.contains(sampleUser))
            return this.internalData.get(this.internalData.indexOf(sampleUser));
        else
            throw new IllegalArgumentException("User not found on internal array!");
    }

    @Override
    public User create(User user) {
        this.internalData.add(user);
        return user;
    }

    @Override
    public boolean existsById(UUID uuid) {
        User sampleUser = new User(uuid);
        return this.internalData.contains(sampleUser);
    }

    @Override
    public void removeById(UUID uuid) {
        User user = this.findById(uuid);
        this.internalData.remove(this.internalData.indexOf(user));
    }


}
