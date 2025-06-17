package com.exemplo.validacaocpf.service;

import com.exemplo.validacaocpf.model.Cpf;

public class CpfService {
    public boolean validar(Cpf cpf) {
        if (cpf.getNumero() == null || cpf.getDataNascimento() == null) {
            return false; // ou lance exceção
        }
        // lógica de validação do CPF e data de nascimento
        return true;
    }

}
