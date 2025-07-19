package br.com.agendai.agendai.model;

public enum StatusTarefa {
    PENDENTE("Pendente"),
    EM_PROGRESSO("Em Progresso"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusTarefa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}