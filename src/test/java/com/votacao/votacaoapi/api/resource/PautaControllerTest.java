package com.votacao.votacaoapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.votacao.votacaoapi.api.dto.PautaDTO;
import com.votacao.votacaoapi.model.entity.Pauta;
import com.votacao.votacaoapi.service.PautaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class PautaControllerTest {

    static String PAUTA_API = "/api/pautas";

    @MockBean
    PautaService service;

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("Deve criar uma pauta.")
    public void criarPauta() throws Exception{

        PautaDTO dto = PautaDTO.builder().descricao("Descrição").dataFim("").build();
        Pauta pautaSalva = Pauta.builder().id(10L).descricao("Descrição").dataFim("").build();

        BDDMockito.given(service.save(Mockito.any(Pauta.class))).willReturn(pautaSalva);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(PAUTA_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json);
        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(10L))
                .andExpect(jsonPath("descricao").value(dto.getDescricao()));

    }

    @Test
    @DisplayName("Deve retornar erro na criação de uma pauta inválida.")
    public void criarPautaInvalida() {
    }
}
