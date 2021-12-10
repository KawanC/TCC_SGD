package com.example.tcc_sgd;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EnviarDenuncia {
   private String texto, tipoProblema, idUsuario, nomeEstabelecimento, enderecoEstabelecimento;
   private FirebaseFirestore feed = FirebaseFirestore.getInstance();



    public EnviarDenuncia(String tipoProblema, String idUsuario, String nomeEstabelecimento, String enderecoEstabelecimento, String texto) {
        this.tipoProblema = tipoProblema;
        this.idUsuario = idUsuario;
        this.nomeEstabelecimento = nomeEstabelecimento;
        this.enderecoEstabelecimento = enderecoEstabelecimento;
        this.texto = texto;
    }


    public void enviarDenuncia(View view, String idUsuario, String nomeEstabelecimento, String enderecoEstabelecimento, Context context){

        // Desabilitando o dialog
        RadioGroup tipoDenuncia = view.findViewById(R.id.radioGroupReportarErro);
        RadioButton tamanhoEstabelecimentoDenuncia = view.findViewById(R.id.radioButtonCorrigirTamanho);
        RadioButton movimentacaoAtual = view.findViewById(R.id.radioButtonCorrigirMovimentacao);
        RadioButton mediaDia = view.findViewById(R.id.radioButtonCorrigirMedia);
        EditText textoDenuncia = view.findViewById(R.id.editTextDenuncia);

        int radioId = tipoDenuncia.getCheckedRadioButtonId(); //pegando id do botão selecionado

        if (tamanhoEstabelecimentoDenuncia.isChecked() || movimentacaoAtual.isChecked() || mediaDia.isChecked()) {
            switch (radioId) {
                case R.id.radioButtonCorrigirTamanho:
                    tipoProblema = "Tamanho do estabelecimento errado";
                    break;
                case R.id.radioButtonCorrigirMedia:
                    tipoProblema = "Media do dia errada";
                    break;
                case R.id.radioButtonCorrigirMovimentacao:
                    tipoProblema = "Movimentação atual errada";
                    break;
            }
            
            if(!textoDenuncia.getText().toString().isEmpty()){
            
            EnviarDenuncia denuncia = new EnviarDenuncia(tipoProblema, idUsuario, nomeEstabelecimento, enderecoEstabelecimento,
                    textoDenuncia.getText().toString());
            String horaDenuncia = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());

            feed.collection("Denuncias").document(nomeEstabelecimento + "-" + idUsuario + "-" + horaDenuncia)
                    .set(denuncia).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context, "Erro reportado com sucesso", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Falha ao enviar", Toast.LENGTH_SHORT).show();

                }
            });

            }else{
                Toast.makeText(view.getContext(), "Escreva no espaço do texto para uma melhor eficiência de sua denúncia", Toast.LENGTH_SHORT).show();
            }
            
            }else{
            Toast.makeText(view.getContext(), "Selecione a sua denúncia e coloque um texto especificando o problema!", Toast.LENGTH_SHORT).show();
        }

     }

    public String getTexto() {
        return texto;
    }

    public String getTipoProblema() {
        return tipoProblema;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getNomeEstabelecimento() {
        return nomeEstabelecimento;
    }

    public String getEnderecoEstabelecimento() {
        return enderecoEstabelecimento;
    }
}
    

