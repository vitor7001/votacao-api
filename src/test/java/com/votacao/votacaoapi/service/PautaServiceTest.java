package com.votacao.votacaoapi.service;

import com.votacao.votacaoapi.model.entity.Pauta;
import com.votacao.votacaoapi.model.repository.PautaRepository;
import com.votacao.votacaoapi.service.implementation.PautaServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class PautaServiceTest {

    PautaService service;
    @MockBean
    PautaRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new PautaServiceImplementation(repository);
    }

    @Test
    @DisplayName("Deve salvar uma pauta.")
    public  void salvarPauta(){
        Pauta pauta = Pauta.builder().descricao("Qualquer descrição").build();

        Mockito.when(repository.save(pauta)).thenReturn(Pauta.builder().id(5L).descricao("Qualquer descrição").build());

        Pauta pautaSalva = service.save(pauta);

        assertThat(pautaSalva.getId()).isNotNull();
        assertThat(pautaSalva.getDescricao()).isEqualTo("Qualquer descrição");
    }

}
