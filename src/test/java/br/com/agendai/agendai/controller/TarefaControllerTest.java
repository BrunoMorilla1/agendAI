package br.com.agendai.agendai.controller;

import br.com.agendai.agendai.exception.TarefaNaoEncontradaException;
import br.com.agendai.agendai.model.*;
import br.com.agendai.agendai.service.ObterEstatisticas;

import br.com.agendai.agendai.service.ServicoTarefa;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TarefaControle.class)
@DisplayName("Testes de Integração do Controlador de Tarefas")
class TarefaControllerTest {

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

    Tarefa tarefaCriada = new Tarefa(
            "1",
            "Estudar java",
            "Revisar tarefas",
            StatusTarefa.PENDENTE,
            PrioridadeTarefa.ALTA,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null);

    when(servicoTarefa.criarTarefa(any(RequisicaoCriacaoTarefa.class))).thenReturn(tarefaCriada);

    mockMvc.perform(post("/tarefas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requisicao)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.idTarefa").value("1"))
        .andExpect(jsonPath("$.titulo").value("Estudar java"))
        .andExpect(jsonPath("$.discricao").value("Revisar tarefas"))
        .andExpect(jsonPath("$.status").value("PENDENTE"))
        .andExpect(jsonPath("$.prioridade").value("ALTA"));

