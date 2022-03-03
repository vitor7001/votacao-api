package com.votacao.votacaoapi.api.resource;

import com.votacao.votacaoapi.api.dto.PautaDTO;
import com.votacao.votacaoapi.model.entity.Pauta;
import com.votacao.votacaoapi.service.PautaService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public PautaDTO criar(@RequestBody PautaDTO dto){
        Pauta pautaASerSalva = modelMapper.map(dto, Pauta.class);

        pautaASerSalva = service.save(pautaASerSalva);

        return modelMapper.map(pautaASerSalva, PautaDTO.class);    }
}
