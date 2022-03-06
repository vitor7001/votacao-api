package com.votacao.votacaoapi.api.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.votacao.votacaoapi.api.dto.PautaDTO;
import com.votacao.votacaoapi.api.dto.PautaDataFimDTO;
import com.votacao.votacaoapi.api.dto.StatusDTO;
import com.votacao.votacaoapi.model.entity.Pauta;
import com.votacao.votacaoapi.service.PautaService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RequestMapping("/api/pautas")
@RestController
public class PautaController {

    private final PautaService service;
    private final ModelMapper modelMapper;
    private final RestTemplate restTemplate;
    private final Random random;
    
    public PautaController(PautaService service, ModelMapper modelMapper,RestTemplate restTemplate,Random random) {
        this.service = service;
        this.modelMapper = modelMapper;
        this.restTemplate = restTemplate;
        this.random = random;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PautaDTO criar(@RequestBody @Valid PautaDTO dto){
        Pauta pautaASerSalva = modelMapper.map(dto, Pauta.class);

        pautaASerSalva = service.save(pautaASerSalva);

        return modelMapper.map(pautaASerSalva, PautaDTO.class);
    }

    @GetMapping
    public Page<PautaDTO> buscarPautas(PautaDTO dto, Pageable paginacao){
        Pauta filtro = modelMapper.map(dto, Pauta.class);

        Page<Pauta> pautas = service.find(filtro, paginacao);

        List<PautaDTO> listaDePautas = pautas.getContent().stream()
                .map(entidade -> modelMapper.map(entidade, PautaDTO.class))
                .collect(Collectors.toList());
        return new PageImpl<PautaDTO>(listaDePautas, paginacao, pautas.getTotalElements());
    }

    @GetMapping("{id}")
    public PautaDTO buscar(@PathVariable Long id){

        return service.getById(id)
                .map( pauta -> modelMapper.map(pauta, PautaDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id){
        Pauta pauta = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(pauta);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public PautaDTO atualizar(@PathVariable Long id,@RequestBody @Valid PautaDTO dto){
        Pauta pauta = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        pauta.setDescricao(dto.getDescricao());
        pauta = service.update(pauta);
        return modelMapper.map(pauta, PautaDTO.class);
    }

    @PutMapping(path="/iniciar/{id}",  produces= MediaType.APPLICATION_JSON_VALUE)
    public String iniciarPauta(@PathVariable Long id, @RequestBody PautaDataFimDTO dto) throws Exception {
        Pauta pauta = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if(pauta.getDataFim() != null && !pauta.getDataFim().isEmpty()){
            StatusDTO situacao = StatusDTO.builder().status("Está pauta já tem uma data de fim indicada.").build();
            return new ObjectMapper().writeValueAsString(situacao);
        }

        if(dto.getDataFim() != null){
            pauta.setDataFim(dto.getDataFim());
        }else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String dataAtualMaisUmMinuto = LocalDateTime.now().plusMinutes(1).format(formatter);
            pauta.setDataFim(dataAtualMaisUmMinuto.toString());
        }

        service.update(pauta);
        StatusDTO situacao = StatusDTO.builder().status("Pauta inicializada com sucesso.").build();
        return new ObjectMapper().writeValueAsString(situacao);
    }

    @PostMapping(path = "/votar/{id}/{cpf}",  produces= MediaType.APPLICATION_JSON_VALUE)
    public String votar(@PathVariable Long id, @PathVariable String cpf) throws Exception {

            Pauta pauta = service.getById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            if(pauta.getDataFim() == null){
                StatusDTO situacao = StatusDTO.builder().status("Está pauta não foi inicializada para ser votada.").build();
                return new ObjectMapper().writeValueAsString(situacao);
            }

            Boolean dataExpirada = this.verificarDataExpirada(pauta.getDataFim());

            if(dataExpirada){
                StatusDTO situacao = StatusDTO.builder().status("A data de fim desta pauta já expirou.").build();
                return new ObjectMapper().writeValueAsString(situacao);
            }

            Boolean situacaoCpf = this.verificarCpf(cpf);

            if(!situacaoCpf){
                StatusDTO situacao = StatusDTO.builder().status("Este cpf não é permitido votar").build();
                return new ObjectMapper().writeValueAsString(situacao);
            }



            StatusDTO situacao = StatusDTO.builder().status("Seu voto foi computado.").build();
            return new ObjectMapper().writeValueAsString(situacao);
    }

    private Boolean verificarDataExpirada(String dataFim) {

        LocalDateTime dataAtual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime dataPauta = LocalDateTime.parse(dataFim, formatter);

        if(!dataAtual.isBefore(dataPauta)){
            return true;
        }
        return false;
    }

    private Boolean verificarCpf(String cpf) {
        try {
            StatusDTO statusCpf = restTemplate.getForObject("https://user-info.herokuapp.com/users/".concat(cpf), StatusDTO.class);

            Boolean cpfValido = false;
            if (statusCpf.getStatus().equals("UNABLE_TO_VOTE")) {
                cpfValido = false;
            } else if (statusCpf.getStatus().equals("ABLE_TO_VOTE")) {
                cpfValido = true;
            }
            
            return cpfValido;
        }catch (Exception ex){
            // caso ocorra erro na requisição de verificação do cpf
            // esta parte simula se o cpf é válido ou inválido apenas para fins didáticos para não ter que inserir
            // uma função que faça essa verificação

            int numero = random.nextInt(20);
            
            if(numero % 2 == 0 ){
                return true;
            }
            return false;
        }
    }
}
