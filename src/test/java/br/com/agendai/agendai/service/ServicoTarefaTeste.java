package br.com.agendai.agendai.service;

import br.com.agendai.agendai.exception.TarefaNaoEncontradaException;
import br.com.agendai.agendai.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ServiçoDeTarefas")
class ServicoTarefaTeste {

    private ServicoTarefa servicoTarefa;

    @BeforeEach
    void setUp() {
        servicoTarefa = new ServicoTarefa();
    }

    @Test
    @DisplayName("Deve criar uma nova tarefa com sucesso")
    void deveCriarTarefaComSucesso() {
        // Given
        RequisicaoCriacaoTarefa requisicaoCriacaoTarefa = new RequisicaoCriacaoTarefa(
                "Estudar Spring Boot",
                "Revisar conceitos de Spring Boot para entrevista",
                PrioridadeTarefa.ALTA
        );

        // When
        Tarefa tarefaCriada = servicoTarefa.criarTarefa(requisicaoCriacaoTarefa);

        // Then
        assertThat(tarefaCriada).isNotNull();
        assertThat(tarefaCriada.getIdTarefa()).isNotNull();
        assertThat(tarefaCriada.getTitulo()).isEqualTo("Estudar Spring Boot");
        assertThat(tarefaCriada.getDiscricao()).isEqualTo("Revisar conceitos de Spring Boot para entrevista");
        assertThat(tarefaCriada.getPrioridade()).isEqualTo(PrioridadeTarefa.ALTA);
        assertThat(tarefaCriada.getStatus()).isEqualTo(StatusTarefa.PENDENTE);
        assertThat(tarefaCriada.getCriadoEm()).isNotNull();
        assertThat(tarefaCriada.getAtualizadoEm()).isNotNull();
        assertThat(tarefaCriada.getConcluidoEm()).isNull();
    }

    @Test
    @DisplayName("Deve retornar todas as tarefas ordenadas por prioridade")
    void deveRetornarTodasAsTarefasOrdenadasPorPrioridade() {
        // Given
        RequisicaoCriacaoTarefa tarefaBaixaPrioridade = new RequisicaoCriacaoTarefa("Tarefa Baixa", "Descrição", PrioridadeTarefa.BAIXA);
        RequisicaoCriacaoTarefa tarefaAltaPrioridade = new RequisicaoCriacaoTarefa("Tarefa Alta", "Descrição", PrioridadeTarefa.ALTA);
        RequisicaoCriacaoTarefa tarefaUrgente = new RequisicaoCriacaoTarefa("Tarefa Urgente", "Descrição", PrioridadeTarefa.URGENTE);

        servicoTarefa.criarTarefa(tarefaBaixaPrioridade);
        servicoTarefa.criarTarefa(tarefaAltaPrioridade);
        servicoTarefa.criarTarefa(tarefaUrgente);

        // When
        List<Tarefa> tarefas = servicoTarefa.listarTodasTarefas();

        // Then
        assertThat(tarefas).hasSize(3);
        assertThat(tarefas.get(0).getPrioridade()).isEqualTo(PrioridadeTarefa.URGENTE);
        assertThat(tarefas.get(1).getPrioridade()).isEqualTo(PrioridadeTarefa.ALTA);
        assertThat(tarefas.get(2).getPrioridade()).isEqualTo(PrioridadeTarefa.BAIXA);
    }

    @Test
    @DisplayName("Deve buscar tarefa por ID com sucesso")
    void deveBuscarTarefaPorIdComSucesso() {
        // Given
        RequisicaoCriacaoTarefa request = new RequisicaoCriacaoTarefa("Teste", "Descrição", PrioridadeTarefa.BAIXA);
        Tarefa tarefaCriada = servicoTarefa.criarTarefa(request);

        // When
        Tarefa tarefaEncontrada = servicoTarefa.buscarTarefaPorId(tarefaCriada.getIdTarefa());

        // Then
        assertThat(tarefaEncontrada).isNotNull();
        assertThat(tarefaEncontrada.getIdTarefa()).isEqualTo(tarefaCriada.getIdTarefa());
        assertThat(tarefaEncontrada.getTitulo()).isEqualTo("Teste");
    }

    @Test
    @DisplayName("Deve lançar exceção quando tarefa não for encontrada")
    void deveLancarExcecaoQuandoTarefaNaoForEncontrada() {
        // Given
        String idInexistente = "id-inexistente";

        // When & Then
        assertThatThrownBy(() -> servicoTarefa.buscarTarefaPorId(idInexistente))
                .isInstanceOf(TarefaNaoEncontradaException.class)
                .hasMessageContaining(idInexistente);
    }

