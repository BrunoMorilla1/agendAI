package br.com.agendai.agendai.exception;

public class TarefaNaoEncontradaException extends RuntimeException{

    public TarefaNaoEncontradaException(String idTarefa) {
        super("Tarefa com ID '" + idTarefa + "' não foi encontrada");
    }
}
