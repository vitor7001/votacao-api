package com.votacao.votacaoapi.api.dto;


import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PautaDTO {

    private Long id;
    private String descricao;
    private String dataFim;
}
