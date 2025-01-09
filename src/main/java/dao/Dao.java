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

    E save(E entity);

    E update(E entity);

    E delete(E entity);
}
