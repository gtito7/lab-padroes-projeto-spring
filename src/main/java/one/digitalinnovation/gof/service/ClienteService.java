package one.digitalinnovation.gof.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import one.digitalinnovation.gof.model.Cliente;

/**
 * Interface que define o padrão <b>Strategy</b> no domínio de cliente. Com
 * isso, se necessário, podemos ter multiplas implementações dessa mesma
 * interface.
 * 
 * @author falvojr
 */
public interface ClienteService {

	Iterable<Cliente> buscarTodos();

	Page<Cliente> buscarTodos(Pageable pageable);

	Cliente buscarPorId(Long id);

	Cliente inserir(Cliente cliente);

	void atualizar(Long id, Cliente cliente);

	void deletar(Long id);

}
