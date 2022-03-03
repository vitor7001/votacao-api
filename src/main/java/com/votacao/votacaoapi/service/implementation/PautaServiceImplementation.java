package com.votacao.votacaoapi.service.implementation;

import com.votacao.votacaoapi.model.entity.Pauta;
import com.votacao.votacaoapi.model.repository.PautaRepository;
import com.votacao.votacaoapi.service.PautaService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PautaServiceImplementation implements PautaService {

    private PautaRepository repository;

    public PautaServiceImplementation(PautaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Pauta save(Pauta any) {
        return repository.save(any);
    }

    @Override
    public Optional<Pauta> getById(Long id) {
        return Optional.empty();
    }
}