    verify(servicoTarefa).criarTarefa(any(RequisicaoCriacaoTarefa.class));
    }

    @Test
    @DisplayName("POST /tarefas - Deve retornar erro 400 para dados inválidos")
    void deveRetornarErro400ParaDadosInvalidos() throws Exception{

        RequisicaoCriacaoTarefa requisicaoInvalida = new RequisicaoCriacaoTarefa("", "", null);

        mockMvc.perform(post("/tarefas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requisicaoInvalida)))
        .andExpect(status().isBadRequest());

    verify(servicoTarefa, never()).criarTarefa(any(RequisicaoCriacaoTarefa.class));
    }

    @Test
    @DisplayName("GET /tarefas - Deve retornar lista de tarefas")
    void deveRetornarListaDeTarefas() throws Exception{

        List<Tarefa> tarefa = Arrays.asList(
                new Tarefa("1", "Tarefa 1", "Descrição 1", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA,
                        LocalDateTime.now(), LocalDateTime.now(), null),
                new Tarefa("2", "Tarefa 2", "Descrição 2", StatusTarefa.CONCLUIDA, PrioridadeTarefa.MEDIA,
                        LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now())
        );

     when(servicoTarefa.listarTodasTarefas()).thenReturn(tarefa);


     mockMvc.perform(get("/tarefas"))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.length()").value(2))
             .andExpect(jsonPath("$[0].idTarefa").value("1"))
             .andExpect(jsonPath("$[0].titulo").value("Tarefa 1"))
             .andExpect((jsonPath("$[1].idTarefa").value("2")))
             .andExpect((jsonPath("$[1].titulo").value("Tarefa 2")));

     verify(servicoTarefa).listarTodasTarefas();
    }

    @Test
    @DisplayName(("GET /tarefas/{id} - Deve retornar tarefa por ID"))
    void deveRetornarTarefaPorId() throws Exception{

        String idTarefa = "1";
        Tarefa tarefa = new Tarefa(idTarefa, "Tarefa teste", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.MEDIA,
                LocalDateTime.now(), LocalDateTime.now(), null);

        when(servicoTarefa.buscarTarefaPorId(idTarefa)).thenReturn(tarefa);

        mockMvc.perform(get("/tarefas/{id}", idTarefa))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTarefa").value(idTarefa))
                .andExpect(jsonPath("$.titulo").value("Tarefa teste"));

        verify(servicoTarefa).buscarTarefaPorId(idTarefa);
    }

    @Test
    @DisplayName("GET /tarefas/{id} - Deve retornar 404 para tarefa não encontrada")
    void deveRetornarNotFoundParaTarefaNaoEncontrada() throws Exception{

        String idTarefa = "nao-existe";
        when(servicoTarefa.buscarTarefaPorId(idTarefa)).thenThrow(new TarefaNaoEncontradaException(idTarefa));


    mockMvc.perform(get("/tarefas/{id}", idTarefa))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.erro").value("Tarefa não encontrada"));

        verify(servicoTarefa).buscarTarefaPorId(idTarefa);
    }

    @Test
    @DisplayName("PUT /tarefas/{id} - Deve atualizar tarefa com sucesso")
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
                null);

        when(servicoTarefa.atualizarTarefa(eq(idTarefa), any(AtualizarTarefa.class)))
                .thenReturn(tarefaAtualizada);

        mockMvc.perform(put("/tarefas/{id}", idTarefa)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atualizarTarefa)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTarefa").value(idTarefa))
                .andExpect(jsonPath("$.titulo").value("Titulo Atualizado"))
                .andExpect(jsonPath("$.prioridade").value("URGENTE"));

        verify(servicoTarefa).atualizarTarefa(eq(idTarefa), any(AtualizarTarefa.class));
    }
    @Test
    @DisplayName("PATCH /tarefas/{id}/complete - Deve marcar tarefa como concluída")
    void deveConcluirTarefa() throws Exception{

        String tarefaId = "1";
        Tarefa tarefaConcluida = new Tarefa(
                tarefaId,
                "Tarefa",
                "Descrisção",
                StatusTarefa.CONCLUIDA,
                PrioridadeTarefa.MEDIA,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(servicoTarefa.concluirTarefa(tarefaId)).thenReturn(tarefaConcluida);

        mockMvc.perform(patch("/tarefas/{id}/concluir", tarefaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTarefa").value(tarefaId))
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));

        verify(servicoTarefa).concluirTarefa(tarefaId);
    }

    @Test
    @DisplayName("PATCH /tarefas/{id}/reabrir - Deve reabrir tarefa")
    void deveReabrirTarefa() throws Exception{

        String tarefaId = "1";
        Tarefa tarefaReaberta = new Tarefa(
            tarefaId,
            "Tarefa",
            "Descrição",
            StatusTarefa.PENDENTE,
            PrioridadeTarefa.MEDIA,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null);

        when(servicoTarefa.reabrirTarefa(tarefaId)).thenReturn(tarefaReaberta);

        mockMvc.perform(patch("/tarefas/{id}/reabrir", tarefaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTarefa").value(tarefaId))
                .andExpect(jsonPath("$.status").value("PENDENTE"));

        verify(servicoTarefa).reabrirTarefa(tarefaId);
        }

     @Test
     @DisplayName("DELETE /tarefas/{id} - Deve excluir tarefa com sucesso")
     void deveExcluirTarefa() throws Exception{

            String tarefaId = "1";
            doNothing().when(servicoTarefa).removerTarefa(tarefaId);

            mockMvc.perform(delete("/tarefas/{id}", tarefaId))
                    .andExpect(status().isNoContent());

            verify(servicoTarefa).removerTarefa(tarefaId);
        }

     @Test
     @DisplayName("GET /tarefas/status/{status} - Deve filtrar tarefas por status")
     void deveFiltrarTarefasPorStatus() throws Exception{

            StatusTarefa status = StatusTarefa.CONCLUIDA;
            List<Tarefa> tarefasConcluidas = Arrays.asList(
                    new Tarefa(
                            "1",
                            "Tarefa Concluida",
                            "Descrição",
                            StatusTarefa.CONCLUIDA,
                            PrioridadeTarefa.ALTA,
                            LocalDateTime.now(),
                            LocalDateTime.now(),
                            LocalDateTime.now()));

            when(servicoTarefa.listarTarefasPorStatus(status)).thenReturn(tarefasConcluidas);

            mockMvc.perform(get("/tarefas/status/{status}", status))
                    .andExpect((status().isOk()))
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].status").value("CONCLUIDA"));

            verify(servicoTarefa).listarTarefasPorStatus(status);
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

            when(servicoTarefa.listarTarefasPorPrioridade(prioridade)).thenReturn(tarefasPrioridadeAlta);

            // Quando & Então
            mockMvc.perform(get("/tarefas/prioridade/{prioridade}", prioridade))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].prioridade").value("ALTA"));

            verify(servicoTarefa).listarTarefasPorPrioridade(prioridade);
        }

        @Test
        @DisplayName("GET /tarefas/buscar - Deve buscar tarefas por termo")
        void deveBuscarTarefasPorTermo() throws Exception {
            // Dado
            String termoBusca = "Spring";
            List<Tarefa> resultadosBusca = Arrays.asList(
                    new Tarefa(
                            "1",
                            "Estudar Spring Boot",
                            "Descrição",
                            StatusTarefa.PENDENTE,
                            PrioridadeTarefa.ALTA,
                            LocalDateTime.now(),
                            LocalDateTime.now(),
                            null));

            when(servicoTarefa.buscarTarefasPorTermo(termoBusca)).thenReturn(resultadosBusca);

            mockMvc.perform(get("/tarefas/buscar")
                            .param("termo", termoBusca))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].titulo").value("Estudar Spring Boot"));

            verify(servicoTarefa).buscarTarefasPorTermo(termoBusca);
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

            when(servicoTarefa.obterEstatisticas()).thenReturn(obterEstatisticas);

            mockMvc.perform(get("/tarefas/estatisticas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalTarefas").value(3))
                    .andExpect(jsonPath("$.tarefasConcluidas").value(1))
                    .andExpect(jsonPath("$.tarefasPendentes").value(2))
                    .andExpect(jsonPath("$.porcentagemConcluidas").value(33.33333333333333));

            verify(servicoTarefa).obterEstatisticas();
    }
}