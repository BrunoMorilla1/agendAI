package br.com.agendai.agendai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarTarefa {

    @NotBlank(message = "O título da tarefa não pode estar vazio")
    @Size(min = 3, max = 100, message = "O título deve ter entre 3 e 100 caracteres")
    private String titulo;

    @Size(max = 500, message = "A descrição não pode ter mais de 500 caracteres")
    private String descricao;

    @NotNull(message = "A prioridade da tarefa é obrigatória")
    private PrioridadeTarefa prioridade;
}

