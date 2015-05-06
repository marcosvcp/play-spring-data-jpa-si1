package models;

import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.data.repository.CrudRepository;

@Named
@Singleton
public interface AutorRepository extends CrudRepository<Autor, Long> {
}