    @Test
    @DisplayName("Deve atualizar tarefa com sucesso")
    void deveAtualizarTarefaComSucesso() {
        // Given
        RequisicaoCriacaoTarefa requisicao = new RequisicaoCriacaoTarefa("Título Original", "Descrição Original", PrioridadeTarefa.BAIXA);
        Tarefa tarefaCriada = servicoTarefa.criarTarefa(requisicao);

        AtualizarTarefa atualizarTarefa = new AtualizarTarefa("Título Atualizado", "Descrição Atualizada", PrioridadeTarefa.ALTA);

        Tarefa tarefaAtualizada = servicoTarefa.atualizarTarefa(tarefaCriada.getIdTarefa(), atualizarTarefa);

        // ThendeveAtualizarTarefaComSucesso
        assertThat(tarefaAtualizada.getTitulo()).isEqualTo("Título Atualizado");
        assertThat(tarefaAtualizada.getDiscricao()).isEqualTo("Descrição Atualizada");
        assertThat(tarefaAtualizada.getPrioridade()).isEqualTo(PrioridadeTarefa.ALTA);
        assertThat(tarefaAtualizada.getAtualizadoEm()).isAfter(tarefaAtualizada.getCriadoEm());
    }

    @Test
    @DisplayName("Deve marcar tarefa como concluída")
    void deveConcluirTarefaComSucesso() {
        // Given
        RequisicaoCriacaoTarefa requisicao = new RequisicaoCriacaoTarefa("Teste", "Descrição", PrioridadeTarefa.MEDIA);
        Tarefa tarefaCriada = servicoTarefa.criarTarefa(requisicao);

        // When
        Tarefa tarefaConcluída = servicoTarefa.concluirTarefa(tarefaCriada.getIdTarefa());

        // Then
        assertThat(tarefaConcluída.getStatus()).isEqualTo(StatusTarefa.CONCLUIDA);
        assertThat(tarefaConcluída.getConcluidoEm()).isNotNull();
        assertThat(tarefaConcluída.estaConcluida()).isTrue();
    }

    @Test
    @DisplayName("Deve reabrir tarefa concluída")
    void deveReiniciarTarefaConcluída() {
        // Given
        RequisicaoCriacaoTarefa requisicao = new RequisicaoCriacaoTarefa("Teste", "Descrição", PrioridadeTarefa.MEDIA);
        Tarefa tarefaCriada = servicoTarefa.criarTarefa(requisicao);
        servicoTarefa.concluirTarefa(tarefaCriada.getIdTarefa());

        // When
        Tarefa tarefaReaberta = servicoTarefa.reabrirTarefa(tarefaCriada.getIdTarefa());

        // Then
        assertThat(tarefaReaberta.getStatus()).isEqualTo(StatusTarefa.PENDENTE);
        assertThat(tarefaReaberta.getConcluidoEm()).isNull();
        assertThat(tarefaReaberta.estaConcluida()).isFalse();
    }

    @Test
    @DisplayName("Deve excluir tarefa com sucesso")
    void deveExcluirTarefaComSucesso() {
        // Given
        RequisicaoCriacaoTarefa requisicao = new RequisicaoCriacaoTarefa("Teste", "Descrição", PrioridadeTarefa.MEDIA);
        Tarefa tarefaCriada = servicoTarefa.criarTarefa(requisicao);

        // When
        servicoTarefa.removerTarefa(tarefaCriada.getIdTarefa());

        // Then
        assertThatThrownBy(() -> servicoTarefa.buscarTarefaPorId(tarefaCriada.getIdTarefa()))
                .isInstanceOf(TarefaNaoEncontradaException.class);
    }

    @Test
    @DisplayName("Deve filtrar tarefas por status")
    void deveFiltraTarefasPorStatus() {
        // Given
        RequisicaoCriacaoTarefa requisicao1 = new RequisicaoCriacaoTarefa("Tarefa 1", "Descrição", PrioridadeTarefa.MEDIA);
        RequisicaoCriacaoTarefa requisicao2 = new RequisicaoCriacaoTarefa("Tarefa 2", "Descrição", PrioridadeTarefa.MEDIA);

        Tarefa tarefa1 = servicoTarefa.criarTarefa(requisicao1);
        Tarefa tarefa2 = servicoTarefa.criarTarefa(requisicao2);
        servicoTarefa.concluirTarefa(tarefa1.getIdTarefa());

        // When
        List<Tarefa> tarefasConcluídas = servicoTarefa.listarTarefasPorStatus(StatusTarefa.CONCLUIDA);
        List<Tarefa> tarefasPendentes = servicoTarefa.listarTarefasPorStatus(StatusTarefa.PENDENTE);

        // Then
        assertThat(tarefasConcluídas).hasSize(1);
        assertThat(tarefasConcluídas.get(0).getIdTarefa()).isEqualTo(tarefa1.getIdTarefa());

        assertThat(tarefasPendentes).hasSize(1);
        assertThat(tarefasPendentes.get(0).getIdTarefa()).isEqualTo(tarefa2.getIdTarefa());
    }

