package br.com.agendai.agendai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarefa {

    private String idTarefa;

    @NotBlank(message = "O título da tarefa não pode estar vazio")
    @Size(min = 3, max = 100, message = "O título deve ter entre 3 e 100 caracteres")
    private String titulo;

    @Size(max = 100, message = "A descrição não pode ter mais de 100 caracteres")
    private String discricao;

    @NotNull(message = "O status da tarefa é obrigatório")
    private StatusTarefa status;

    @NotNull(message = "A prioridade da tarefa é obrigatoria")
    private PrioridadeTarefa prioridade;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    private LocalDateTime concluidoEm;

    public Tarefa(String titulo, String discricao, PrioridadeTarefa prioridade) {
        this.idTarefa = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.discricao = discricao;
        this.status = StatusTarefa.PENDENTE;
        this.prioridade = prioridade;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    public void marcarComoConcluida(){
        this.status = StatusTarefa.CONCLUIDA;
        this.concluidoEm = java.time.LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    public void marcarComoPendente() {
        this.status = StatusTarefa.PENDENTE;
        this.concluidoEm = null;
        this.atualizadoEm = LocalDateTime.now();
    }

    public boolean estaConcluida() {
        return StatusTarefa.CONCLUIDA.equals(this.status);
    }

    public void atualizarTarefa(String titulo, String descricao, PrioridadeTarefa prioridade) {
        this.titulo = titulo;
        this.discricao = descricao;
        this.prioridade = prioridade;
        this.atualizadoEm = LocalDateTime.now();
    }

}