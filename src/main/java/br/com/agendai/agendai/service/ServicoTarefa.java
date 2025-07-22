package br.com.agendai.agendai.service;

import br.com.agendai.agendai.exception.TarefaNaoEncontradaException;
import br.com.agendai.agendai.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela lógica de negócio das tarefas
 * Demonstra o uso de Lambdas e Streams do Java 8+
 */
@Slf4j
@Service
public class ServicoTarefa {

    // Simulando um banco de dados em memória usando ConcurrentHashMap para segurança em múltiplas threads
    private final Map<String, Tarefa> bancoTarefas = new ConcurrentHashMap<>();

    /**
     * Cria uma nova tarefa
     */
    public Tarefa criarTarefa(RequisicaoCriacaoTarefa requisicao) {
        log.info("Criando nova tarefa: {}", requisicao.getTitulo());

        Tarefa tarefa = new Tarefa(requisicao.getTitulo(), requisicao.getDescricao(), requisicao.getPrioridade());
        bancoTarefas.put(tarefa.getIdTarefa(), tarefa);

        log.info("Tarefa criada com sucesso. ID: {}", tarefa.getIdTarefa());
        return tarefa;
    }

    /**
     * Retorna todas as tarefas ordenadas por prioridade
     */
    public List<Tarefa> listarTodasTarefas() {
        log.info("Buscando todas as tarefas");

        return bancoTarefas.values()
                .stream()
                .sorted(Comparator
                        .comparing((Tarefa t) -> t.getPrioridade().getNivel())
                        .reversed()
                        .thenComparing(Tarefa::getCriadoEm))
                .collect(Collectors.toList());
    }

    /**
     * Busca uma tarefa pelo ID
     */
    public Tarefa buscarTarefaPorId(String id) {
        log.info("Buscando tarefa por ID: {}", id);

        return Optional.ofNullable(bancoTarefas.get(id))
                .orElseThrow(() -> new TarefaNaoEncontradaException(id));
    }

    /**
     * Atualiza uma tarefa existente
     */
    public Tarefa atualizarTarefa(String id, AtualizarTarefa requisicao) {
        log.info("Atualizando tarefa ID: {}", id);

        Tarefa tarefa = buscarTarefaPorId(id);
        tarefa.atualizarTarefa(requisicao.getTitulo(), requisicao.getDescricao(), requisicao.getPrioridade());

        log.info("Tarefa atualizada com sucesso. ID: {}", id);
        return tarefa;
    }

    /**
     * Marca uma tarefa como concluída
     */
    public Tarefa concluirTarefa(String id) {
        log.info("Marcando tarefa como concluída. ID: {}", id);

        Tarefa tarefa = buscarTarefaPorId(id);
        tarefa.marcarComoConcluida();

        log.info("Tarefa marcada como concluída. ID: {}", id);
        return tarefa;
    }

    /**
     * Reabre uma tarefa (marca como pendente)
     */
    public Tarefa reabrirTarefa(String id) {
        log.info("Reabrindo tarefa. ID: {}", id);

        Tarefa tarefa = buscarTarefaPorId(id);
        tarefa.marcarComoPendente();

        log.info("Tarefa reaberta com sucesso. ID: {}", id);
        return tarefa;
    }

    /**
     * Remove uma tarefa
     */
    public void removerTarefa(String id) {
        log.info("Removendo tarefa. ID: {}", id);

        if (!bancoTarefas.containsKey(id)) {
            throw new TarefaNaoEncontradaException(id);
        }

        bancoTarefas.remove(id);
        log.info("Tarefa removida com sucesso. ID: {}", id);
    }

    /**
     * Filtra tarefas por status
     */
    public List<Tarefa> listarTarefasPorStatus(StatusTarefa status) {
        log.info("Filtrando tarefas por status: {}", status);

        return bancoTarefas.values()
                .stream()
                .filter(t -> t.getStatus().equals(status))
                .sorted(Comparator
                        .comparing((Tarefa t) -> t.getPrioridade().getNivel())
                        .reversed()
                        .thenComparing(Tarefa::getCriadoEm))
                .collect(Collectors.toList());
    }

    /**
     * Filtra tarefas por prioridade
     */
    public List<Tarefa> listarTarefasPorPrioridade(PrioridadeTarefa prioridade) {
        log.info("Filtrando tarefas por prioridade: {}", prioridade);

        return bancoTarefas.values()
                .stream()
                .filter(t -> t.getPrioridade().equals(prioridade))
                .sorted(Comparator.comparing(Tarefa::getCriadoEm))
                .collect(Collectors.toList());
    }

    /**
     * Busca tarefas por termo no título ou descrição
     */
    public List<Tarefa> buscarTarefasPorTermo(String termo) {
        log.info("Buscando tarefas com termo: {}", termo);

        String termoMinusculo = termo.toLowerCase();

        return bancoTarefas.values()
                .stream()
                .filter(t ->
                        t.getTitulo().toLowerCase().contains(termoMinusculo) ||
                                (t.getDiscricao() != null &&
                                        t.getDiscricao().toLowerCase().contains(termoMinusculo)))
                .sorted(Comparator
                        .comparing((Tarefa t) -> t.getPrioridade().getNivel())
                        .reversed()
                        .thenComparing(Tarefa::getCriadoEm))
                .collect(Collectors.toList());
    }

    /**
     * Retorna estatísticas das tarefas
     */
    public ObterEstatisticas obterEstatisticas() {
        log.info("Calculando estatísticas das tarefas");

        List<Tarefa> tarefas = new ArrayList<>(bancoTarefas.values());

        long total = tarefas.size();
        long concluidas = tarefas.stream().filter(Tarefa::estaConcluida).count();
        long pendentes = tarefas.stream().filter(t -> !t.estaConcluida()).count();

        Map<PrioridadeTarefa, Long> porPrioridade = tarefas.stream()
                .collect(Collectors.groupingBy(Tarefa::getPrioridade, Collectors.counting()));

        Map<StatusTarefa, Long> porStatus = tarefas.stream()
                .collect(Collectors.groupingBy(Tarefa::getStatus, Collectors.counting()));

        return ObterEstatisticas.builder()
                .totalTarefas(total)
                .tarefasConcluidas(concluidas)
                .tarefasPendentes(pendentes)
                .tarefasPorPrioridade(porPrioridade)
                .tarefasPorStatus(porStatus)
                .build();
    }

    /**
     * Retorna tarefas criadas dentro de um intervalo de datas
     */
    public List<Tarefa> listarTarefasEntreDatas(LocalDateTime inicio, LocalDateTime fim) {
        log.info("Buscando tarefas criadas entre {} e {}", inicio, fim);

        return bancoTarefas.values()
                .stream()
                .filter(t -> t.getCriadoEm().isAfter(inicio) && t.getCriadoEm().isBefore(fim))
                .sorted(Comparator.comparing(Tarefa::getCriadoEm).reversed())
                .collect(Collectors.toList());
    }
}
