package com.votacao.votacaoapi.api.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.votacao.votacaoapi.api.dto.PautaDTO;
import com.votacao.votacaoapi.api.dto.StatusDTO;
import com.votacao.votacaoapi.model.entity.Pauta;
import com.votacao.votacaoapi.service.PautaService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RequestMapping("/api/pautas")
@RestController
public class PautaController {

    private PautaService service;
    private ModelMapper modelMapper;

    public PautaController(PautaService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PautaDTO criar(@RequestBody @Valid PautaDTO dto){
        Pauta pautaASerSalva = modelMapper.map(dto, Pauta.class);

        pautaASerSalva = service.save(pautaASerSalva);

        return modelMapper.map(pautaASerSalva, PautaDTO.class);
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

    @PostMapping(path = "/votar/{id}/{cpf}",  produces= MediaType.APPLICATION_JSON_VALUE)
    public String votar(@PathVariable Long id, @PathVariable String cpf) throws Exception {
        try {

            RestTemplate instanciaRest = new RestTemplate();
            StatusDTO statusCpf = instanciaRest.getForObject("https://user-info.herokuapp.com/users/".concat(cpf), StatusDTO.class);

            String situacaoCpf = "";

            if(statusCpf.getStatus().equals("UNABLE_TO_VOTE")){
                situacaoCpf = "Este cpf não é permitido votar.";
            }else if(statusCpf.getStatus().equals("ABLE_TO_VOTE")){
                situacaoCpf =  "Seu voto foi computado.";
            }else {
                situacaoCpf ="Não foi possível verificar seu cpf no momento.";
            }

            StatusDTO situacao = StatusDTO.builder().status(situacaoCpf).build();
            String json = new ObjectMapper().writeValueAsString(situacao);

            return json;
        }catch (HttpStatusCodeException httpError){
            String json = new ObjectMapper().writeValueAsString("Houve um erro ao validar o cpf.");

            return json;
        }
    }
}
