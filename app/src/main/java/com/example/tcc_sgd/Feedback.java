package com.example.tcc_sgd;

import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class Feedback {
    private String nomeEstabelecimento, movimentoEstabelecimento, tipoEstabelecimento, tamanhoEstabelecimento, horaFeedBack, dataFeedBack, idUsuario, enderecoLocal;

    public Feedback(String nomeEstabelecimento, String movimentoEstabelecimento, String tipoEstabelecimento, String tamanhoEstabelecimento, String horaFeedBack,String dataFeedBack, String idUsuario, String enderecoLocal) {
        this.nomeEstabelecimento = nomeEstabelecimento;
        this.movimentoEstabelecimento = movimentoEstabelecimento;
        this.tipoEstabelecimento = tipoEstabelecimento;
        this.tamanhoEstabelecimento = tamanhoEstabelecimento;
        this.horaFeedBack = horaFeedBack;
        this.dataFeedBack = dataFeedBack;
        this.idUsuario = idUsuario;
        this.enderecoLocal = enderecoLocal;
    }

    public String getNomeEstabelecimento() {
        return nomeEstabelecimento;
    }

    public String getMovimentoEstabelecimento() {
        return movimentoEstabelecimento;
    }

    public String getTipoEstabelecimento() {
        return tipoEstabelecimento;
    }

    public String getTamanhoEstabelecimento() {
        return tamanhoEstabelecimento;
    }

    public String getHoraFeedBack() { return horaFeedBack; }

    public String getDataFeedBack() { return dataFeedBack; }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getEnderecoLocal() { return enderecoLocal; }
}
