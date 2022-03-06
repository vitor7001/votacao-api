package com.votacao.votacaoapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Votos {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "pauta", referencedColumnName = "id")
    private Pauta pauta;

    @OneToOne
    @JoinColumn(name = "associado", referencedColumnName = "id")
    private Associado associado;

    private boolean votou;

}
