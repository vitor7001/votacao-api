package com.votacao.votacaoapi.service;

import com.votacao.votacaoapi.model.entity.Pauta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PautaService {
    Pauta save(Pauta any);

    Optional<Pauta> getById(Long id);

    void delete(Pauta pauta);

    Pauta update(Pauta pauta);

    Page<Pauta> find(Pauta filtro, Pageable paginacao );
}
