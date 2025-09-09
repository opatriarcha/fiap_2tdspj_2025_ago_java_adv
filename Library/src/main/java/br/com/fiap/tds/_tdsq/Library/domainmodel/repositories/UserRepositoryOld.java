package br.com.fiap.tds._tdsq.Library.domainmodel.repositories;

import java.util.List;

public interface UserRepositoryOld<T, ID> {
    List<T> findAll();

    T findById(ID id);

    T create(T user);

    boolean existsById(ID id);

    void removeById(ID id);
}
