package com.votacao.votacaoapi.service;

import com.votacao.votacaoapi.model.entity.Pauta;

import java.util.Optional;

public interface PautaService {
    Pauta save(Pauta any);

    Optional<Pauta> getById(Long id);

    void delete(Pauta pauta);
}
