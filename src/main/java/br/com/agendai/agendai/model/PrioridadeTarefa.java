package br.com.agendai.agendai.model;

public enum PrioridadeTarefa {
    BAIXA("Baixa", 1),
    MEDIA("Média", 2),
    ALTA("Alta", 3),
    URGENTE("Urgente", 4);

    private final String descricao;
    private final int nivel;

    PrioridadeTarefa(String descricao, int nivel) {
        this.descricao = descricao;
        this.nivel = nivel;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getNivel() {
        return nivel;
    }
}