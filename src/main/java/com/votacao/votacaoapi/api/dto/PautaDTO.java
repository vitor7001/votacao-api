package com.votacao.votacaoapi.api.dto;


import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PautaDTO {

    private Long id;

    @NotEmpty
    private String descricao;
    
    private String dataFim;

    private List Voto;
}