    @Test
    @DisplayName("Deve filtrar tarefas por prioridade")
    void deveFiltraTarefasPorPrioridade() {
        // Given
        RequisicaoCriacaoTarefa altaPrioridade = new RequisicaoCriacaoTarefa("Alta Prioridade", "Descrição", PrioridadeTarefa.ALTA);
        RequisicaoCriacaoTarefa baixaPrioridade = new RequisicaoCriacaoTarefa("Baixa Prioridade", "Descrição", PrioridadeTarefa.BAIXA);

        servicoTarefa.criarTarefa(altaPrioridade);
        servicoTarefa.criarTarefa(baixaPrioridade);

        // When
        List<Tarefa> tarefasAltaPrioridade = servicoTarefa.listarTarefasPorPrioridade(PrioridadeTarefa.ALTA);
        List<Tarefa> tarefasBaixaPrioridade = servicoTarefa.listarTarefasPorPrioridade(PrioridadeTarefa.BAIXA);

        // Then
        assertThat(tarefasAltaPrioridade).hasSize(1);
        assertThat(tarefasAltaPrioridade.get(0).getTitulo()).isEqualTo("Alta Prioridade");

        assertThat(tarefasBaixaPrioridade).hasSize(1);
        assertThat(tarefasBaixaPrioridade.get(0).getTitulo()).isEqualTo("Baixa Prioridade");
    }

    @Test
    @DisplayName("Deve buscar tarefas por termo no título")
    void deveBuscarTarefasPorTermoNoTítulo() {
        // Given
        RequisicaoCriacaoTarefa requisicao1 = new RequisicaoCriacaoTarefa("Estudar Java", "Descrição", PrioridadeTarefa.MEDIA);
        RequisicaoCriacaoTarefa requisicao2 = new RequisicaoCriacaoTarefa("Estudar Spring", "Descrição", PrioridadeTarefa.MEDIA);
        RequisicaoCriacaoTarefa requisicao3 = new RequisicaoCriacaoTarefa("Fazer exercícios", "Descrição", PrioridadeTarefa.MEDIA);

        servicoTarefa.criarTarefa(requisicao1);
        servicoTarefa.criarTarefa(requisicao2);
        servicoTarefa.criarTarefa(requisicao3);

        // When
            List<Tarefa> tarefasDeEstudo = servicoTarefa.buscarTarefasPorTermo("Estudar");

            // Then
            assertThat(tarefasDeEstudo).hasSize(2);
            assertThat(tarefasDeEstudo).extracting(Tarefa::getTitulo)
                    .containsExactlyInAnyOrder("Estudar Java", "Estudar Spring");
        }

    @Test
    @DisplayName("Deve buscar tarefas por termo na descrição")
    void deveBuscarTarefasPorTermoNaDescrição() {
        // Given
        RequisicaoCriacaoTarefa requisicao1 = new RequisicaoCriacaoTarefa("Tarefa 1", "Preparar para entrevista", PrioridadeTarefa.MEDIA);
        RequisicaoCriacaoTarefa requisicao2 = new RequisicaoCriacaoTarefa("Tarefa 2", "Estudar algoritmos", PrioridadeTarefa.MEDIA);

        servicoTarefa.criarTarefa(requisicao1);
        servicoTarefa.criarTarefa(requisicao2);

        // When
        List<Tarefa> tarefasDeEntrevista = servicoTarefa.buscarTarefasPorTermo("entrevista");

        // Then
        assertThat(tarefasDeEntrevista).hasSize(1);
        assertThat(tarefasDeEntrevista.get(0).getTitulo()).isEqualTo("Tarefa 1");
    }

    @Test
    @DisplayName("Deve calcular estatísticas das tarefas")
    void deveCalcularEstatísticasDasTarefas() {
        // Given
        RequisicaoCriacaoTarefa requisicao1 = new RequisicaoCriacaoTarefa("Tarefa 1", "Descrição", PrioridadeTarefa.ALTA);
        RequisicaoCriacaoTarefa requisicao2 = new RequisicaoCriacaoTarefa("Tarefa 2", "Descrição", PrioridadeTarefa.BAIXA);
    }
}