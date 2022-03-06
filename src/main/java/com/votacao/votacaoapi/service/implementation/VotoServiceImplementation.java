package com.votacao.votacaoapi.service.implementation;

import com.votacao.votacaoapi.model.entity.Voto;
import com.votacao.votacaoapi.model.repository.VotoRepository;
import com.votacao.votacaoapi.service.VotoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VotoServiceImplementation implements VotoService {

    private VotoRepository repository;

    public VotoServiceImplementation(VotoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Voto save(Voto any) {
        return repository.save(any);
    }

    @Override
    public Optional<Voto> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Voto> votosDeUmaPauta(Long id) {
        return repository.findByPauta_Id(id);
    }


}
