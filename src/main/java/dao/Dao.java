package dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/*
* Dao CRUD interface
* */
public interface Dao<K, E> {
    Optional<E> findById(K id)throws SQLException;

    List<E> findAll() throws SQLException;

    K save(E entity)throws SQLException;

    void update(E entity)throws SQLException;

    void delete(E entity)throws SQLException;
}
