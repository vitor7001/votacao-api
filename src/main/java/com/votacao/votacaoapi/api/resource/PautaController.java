package com.votacao.votacaoapi.api.resource;

import com.votacao.votacaoapi.api.dto.PautaDTO;
import com.votacao.votacaoapi.model.entity.Pauta;
import com.votacao.votacaoapi.service.PautaService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;
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
        pauta.setDataFim(dto.getDataFim());
        pauta = service.update(pauta);
        return modelMapper.map(pauta, PautaDTO.class);
    }
}
