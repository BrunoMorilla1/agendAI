package br.com.agendai.agendai.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestControllerAdvice
public class ManipuladorExcecaoGlobal {


    @ExceptionHandler(TarefaNaoEncontradaException.class)
    public ResponseEntity<ErrorResponse> tratarTarefaNaoEncontrada(TarefaNaoEncontradaException e){
        log.error("Tarefa não encontrada: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .dataHora(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .erro("Tarefa não encontrada")
                .mensagem(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> tratarErrosDeValidacao(MethodArgumentNotValidException e){
        log.error("Erro de validação: {}", e.getMessage());

        Map<String, String> erros = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((erro) ->{
         String nomeCampo = ((FieldError) erro).getField();
         String mensagemErro = erro.getDefaultMessage();
         erros.put(nomeCampo, mensagemErro);
    });

    ErrorResponse errorResponse = ErrorResponse.builder()
            .dataHora(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .erro("Erro de validação")
            .mensagem("Dados inválidos fornecidos")
            .errosValidacao(erros)
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
}
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> tratarExcecaoGenerica(Exception e){
     log.error("Erro interno do servidor: {}", e.getMessage(), e);

     ErrorResponse errorResponse = ErrorResponse.builder()
             .dataHora(LocalDateTime.now())
             .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
             .erro("Erro interno do servidor")
             .mensagem("Ocorreu um erro inesperado")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);

}
}
