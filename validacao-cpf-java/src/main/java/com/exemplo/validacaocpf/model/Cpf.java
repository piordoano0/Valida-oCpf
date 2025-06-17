package com.exemplo.validacaocpf.model;

import java.time.LocalDate;

public class Cpf {
    private String numero;
    private LocalDate dataNascimento;

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
}
