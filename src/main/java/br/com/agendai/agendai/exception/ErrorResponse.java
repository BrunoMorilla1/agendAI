package br.com.agendai.agendai.exception;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private LocalDateTime dataHora;
    private int status;
    private String erro;
    private String mensagem;
    private Map<String, String> errosValidacao;
}
