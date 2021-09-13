package com.example.tcc_sgd;

import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class Feedback {
    private RadioGroup radioGroupTamanho;
    private TextView textViewMovimentacao;
    private Button botaoEnviar;

    public Feedback(RadioGroup radioGroupTamanho, TextView textViewMovimentacao, Button botaoEnviar) {
        this.radioGroupTamanho = radioGroupTamanho;
        this.textViewMovimentacao = textViewMovimentacao;
        this.botaoEnviar = botaoEnviar;
    }

    public RadioGroup getRadioGroupTamanho() {
        return radioGroupTamanho;
    }

    public TextView getTextViewMovimentacao() {
        return textViewMovimentacao;
    }

    public Button getBotaoEnviar() {
        return botaoEnviar;
    }
}
