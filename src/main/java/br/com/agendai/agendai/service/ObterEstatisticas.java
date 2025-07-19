package br.com.agendai.agendai.service;

import br.com.agendai.agendai.model.PrioridadeTarefa;
import br.com.agendai.agendai.model.StatusTarefa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Classe que representa as estatísticas das tarefas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObterEstatisticas {

    private long totalTarefas;
    private long tarefasConcluidas;
    private long tarefasPendentes;
    private Map<PrioridadeTarefa, Long> tarefasPorPrioridade;
    private Map<StatusTarefa, Long> tarefasPorStatus;

    public double getPorcentagemConcluidas() {
        if (totalTarefas == 0) {
            return 0.0;
        }
        return (double) tarefasConcluidas / totalTarefas * 100;
    }
}
