package one.digitalinnovation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import one.digitalinnovation.gof.model.*;
import one.digitalinnovation.gof.service.ViaCepService;
import one.digitalinnovation.gof.service.impl.ClienteServiceImpl;

@SpringBootTest
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private ViaCepService viaCepService;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        Endereco endereco = new Endereco();
        endereco.setCep("01001000");

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Gabriel");
        cliente.setEndereco(endereco);
    }

    @Test
    void deveBuscarClientePorId() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente resultado = clienteService.buscarPorId(1L);

        assertEquals("Gabriel", resultado.getNome());
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoExiste() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            clienteService.buscarPorId(1L);
        });
    }

    @Test
    void deveSalvarClienteComCepNovo() {
        when(enderecoRepository.findById("01001000")).thenReturn(Optional.empty());
        when(viaCepService.consultarCep("01001000")).thenReturn(cliente.getEndereco());
        when(clienteRepository.save(any())).thenReturn(cliente);

        Cliente salvo = (Cliente) clienteService.inserir(cliente);

        assertNotNull(salvo);
        verify(viaCepService, times(1)).consultarCep("01001000");
    }
}