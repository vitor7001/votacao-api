package com.votacao.votacaoapi.service.implementation;

import com.votacao.votacaoapi.model.entity.Pauta;
import com.votacao.votacaoapi.model.repository.PautaRepository;
import com.votacao.votacaoapi.service.PautaService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        return repository.findById(id);
    }

    @Override
    public void delete(Pauta pauta) {
        repository.delete(pauta);
    }

    @Override
    public Pauta update(Pauta pauta) {
        return repository.save(pauta);
    }

    @Override
    public Page<Pauta> find(Pauta filtro, Pageable paginacao) {
        Example<Pauta> exemplo = Example.of(filtro,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)

        );
        return  repository.findAll(exemplo, paginacao);
    }
}
