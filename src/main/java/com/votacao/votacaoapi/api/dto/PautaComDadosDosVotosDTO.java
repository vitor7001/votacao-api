package com.votacao.votacaoapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PautaComDadosDosVotosDTO {

    private int votosAFavor;
    private int votosContra;

}
