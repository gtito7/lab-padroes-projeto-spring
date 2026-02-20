package one.digitalinnovation.gof.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends CrudRepository<Cliente, Long> {

    Page<Cliente> findAll(Pageable pageable);

    Page<Cliente> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

}