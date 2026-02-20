package one.digitalinnovation.gof.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private static final Logger log = LoggerFactory.getLogger(ClienteServiceImpl.class);

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final ViaCepService viaCepService;

    // ✅ Injeção por construtor
    public ClienteServiceImpl(
            ClienteRepository clienteRepository,
            EnderecoRepository enderecoRepository,
            ViaCepService viaCepService) {

        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
        this.viaCepService = viaCepService;
    }

    // =========================
    // BUSCAR TODOS
    // =========================
    @Transactional(readOnly = true)
    public Iterable<Cliente> buscarTodos() {
        log.info("Buscando todos os clientes");
        return clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Cliente> buscarTodos(Pageable pageable) {
        log.info("Buscando clientes paginados");
        return clienteRepository.findAll(pageable);
    }

    // =========================
    // BUSCAR POR ID
    // =========================
    @Override
    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        log.info("Buscando cliente ID: {}", id);

        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));
    }

    // =========================
    // BUSCAR POR NOME (NOVO RECURSO)
    // =========================
    @Transactional(readOnly = true)
    public Page<Cliente> buscarPorNome(String nome, Pageable pageable) {
        log.info("Buscando clientes pelo nome: {}", nome);
        return clienteRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    // =========================
    // INSERIR
    // =========================
    @Override
    public void inserir(Cliente cliente) {
        log.info("Inserindo cliente");
        return;
    }

    // =========================
    // ATUALIZAR
    // =========================
    @Override
    public void atualizar(Long id, Cliente cliente) {

        Cliente clienteExistente = buscarPorId(id);

        log.info("Atualizando cliente ID: {}", id);

        clienteExistente.setNome(cliente.getNome());
        clienteExistente.setEndereco(cliente.getEndereco());

        return;
    }

    // =========================
    // DELETAR
    // =========================
    @Override
    public void deletar(Long id) {

        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado.");
        }

        log.info("Deletando cliente ID: {}", id);
        clienteRepository.deleteById(id);
    }

    // =========================
    // EXISTE POR ID
    // =========================
    public boolean existePorId(Long id) {
        return clienteRepository.existsById(id);
    }

    // =========================
    // MÉTODO CENTRAL
    // =========================
    private Cliente salvarClienteComCep(Cliente cliente) {

        if (cliente.getEndereco() == null || cliente.getEndereco().getCep() == null) {
            throw new IllegalArgumentException("CEP é obrigatório.");
        }

        String cep = cliente.getEndereco().getCep();

        Endereco endereco = enderecoRepository.findById(cep)
                .orElseGet(() -> buscarEnderecoViaCep(cep));

        cliente.setEndereco(endereco);

        return clienteRepository.save(cliente);
    }

    // =========================
    // CACHE VIA CEP (INOVAÇÃO)
    // =========================
    @Cacheable("enderecos")
    public Endereco buscarEnderecoViaCep(String cep) {

        log.info("Consultando ViaCEP para CEP: {}", cep);

        Endereco novoEndereco = viaCepService.consultarCep(cep);

        if (novoEndereco == null) {
            throw new RuntimeException("CEP inválido.");
        }

        enderecoRepository.save(novoEndereco);

        return novoEndereco;
    }
}