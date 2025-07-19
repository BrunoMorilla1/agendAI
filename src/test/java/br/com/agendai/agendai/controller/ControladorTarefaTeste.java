package br.com.agendai.agendai.controller;

import br.com.agendai.agendai.exception.TarefaNaoEncontradaException;
import br.com.agendai.agendai.model.RequisicaoCriacaoTarefa;
import br.com.agendai.agendai.model.Tarefa;
import br.com.agendai.agendai.model.PrioridadeTarefa;
import br.com.agendai.agendai.model.StatusTarefa;
import br.com.agendai.agendai.service.ObterEstatisticas;

import br.com.agendai.agendai.service.ServicoTarefa;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static com.fasterxml.jackson.databind.cfg.CoercionInputShape.Array;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(TarefaControle.class)
@DisplayName("Testes de Integração do Controlador de Tarefas")
class TarefaControlleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServicoTarefa servicoTarefa;

    @Test
    @DisplayName("POST /tarefas - Deve criar tarefa com sucesso")
    void deveCriarTaref() throws Exception{
        RequisicaoCriacaoTarefa requisicao = new RequisicaoCriacaoTarefa(
            "Estudar java",
            "Revisar tarefas",
                PrioridadeTarefa.ALTA
);

    Tarefa tarefaCriada = new Tarefa("1", "Estudar java", "Revisar tarefas",
            StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDateTime.now(), LocalDateTime.now(), null);

    when(servicoTarefa.criarTarefa(any(RequisicaoCriacaoTarefa.class))).thenReturn(tarefaCriada);

    mockMvc.perform(post("/tarefas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requisicao)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("1"))
        .andExpect(jsonPath("$.titulo").value("Estudar java"))
        .andExpect(jsonPath("$.descricao").value("Revisar tarefas"))
        .andExpect(jsonPath("$.status").value("PENDENTE"))
        .andExpect(jsonPath("$.prioridade").value("ALTA"));

    verify(servicoTarefa).criarTarefa(any(RequisicaoCriacaoTarefa.class));
    }

    @Test
    @DisplayName("POST /tarefas - Deve retornar erro 400 para dados inválidos")
    void deveRetornarErro400ParaDadosInvalidos() throws Exception{

        RequisicaoCriacaoTarefa requisicaoInvalida = new RequisicaoCriacaoTarefa("", "", null);

        mockMvc.perform(post("tarefas"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requisicaoInvalida))
        .andExpect(status().isBadRequest());

    verify(servicoTarefa, never().criarTarefa(any(RequisicaoCriacaoTarefa.class)));
    }

    @Test
    @DisplayName("GET /tarefas - Deve retornar lista de tarefas")
    void deveRetornarListaDeTarefas() throws Exception{

        List<Tarefa> tarefa = Array.asList(
                new tarefa("1", "Tarefa 1", "Descrição 1", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA,
                        LocalDateTime.now(), LocalDateTime.now(), null),
                new tarefa("2", "Tarefa 2", "Descrição 2", StatusTarefa.CONCLUIDA, PrioridadeTarefa.MEDIA,
                        LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now())
        );

     when(servicoTarefa.obterTodasTarefas()).thenReturn(tarefas);


     mockMvc.perform(get("Tarefas"))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.length()").value(2))
             .andExpect(jsonPath("$[0].id").value("1"))
             .andExpect(jsonPath("$[0].titulo").value("Tarefa 1"))
             .andExpect((jsonPath("$[]1.id").value("2")))
             .andExpect((jsonPath("$[]1.titulo").value("Tarefa 2")));

     verify((servicoTarefa).obterTodasTarefas());
    }

    @Test
    @DisplayName(("GET /tarefas/{id} - Deve retornar tarefa por ID"))
    void deveRetornarTarefaPorId() throws Exception{

        String idTarefa = "1";
        Tarefa tarefa = new Tarefa(idTarefa, "Tarefa teste", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.MEDIA,
                LocalDateTime.now(), LocalDateTime.now(), null);

        when(servicoTarefa.obterTarefaPorId(IdTarefa)).thenReturn(tarefa);

        mockMvc.perform(get("/tarefas/{id}", idTarefa))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idTarefa))
                .andExpect(jsonPath("$.titulo").value("Tarefa teste"));

        verify(servicoTarefa).obterTarefaPorId(idTarefa);
    }

    @Test
    @DisplayName("GET /tarefas/{id} - Deve retornar 404 para tarefa não encontrada")
    void deveRetornarNotFoundParaTarefaNaoEncontrada() throws Exception{

        String idTarefa = "nao-existe";
        when(servicoTarefa.obter)TarefaPorId(idTarefa)).thenThrow(new TarefaNaoEncontradaException(idTarefa));


    mockMvc.perform(get("/tarefas/{id}", idTarefa))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.erro").value("Tarefa não encontrada"));

        verify(servico  Tarefa).obterTarefaPorId(idTarefa);
    }

    @Test
    @DisplayName("PUT /tasks/{id} - Deve atualizar tarefa com sucesso")
    void deveAtualizarTarefa() throws Exception{

        String idTarefa = "1";
        AtualizarTarefa atualizarTarefa = new AtualizarTarefa(
                "Titulo Atualizado",
                "Descrição Atualizada",
                PrioridadeTarefa.URGENTE
        );

        Tarefa tarefaAtualizada = new Tarefa(
                idTarefa,
                "Titulo Atualizado",
                "Descrição Atualizada",
                StatusTarefa.PENDENTE,
                PrioridadeTarefa.URGENTE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );

        when(servicoTarefa.atualizarTarefa(eq(idTarefa), any(AtualizarTarefa.class)))
                .thenReturn((tarefaAtualizada);

        mockMvc.perform(put("Tarefa/{id}", idTarefa))
                .contentType(MediaType.APPLICATION_JSON);
                .contentType(objectMapper.writeValueAsString(atualizarTarefa));
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idTarefa))
                .andExpect(jsonPath("$.titulo").value("Titulo atuaizado"))
                .andExpect(jsonPath("$.prioridade").value("URGENTE"));

        verify(servicoTarefa).atualizarTarefa(eq(idTarefa), any(AtualizarTarefa.class));
    }
    @Test
    @DisplayName("PATCH /tasks/{id}/complete - Deve marcar tarefa como concluída")
    void deveConcluirTarefa() throws Exception{

        String tarefaId = "1";
        Tarefa tarefaConcluida = new Tarefa(
                idTarefa,
                "Tarefa",
                "Descrisção",
                StatusTarefa.CONCLUIDA,
                PrioridadeTarefa.MEDIA,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(servicoTarefa.concluirTarefa(idTarefa)).thenReturn(tarefaConcluida);

        mockMvc.perform(patch("/tarefas/{id}/concluir", idTarefa))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tarefaId))
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));

        verify(servicoTarefa).concluirTarefa(tarefaId);
    }

    @Test
    @DisplayName("PATCH /tarefas/{id}/reabrir - Deve reabrir tarefa")
    void deveReabrirTarefa() throws Exception{

        Tarefa tarefaReaberta = new Tarefa{
            idTarefa,
            "Tarefa",
            "Descrição",
            Status.PENDENTE,
            PrioridadeTarefa.MEDIA,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null
        );

        when(servicoTarefa.tarefaReaberta(idTarefa)).thenReturn(tarefaReaberta);

        mockMvc.perform(patch("/tarefas/{id}/reabrir", idTarefa))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idTarefa))
                .andExpect(jsonPath("$.status").value("PENDENTE"));

        verify(servicoTarefa).reabrirTarefa(IdTarefa);
        }

     @Test
     @DisplayName("DELETE /tarefas/{id} - Deve excluir tarefa com sucesso")
     void deveExcluirTarefa() throw Exception{

            String idTarefa = 1;
            doNothing().when(servicoTarefa).excluirTarefa(idTarefa);

            mockMvc.perform(delete("/tarefas/{id}", idTarefa))
                    .andExpect(status().isNoContent());

            verify(servicoTarefa).excluirTarefa(idTarefa);
        }

     @Test
     @DisplayName("GET /tarefas/status/{status} - Deve filtrar tarefas por status")
     void deveFiltrarTarefasPorStatus() throw Exception{

            statusTarefa status StatusTarefa.CONCLLUIDA;
            List<Tarefa> tarefasConcluidas = Array.asList(
                    new Tarefa("1", "Tarefa Concluida", "Descrição", StatusTarefa.CONCLUIDA. PrioridadeTarefa.ALTA,
                            LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()));

            when(taskService.buscarTarefaPorStatus(status)).thenReturn(tarefasConcluidas);

            mockMvc.perform(get("/tarefas/status/{status}", status))
                    .andExpect((status().isOk()))
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].status").value("CONCLUIDA"));

            verify(servicoTarefa).buscarTarefasPorSatus(status);
        }
        @Test
        @DisplayName("GET /tarefas/prioridade/{prioridade} - Deve filtrar tarefas por prioridade")
        void deveFiltrarTarefasPorPrioridade() throws Exception {
            // Dado
            PrioridadeTarefa prioridade = PrioridadeTarefa.ALTA;
            List<Tarefa> tarefasPrioridadeAlta = Arrays.asList(
                    new Tarefa("1", "Tarefa Urgente", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA,
                            LocalDateTime.now(), LocalDateTime.now(), null)
            );

            when(servicoTarefa.buscarTarefasPorPrioridade(prioridade)).thenReturn(tarefasPrioridadeAlta);

            // Quando & Então
            mockMvc.perform(get("/tarefas/prioridade/{prioridade}", prioridade))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].priority").value("ALTA"));

            verify(servicoTarefa).buscarTarefasPorPrioridade(prioridade);
        }

        @Test
        @DisplayName("GET /tarefas/buscar - Deve buscar tarefas por termo")
        void deveBuscarTarefasPorTermo() throws Exception {
            // Dado
            String termoBusca = "Spring";
            List<Tarefa> resultadosBusca = Arrays.asList(
                    new Tarefa("1", "Estudar Spring Boot", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA,
                            LocalDateTime.now(), LocalDateTime.now(), null)
            );

            when(servicoTarefa.buscarTarefas(termoBusca)).thenReturn(resultadosBusca);

            // Quando & Então
            mockMvc.perform(get("/tarefas/buscar")
                            .param("termo", termoBusca))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].title").value("Estudar Spring Boot"));

            verify(servicoTarefa).buscarTarefas(termoBusca);
        }

        @Test
        @DisplayName("GET /tarefas/estatisticas - Deve retornar estatísticas das tarefas")
        void deveRetornarEstatisticasDasTarefas() throws Exception {
            // Dado
            Map<PrioridadeTarefa, Long> tarefasPorPrioridade = new HashMap<>();
            tarefasPorPrioridade.put(PrioridadeTarefa.ALTA, 2L);
            tarefasPorPrioridade.put(PrioridadeTarefa.MEDIA, 1L);

            Map<StatusTarefa, Long> tarefasPorStatus = new HashMap<>();
            tarefasPorStatus.put(StatusTarefa.PENDENTE, 2L);
            tarefasPorStatus.put(StatusTarefa.CONCLUIDA, 1L);

            ObterEstatisticas obterEstatisticas = ObterEstatisticas.builder()
                    .totalTarefas(3L)
                    .tarefasConcluidas(1L)
                    .tarefasPendentes(2L)
                    .tarefasPorPrioridade(tarefasPorPrioridade)
                    .tarefasPorStatus(tarefasPorStatus)
                    .build();

            when(servicoTarefa.obterEstatisticasTarefas()).thenReturn(estatisticas);

            mockMvc.perform(get("/tarefas/estatisticas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalTarefas").value(3))
                    .andExpect(jsonPath("$.tarefasConcluidas").value(1))
                    .andExpect(jsonPath("$.tarefasPendentes").value(2))
                    .andExpect(jsonPath("$.percentualConclusao").value(33.33333333333333));

            verify(servicoTarefa).obterEstatisticasTarefas();
        }
    }
}