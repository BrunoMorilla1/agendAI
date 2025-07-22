package br.com.agendai.agendai.controller;

import br.com.agendai.agendai.model.*;
import br.com.agendai.agendai.service.ObterEstatisticas;
import br.com.agendai.agendai.service.ServicoTarefa;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tarefas")
@RequiredArgsConstructor
@Tag(name = "Tarefas", description = "API para gerenciamento de tarefas")
public class TarefaControle {

    private final ServicoTarefa servicoTarefa;

    @Operation(summary = "Criar uma nova tarefa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<Tarefa> criarTarefa(@Valid @RequestBody RequisicaoCriacaoTarefa requisicao) {
        log.info("Recebida requisição para criar tarefa: {}", requisicao.getTitulo());
        Tarefa novaTarefa = servicoTarefa.criarTarefa(requisicao);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaTarefa);
    }

    @Operation(summary = "Listar todas as tarefas")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<Tarefa>> listarTodasAsTarefas() {
        log.info("Recebida requisição para listar todas as tarefas");
        List<Tarefa> tarefas = servicoTarefa.listarTodasTarefas();
        return ResponseEntity.ok(tarefas);
    }

    @Operation(summary = "Buscar tarefa por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa encontrada"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Tarefa> buscarTarefaPorId(
            @Parameter(description = "ID da tarefa") @PathVariable String id) {
        log.info("Recebida requisição para buscar tarefa por ID: {}", id);
        Tarefa tarefa = servicoTarefa.buscarTarefaPorId(id);
        return ResponseEntity.ok(tarefa);
    }

    @Operation(summary = "Atualizar uma tarefa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Tarefa> atualizarTarefa(
            @Parameter(description = "ID da tarefa") @PathVariable String id,
            @Valid @RequestBody AtualizarTarefa requisicao) {
        log.info("Recebida requisição para atualizar tarefa ID: {}", id);
        Tarefa tarefaAtualizada = servicoTarefa.atualizarTarefa(id, requisicao);
        return ResponseEntity.ok(tarefaAtualizada);
    }

    @Operation(summary = "Marcar tarefa como concluída")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa marcada como concluída"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    @PatchMapping("/{id}/concluir")
    public ResponseEntity<Tarefa> concluirTarefa(
            @Parameter(description = "ID da tarefa") @PathVariable String id) {
        log.info("Recebida requisição para concluir tarefa. ID: {}", id);
        Tarefa tarefaConcluida = servicoTarefa.concluirTarefa(id);
        return ResponseEntity.ok(tarefaConcluida);
    }

    @Operation(summary = "Reabrir tarefa (marcar como pendente)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa reaberta com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    @PatchMapping("/{id}/reabrir")
    public ResponseEntity<Tarefa> reabrirTarefa(
            @Parameter(description = "ID da tarefa") @PathVariable String id) {
        log.info("Recebida requisição para reabrir tarefa. ID: {}", id);
        Tarefa tarefaReaberta = servicoTarefa.reabrirTarefa(id);
        return ResponseEntity.ok(tarefaReaberta);
    }

    @Operation(summary = "Excluir uma tarefa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirTarefa(
            @Parameter(description = "ID da tarefa") @PathVariable String id) {
        log.info("Recebida requisição para excluir tarefa. ID: {}", id);
        servicoTarefa.removerTarefa(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Filtrar tarefas por status")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas filtradas por status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Tarefa>> listarPorStatus(
            @Parameter(description = "Status da tarefa") @PathVariable StatusTarefa status) {
        log.info("Recebida requisição para listar tarefas por status: {}", status);
        List<Tarefa> tarefas = servicoTarefa.listarTarefasPorStatus(status);
        return ResponseEntity.ok(tarefas);
    }

    @Operation(summary = "Filtrar tarefas por prioridade")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas filtradas por prioridade")
    @GetMapping("/prioridade/{prioridade}")
    public ResponseEntity<List<Tarefa>> listarPorPrioridade(
            @Parameter(description = "Prioridade da tarefa") @PathVariable PrioridadeTarefa prioridade) {
        log.info("Recebida requisição para listar tarefas por prioridade: {}", prioridade);
        List<Tarefa> tarefas = servicoTarefa.listarTarefasPorPrioridade(prioridade);
        return ResponseEntity.ok(tarefas);
    }

    @Operation(summary = "Buscar tarefas por termo")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas que contêm o termo buscado")
    @GetMapping("/buscar")
    public ResponseEntity<List<Tarefa>> buscarTarefas(
            @Parameter(description = "Termo de busca") @RequestParam String termo) {
        log.info("Recebida requisição para buscar tarefas com termo: {}", termo);
        List<Tarefa> tarefas = servicoTarefa.buscarTarefasPorTermo(termo);
        return ResponseEntity.ok(tarefas);
    }

    @Operation(summary = "Obter estatísticas das tarefas")
    @ApiResponse(responseCode = "200", description = "Estatísticas das tarefas")
    @GetMapping("/estatisticas")
    public ResponseEntity<ObterEstatisticas> obterEstatisticas() {
        log.info("Recebida requisição para obter estatísticas das tarefas");
        ObterEstatisticas estatisticas = servicoTarefa.obterEstatisticas();
        return ResponseEntity.ok(estatisticas);
    }

    @Operation(summary = "Buscar tarefas criadas em um período")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas criadas no período especificado")
        @GetMapping("/criadas-entre")
    public ResponseEntity<List<Tarefa>> buscarTarefasPorPeriodo(
            @Parameter(description = "Data de início")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @Parameter(description = "Data de fim")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        log.info("Recebida requisição para buscar tarefas criadas entre {} e {}", dataInicio, dataFim);
        List<Tarefa> tarefas = servicoTarefa.listarTarefasEntreDatas(dataInicio, dataFim);
        return ResponseEntity.ok(tarefas);
    }
}
