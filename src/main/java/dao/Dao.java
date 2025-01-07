package dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/*
* Dao CRUD interface
* */
public interface Dao<K, E> {
    Optional<E> findById(K id);

    List<E> findAll() ;

    K save(E entity);

    void update(E entity);

    void delete(E entity);
}
