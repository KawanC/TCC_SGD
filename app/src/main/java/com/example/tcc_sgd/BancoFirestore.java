package com.example.tcc_sgd;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BancoFirestore {
    //Atributos
    String idFeed, dataF, horaF, valorFinal, valorData;
    AlertDialog.Builder builderDialog;
    AlertDialog alertDialog;
    FirebaseFirestore bancoDeDados = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ProgressDialog progressDialog;



    public void enviarFeedBack(Estabelecimento estabelecimento, String tamanhoEstabelecimento, int numeroMovimentacao, String tipoEstabelecimento, String usuarioID,
                               RadioButton radioButtonPequeno,RadioButton radioButtonMedio, RadioButton radioButtonGrande , BottomSheetDialog bottomSheetDialogEtapa2, View view, Activity activity){
        //ATUALIZANDO A HORA
        String horaFeedback = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        String dataAtualString = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        estabelecimento.setHora(horaFeedback);
        // CODIGO BANCO DE DADOS
        if (radioButtonPequeno.isChecked() || radioButtonMedio.isChecked() || radioButtonGrande.isChecked()) {
            Feedback feedbackUsuario = new Feedback(estabelecimento.getNome(), Integer.toString(numeroMovimentacao), tipoEstabelecimento,
                    tamanhoEstabelecimento, estabelecimento.getHora(),dataAtualString, usuarioID, estabelecimento.getEndereco());

            bancoDeDados.collection("Feedbacks").document(estabelecimento.getNome() + estabelecimento.getHora()).set(feedbackUsuario).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    showAlertDialogFeedback(R.layout.dialog_sucesso_feedback, view, activity);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    Toast.makeText(view.getContext(), "ERRO" + e.getMessage() , Toast.LENGTH_SHORT).show();
                    showAlertDialogFeedback(R.layout.dialog_erro_feedback, view, activity);
                }
            });
            bottomSheetDialogEtapa2.dismiss();
        }else {
            Toast.makeText(view.getContext(), "Por favor selecione o tamanho do Estabelecimento!", Toast.LENGTH_SHORT).show();
        }
    }

    public void apagarDados(String idItem){
        bancoDeDados.collection("Feedbacks").document(idItem).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void confereDiaEHora(Context context){
        String horaAtualString = new SimpleDateFormat("HH").format(Calendar.getInstance().getTime());
        String dataAtualString = new SimpleDateFormat("dd").format(Calendar.getInstance().getTime());
        bancoDeDados.collection("Feedbacks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        idFeed = doc.getId(); //PEGANDO ID DO FEEDBACKS NO BANCO
                        horaF = (String) doc.get("horaFeedBack"); //PEGANDO A HORA DO FEEDBACKS
                        dataF =  (String) doc.get("dataFeedBack"); //PEGANDO A DATA DO FEEDBACKS
                            //PEGANDO OS 2 PRIMEIROS CARACTERES DA DATA E DA HORA
                            char data1 = dataF.charAt(0);
                            char data2 = dataF.charAt(1);
                            valorData = "" + data1 + data2;
                            char numero1 = horaF.charAt(0);
                            char numero2 = horaF.charAt(1);
                            valorFinal = "" + numero1 + numero2;
                            //CONVERTENDO A STRING PARA INT PARA PODER REALIZAR O CALCULO E COMPARACAÇÃO
                            int dataFinal = Integer.parseInt(valorData);
                            int dataAtual = Integer.parseInt(dataAtualString);
                            int horaAtual = Integer.parseInt(horaAtualString);
                            int horaFeedBack = Integer.parseInt(valorFinal);
                            //VERIFICANDO SE JÁ PASSOU UM DIA DESDE O FEEDBACK
                            if (dataAtual != dataFinal){
                                //LOGICA PARA APAGAR APENAS OS FEEEDBACKS QUE SÃO DO DIA ANTERIOR E PASSARAM DAS 3 HORAS DA MANHA
                                if (horaAtual == 00 || horaAtual == 01 || horaAtual == 02 && horaFeedBack == 22 || horaFeedBack == 23){
                                } else {
                                    apagarDados(idFeed);
                                }
                            }
                        dataF = null;
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "ERRO AO LIMPAR O BANCO", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void pesquisarMovimento(Estabelecimento estabelecimento, TextView textViewMediaHoje, TextView textViewNomeInformacao,
                                   TextView textViewEnderecoInformaco, TextView textViewTamanhoInformacao, TextView textViewHora,
                                   TextView textViewFeedAnalisados, TextView textViewMovimentoAtual, TextView textViewMovientoInfo,
                                   LinearLayout linearLayoutSeekBarAtual, LinearLayout linearLayoutSeekBar, ImageView imageViewIconEstabelecimentoInfo,
                                   final int contador[], final int movimentacaoNumero[], final String movimentacao[], View view,
                                   BottomSheetDialog bottomSheetView, ProgressDialog progressDialog){

        //PEGANDO O DIA ATUAL DE ACORDO COM A DATA DO APARELHO
        String diaAtual = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        CollectionReference colection = bancoDeDados.collection("Feedbacks");
        colection.whereEqualTo("enderecoLocal", estabelecimento.getEndereco())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    contador[0] = 0;
                    movimentacaoNumero[0] = 0;
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        contador[0]++;
                        movimentacaoNumero[0] += Integer.parseInt((String)doc.get("movimentoEstabelecimento")); // SOMA PARA DEPOIS SER EFETUADA A MEDIA
                        movimentacaoNumero[1] = Integer.parseInt((String)doc.get("movimentoEstabelecimento")); //APENAS ARMAZENA O ULTIMO NUMERO DA MOVIMENTAÇÃO
                        movimentacao[0] = (String) doc.get("tipoEstabelecimento"); // ARMAZENA O TIPO DO ESTABELECIMENTO
                        movimentacao[1] = (String) doc.get("horaFeedBack"); // PEGA A HORA DO ULTIMO FEDDBACK
                        movimentacao[2] = (String) doc.get("tamanhoEstabelecimento"); // ARMAZENA O TAMANHO DO ESTABELECIMENTO

                    }
                    textViewMediaHoje.setText("Média da movimentação no dia de hoje\n (" + diaAtual + ")");
                    if (contador[0] != 0){
                        mudancaImagem(movimentacao[0], bottomSheetView, imageViewIconEstabelecimentoInfo);
                        int media = movimentacaoNumero[0] /(contador[0]);
                        switch (movimentacao[0]){
                            //TESTANDO O TIPO DO ESTABELECIMENTO DE ACORDO COM OS FEEDBACKS
                            case "Shopping":
                                switch (movimentacao[2]){
                                    case "Pequeno":
                                        //IFs PARA TESTAR EM QUAL ESCALA SE ENCAIXA O ULTIMO FEDEBACKS
                                        if (movimentacaoNumero[1] == 0){
                                            textViewMovimentoAtual.setText("Vazio (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#000000"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_vazio));
                                        }

                                        if (movimentacaoNumero[1] > 0 && movimentacaoNumero[1] <= 1000){
                                            textViewMovimentoAtual.setText("Pouco Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#4CAF50"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_pouco_movimentado));
                                        }
                                        if (movimentacaoNumero[1] > 1000 && movimentacaoNumero[1] <= 3000){
                                            textViewMovimentoAtual.setText("Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#FFFB18"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_movimentado));
                                        }

                                        if (movimentacaoNumero[1] > 3000){
                                            textViewMovimentoAtual.setText("Muito Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#FF0000"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar));
                                        }

                                        //IFs PARA TESTAR A MEDIA E ESTABELECER AS CORES APARENTES AO USUARIO
                                        if(media == 0){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#000000"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_vazio));
                                        }

                                        if (media > 0 && media <= 1000){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#4CAF50"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_pouco_movimentado));
                                        }

                                        if (media > 1000 && media <= 3000){

                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#FFFB18"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_movimentado));
                                        }

                                        if (media > 3000){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#FF0000"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar));
                                        }
                                        break;
                                    case "Médio":
                                        //IFs PARA TESTAR EM QUAL ESCALA SE ENCAIXA O ULTIMO FEDEBACKS
                                        if (movimentacaoNumero[1] == 0){
                                            textViewMovimentoAtual.setText("Vazio (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#000000"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_vazio));
                                        }

                                        if (movimentacaoNumero[1] > 0 && movimentacaoNumero[1] <= 2500){
                                            textViewMovimentoAtual.setText("Pouco Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#4CAF50"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_pouco_movimentado));
                                        }
                                        if (movimentacaoNumero[1] > 2500 && movimentacaoNumero[1] <= 5000){
                                            textViewMovimentoAtual.setText("Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#FFFB18"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_movimentado));
                                        }

                                        if (movimentacaoNumero[1] > 5000){
                                            textViewMovimentoAtual.setText("Muito Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#FF0000"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar));
                                        }

                                        //IFs PARA TESTAR A MEDIA E ESTABELECER AS CORES APARENTES AO USUARIO
                                        if(media == 0){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#000000"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_vazio));
                                        }

                                        if (media > 0 && media <= 2500){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#4CAF50"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_pouco_movimentado));
                                        }

                                        if (media > 2500 && media <= 5000){

                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#FFFB18"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_movimentado));
                                        }

                                        if (media > 5000){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#FF0000"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar));
                                        }
                                        break;
                                    case "Grande":
                                        //IFs PARA TESTAR EM QUAL ESCALA SE ENCAIXA O ULTIMO FEDEBACKS
                                        if (movimentacaoNumero[1] == 0){
                                            textViewMovimentoAtual.setText("Vazio (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#000000"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_vazio));
                                        }

                                        if (movimentacaoNumero[1] > 0 && movimentacaoNumero[1] <= 3000){
                                            textViewMovimentoAtual.setText("Pouco Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#4CAF50"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_pouco_movimentado));
                                        }
                                        if (movimentacaoNumero[1] > 3000 && movimentacaoNumero[1] <= 7500){
                                            textViewMovimentoAtual.setText("Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#FFFB18"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_movimentado));
                                        }

                                        if (movimentacaoNumero[1] > 7500){
                                            textViewMovimentoAtual.setText("Muito Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#FF0000"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar));
                                        }

                                        //IFs PARA TESTAR A MEDIA E ESTABELECER AS CORES APARENTES AO USUARIO
                                        if(media == 0){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#000000"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_vazio));
                                        }

                                        if (media > 0 && media <= 175){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#4CAF50"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_pouco_movimentado));
                                        }

                                        if (media > 175 && media <= 300){

                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#FFFB18"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_movimentado));
                                        }

                                        if (media > 300){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#FF0000"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar));
                                        }
                                        break;
                                }

                                break;

                            case "Restaurante":
                                //TERSTANDO O TAMANHO DO ESTABELECIMENTO DE ACORDO COM OS FEEDBACKS
                                switch (movimentacao[2]){
                                    case "Pequeno":
                                        //IFs PARA TESTAR EM QUAL ESCALA SE ENCAIXA O ULTIMO FEDEBACKS
                                        if (movimentacaoNumero[1] == 0){
                                            textViewMovimentoAtual.setText("Vazio (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#000000"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_vazio));
                                        }

                                        if (movimentacaoNumero[1] > 0 && movimentacaoNumero[1] <= 50){
                                            textViewMovimentoAtual.setText("Pouco Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#4CAF50"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_pouco_movimentado));
                                        }
                                        if (movimentacaoNumero[1] > 50 && movimentacaoNumero[1] <= 100){
                                            textViewMovimentoAtual.setText("Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#FFFB18"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_movimentado));
                                        }

                                        if (movimentacaoNumero[1] > 100){
                                            textViewMovimentoAtual.setText("Muito Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#FF0000"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar));
                                        }

                                        //IFs PARA TESTAR A MEDIA E ESTABELECER AS CORES APARENTES AO USUARIO
                                        if(media == 0){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#000000"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_vazio));
                                        }

                                        if (media > 0 && media <= 50){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#4CAF50"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_pouco_movimentado));
                                        }

                                        if (media > 50 && media <= 100){

                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#FFFB18"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_movimentado));
                                        }

                                        if (media > 100){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#FF0000"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar));
                                        }
                                        break;
                                    case "Médio":
                                        //IFs PARA TESTAR EM QUAL ESCALA SE ENCAIXA O ULTIMO FEDEBACKS
                                        if (movimentacaoNumero[1] == 0){
                                            textViewMovimentoAtual.setText("Vazio (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#000000"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_vazio));
                                        }

                                        if (movimentacaoNumero[1] > 0 && movimentacaoNumero[1] <= 100){
                                            textViewMovimentoAtual.setText("Pouco Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#4CAF50"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_pouco_movimentado));
                                        }
                                        if (movimentacaoNumero[1] > 100 && movimentacaoNumero[1] <= 150){
                                            textViewMovimentoAtual.setText("Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#FFFB18"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_movimentado));
                                        }

                                        if (movimentacaoNumero[1] > 150){
                                            textViewMovimentoAtual.setText("Muito Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#FF0000"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar));
                                        }

                                        //IFs PARA TESTAR A MEDIA E ESTABELECER AS CORES APARENTES AO USUARIO
                                        if(media == 0){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#000000"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_vazio));
                                        }

                                        if (media > 0 && media <= 100){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#4CAF50"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_pouco_movimentado));
                                        }

                                        if (media > 100 && media <= 150){

                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#FFFB18"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_movimentado));
                                        }

                                        if (media > 150){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#FF0000"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar));
                                        }
                                        break;
                                    case "Grande":
                                        //IFs PARA TESTAR EM QUAL ESCALA SE ENCAIXA O ULTIMO FEDEBACKS
                                        if (movimentacaoNumero[1] == 0){
                                            textViewMovimentoAtual.setText("Vazio (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#000000"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_vazio));
                                        }

                                        if (movimentacaoNumero[1] > 0 && movimentacaoNumero[1] <= 175){
                                            textViewMovimentoAtual.setText("Pouco Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#4CAF50"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_pouco_movimentado));
                                        }
                                        if (movimentacaoNumero[1] > 175 && movimentacaoNumero[1] <= 300){
                                            textViewMovimentoAtual.setText("Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#FFFB18"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_movimentado));
                                        }

                                        if (movimentacaoNumero[1] > 300){
                                            textViewMovimentoAtual.setText("Muito Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#FF0000"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar));
                                        }

                                        //IFs PARA TESTAR A MEDIA E ESTABELECER AS CORES APARENTES AO USUARIO
                                        if(media == 0){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#000000"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_vazio));
                                        }

                                        if (media > 0 && media <= 175){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#4CAF50"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_pouco_movimentado));
                                        }

                                        if (media > 175 && media <= 300){

                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#FFFB18"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_movimentado));
                                        }

                                        if (media > 300){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#FF0000"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar));
                                        }
                                        break;
                                }

                                break;
                            case "Mercado":
                                //TERSTANDO O TAMANHO DO ESTABELECIMENTO DE ACORDO COM OS FEEDBACKS
                                switch (movimentacao[2]){
                                    case "Pequeno":
                                        //IFs PARA TESTAR EM QUAL ESCALA SE ENCAIXA O ULTIMO FEDEBACKS
                                        if (movimentacaoNumero[1] == 0){
                                            textViewMovimentoAtual.setText("Vazio (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#000000"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_vazio));
                                        }

                                        if (movimentacaoNumero[1] > 0 && movimentacaoNumero[1] <= 100){
                                            textViewMovimentoAtual.setText("Pouco Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#4CAF50"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_pouco_movimentado));
                                        }
                                        if (movimentacaoNumero[1] > 100 && movimentacaoNumero[1] <= 300){
                                            textViewMovimentoAtual.setText("Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#FFFB18"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_movimentado));
                                        }

                                        if (movimentacaoNumero[1] > 300){
                                            textViewMovimentoAtual.setText("Muito Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#FF0000"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar));
                                        }

                                        //IFs PARA TESTAR A MEDIA E ESTABELECER AS CORES APARENTES AO USUARIO
                                        if(media == 0){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#000000"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_vazio));
                                        }

                                        if (media > 0 && media <= 100){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#4CAF50"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_pouco_movimentado));
                                        }

                                        if (media > 100 && media <= 300){

                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#FFFB18"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_movimentado));
                                        }

                                        if (media > 300){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#FF0000"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar));
                                        }
                                        break;
                                    case "Médio":
                                        //IFs PARA TESTAR EM QUAL ESCALA SE ENCAIXA O ULTIMO FEDEBACKS
                                        if (movimentacaoNumero[1] == 0){
                                            textViewMovimentoAtual.setText("Vazio (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#000000"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_vazio));
                                        }

                                        if (movimentacaoNumero[1] > 0 && movimentacaoNumero[1] <= 500){
                                            textViewMovimentoAtual.setText("Pouco Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#4CAF50"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_pouco_movimentado));
                                        }
                                        if (movimentacaoNumero[1] > 500 && movimentacaoNumero[1] <= 750){
                                            textViewMovimentoAtual.setText("Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#FFFB18"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_movimentado));
                                        }

                                        if (movimentacaoNumero[1] > 750){
                                            textViewMovimentoAtual.setText("Muito Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#FF0000"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar));
                                        }

                                        //IFs PARA TESTAR A MEDIA E ESTABELECER AS CORES APARENTES AO USUARIO
                                        if(media == 0){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#000000"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_vazio));
                                        }

                                        if (media > 0 && media <= 500){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#4CAF50"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_pouco_movimentado));
                                        }

                                        if (media > 500 && media <= 750){

                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#FFFB18"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_movimentado));
                                        }

                                        if (media > 750){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#FF0000"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar));
                                        }
                                        break;
                                    case "Grande":
                                        //IFs PARA TESTAR EM QUAL ESCALA SE ENCAIXA O ULTIMO FEDEBACKS
                                        if (movimentacaoNumero[1] == 0){
                                            textViewMovimentoAtual.setText("Vazio (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#000000"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_vazio));
                                        }

                                        if (movimentacaoNumero[1] > 0 && movimentacaoNumero[1] <= 3000){
                                            textViewMovimentoAtual.setText("Pouco Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#4CAF50"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_pouco_movimentado));
                                        }
                                        if (movimentacaoNumero[1] > 3000 && movimentacaoNumero[1] <= 7500){
                                            textViewMovimentoAtual.setText("Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#FFFB18"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_movimentado));
                                        }

                                        if (movimentacaoNumero[1] > 7500){
                                            textViewMovimentoAtual.setText("Muito Movimentado (Aprox. " + movimentacaoNumero[1] + " pess.)");
                                            textViewMovimentoAtual.setTextColor(Color.parseColor("#FF0000"));
                                            linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar));
                                        }

                                        //IFs PARA TESTAR A MEDIA E ESTABELECER AS CORES APARENTES AO USUARIO
                                        if(media == 0){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#000000"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_vazio));
                                        }

                                        if (media > 0 && media <= 3000){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#4CAF50"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_pouco_movimentado));
                                        }

                                        if (media > 3000 && media <= 7500){

                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#FFFB18"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.sek_bar_movimentado));
                                        }

                                        if (media > 7500){
                                            textViewMovientoInfo.setText("Aproximadamente " + media + " pess.");
                                            textViewMovientoInfo.setTextColor(Color.parseColor("#FF0000"));
                                            linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar));
                                        }
                                        break;
                                }

                                break;
                        }

                        //INFORMAÇÕES DO ESTABELECIMENTO
                        textViewNomeInformacao.setText(estabelecimento.getNome());
                        textViewEnderecoInformaco.setText(estabelecimento.getEndereco());
                        textViewTamanhoInformacao.setText(" " + movimentacao[2]);

                        //PRIMERIA PARTE
                        textViewHora.setText("Ultima atualização: " + movimentacao[1]);

                        //SEGUNDA PARTE
                        textViewFeedAnalisados.setText("Numero de FeedBacks Analisados: " + contador[0]);

                    }else{
                        // NENHUM FEEDBACK ENCONTRAO PARA ESTE ESTABELECIMENTO

                        //INFORMAÇÕES DO ESTABELECIMENTO
                        textViewNomeInformacao.setText(estabelecimento.getNome());
                        textViewEnderecoInformaco.setText(estabelecimento.getEndereco());
                        textViewTamanhoInformacao.setText(" ");

                        //PRIMEIRA PARTE
                        textViewMovimentoAtual.setText("Nenhum feedback");
                        textViewMovimentoAtual.setTextColor(Color.parseColor("#A1A1A1"));
                        linearLayoutSeekBarAtual.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar_nenhum));
                        textViewHora.setText("Ultima atualização: Nenhuma nas ultimas 24 horas");


                        imageViewIconEstabelecimentoInfo.setImageResource(R.drawable.ic_nenhumfeedback);

                        //SEGUNDA PARTE
                        textViewMovientoInfo.setText("Nenhum feedback");
                        textViewMovientoInfo.setTextColor(Color.parseColor("#A1A1A1"));
                        linearLayoutSeekBar.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.seek_bar_nenhum));

                    }
                }
            }
        });
        progressDialog.dismiss();
    }

    public void cadastrarUsuario(  EditText email, EditText senha, EditText nome,EditText senhaConfirmar,EditText telefone,EditText data_nasc,
                                   EditText sobrenome, View view, Context contexto, Activity activity){

        String nomeCompleto = nome.getText().toString() + " " + sobrenome.getText().toString();
        if(!nome.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !senha.getText().toString().isEmpty()
                && !senhaConfirmar.getText().toString().isEmpty() && !sobrenome.getText().toString().isEmpty() && !data_nasc.getText().toString().isEmpty()
                && !telefone.getText().toString().isEmpty())  {
            try{
                //CRIANDO FORMATAÇÃO DE DATA VALIDA
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String anoAtual = new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime());
                simpleDateFormat.setLenient(false);
                simpleDateFormat.parse(data_nasc.getText().toString());
                char data1 = data_nasc.getText().toString().charAt(6);
                char data2 = data_nasc.getText().toString().charAt(7);
                char data3 = data_nasc.getText().toString().charAt(8);
                char data4 = data_nasc.getText().toString().charAt(9);
                String valorAno = "" + data1 + data2 + data3 + data4;
                char ano1 = anoAtual.charAt(0);
                char ano2 = anoAtual.charAt(1);
                char ano3 = anoAtual.charAt(2);
                char ano4 = anoAtual.charAt(3);
                String valorAnoAtual = "" + ano1 + ano2 + ano3 + ano4;
                //CONVERTENDO A STRING PARA INT PARA PODER REALIZAR O CALCULO E COMPARACAÇÃO
                int anoFinal = Integer.parseInt(valorAno);
                int anoAtualFinal = Integer.parseInt(valorAnoAtual);
                if (anoAtualFinal - anoFinal >= 13 && anoAtualFinal - anoFinal <= 100){
                    if (senhaConfirmar.getText().toString().equals(senha.getText().toString())) {
                        String emailErrado = email.getText().toString();
                        String emailCorreto = emailErrado.toLowerCase(Locale.ROOT);
                        CadastrarUsuario_Valores cadastro = new CadastrarUsuario_Valores(nomeCompleto,
                                emailCorreto, data_nasc.getText().toString(),
                                telefone.getText().toString());

                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(), senha.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    bancoDeDados.collection("Usuarios").document(emailCorreto)
                                            .set(cadastro).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            showAlertDialogCadastro(R.layout.dialog_cadastrar_conta_usuario, contexto,activity);
                                            nome.getText().clear();
                                            sobrenome.getText().clear();
                                            email.getText().clear();
                                            data_nasc.getText().clear();
                                            telefone.getText().clear();
                                            senha.getText().clear();
                                            senhaConfirmar.getText().clear();
                                        }
                                    });

                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException e) {
                                        Toast.makeText(view.getContext(), "Digite uma senha de no mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
                                    } catch (FirebaseAuthUserCollisionException e) {
                                        Toast.makeText(view.getContext(), "Conta já cadastrada com esse email", Toast.LENGTH_SHORT).show();
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        Toast.makeText(view.getContext(), "Email inválido", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        Toast.makeText(view.getContext(), "Erro loco " + e, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

                    }else{
                        Toast.makeText(view.getContext(), "Sua senha precisa ser a mesma", Toast.LENGTH_SHORT).show();
                    }
                } else Toast.makeText(contexto, "Insira um ano valido (Necessario ser maior de 13 anos)", Toast.LENGTH_SHORT).show();

            } catch (ParseException e) {
                Toast.makeText(contexto, "Insira uma data valida", Toast.LENGTH_SHORT).show();
            }

        } else Toast.makeText(view.getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
    }

    public void loginUsuario(View view, Context context, EditText emailLogin, EditText senhaLogin, Activity activity){
        //Criando Tela de loading
        Progress(progressDialog, "Login", "Logando...", context);
        if( emailLogin.getText().toString().isEmpty() || senhaLogin.getText().toString().isEmpty()){
            Toast.makeText(view.getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
        }else{
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailLogin.getText().toString(), senhaLogin.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        emailLogin.getText().clear();
                        senhaLogin.getText().clear();
                        activity.finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception erro) {
                            Toast.makeText(view.getContext(), "Email ou senha incorretos", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }



    public void atualizarInformacoes(String email, String nomeCompleto, String dataNasc, String telefone, Context context){
        CadastrarUsuario_Valores cadastro = new CadastrarUsuario_Valores(nomeCompleto,
                email, dataNasc, telefone);
        bancoDeDados.collection("Usuarios").document(email)
                .set(cadastro).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void redfinirSenha(EditText email, View view){
        if(email.getText().toString().isEmpty()){
            Toast.makeText(view.getContext(), "Ensira ao menos o seu email para recuperar a senha!", Toast.LENGTH_SHORT).show();

        }else{
            email.getText();
            auth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(view.getContext(), "Enviamos uma MSG para o seu email com um link para você redefinir a sua senha!", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(view.getContext(), "Erro ao enviar o Email", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void deslogarApp(Activity activity, Context context) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(context, LoginActivity.class);
        activity.startActivity(intent);
    }

    public void verificaLogin(Activity activity){
        FirebaseUser usuarioLogado = FirebaseAuth.getInstance().getCurrentUser();
        if(usuarioLogado != null){

        }else {
            activity.finish();
        }
    }

    public void verificaLoginInicial(Activity activity, Context contexto){
        FirebaseUser usuarioLogado = FirebaseAuth.getInstance().getCurrentUser();
        if(usuarioLogado != null){
            Intent intent = new Intent(contexto, MainActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    //MEDOTO QUE SETA A IMAGEMDE ACORDO COM O TIPO DE ESTABELECIMENTO
    public void mudancaImagem(String tipo, BottomSheetDialog view, ImageView imageViewIconEstabelecimentoInfo){
        imageViewIconEstabelecimentoInfo = view.findViewById(R.id.iconEstabelecimentoInfo);
        switch (tipo){
            case "Mercado":
                imageViewIconEstabelecimentoInfo.setImageResource(R.drawable.ic_mercado_info);
                break;
            case "Restaurante":
                imageViewIconEstabelecimentoInfo.setImageResource(R.drawable.ic_restaurante_info);
                break;
            case "Shopping":
                imageViewIconEstabelecimentoInfo.setImageResource(R.drawable.ic__shopping);
                break;
        }
    }

    //ALERT DIALOG DE CONFIRMAR FEEDBACK
    // Metodo do custom dialog.
    public void showAlertDialogFeedback(int layoutDialog, View view, Activity activity) {
        builderDialog = new AlertDialog.Builder(view.getContext());
        View LayoutView = activity.getLayoutInflater().inflate(layoutDialog, null);
        AppCompatButton dialogButtom = LayoutView.findViewById(R.id.botao_ok_dialog);
        builderDialog.setView(LayoutView);
        alertDialog = builderDialog.create();
        alertDialog.show();

        // Quando clicado no botão de "Ok" no custom dialog
        dialogButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Desabilitando o dialog
                alertDialog.dismiss();
            }
        });
    }

    //ALERT DIALOG DE CONFIRMAR CADASTRO
    public void showAlertDialogAtualizarInfo(int layoutDialog, Context contexto, Activity activity) {
        builderDialog = new AlertDialog.Builder(contexto);
        View LayoutView = activity.getLayoutInflater().inflate(layoutDialog, null);
        AppCompatButton dialogButtom = LayoutView.findViewById(R.id.botao_ok_dialog);
        builderDialog.setView(LayoutView);
        alertDialog = builderDialog.create();
        alertDialog.show();

        // Quando clicado no botão de "Ok" no custom dialog
        dialogButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Desabilitando o dialog
                alertDialog.dismiss();

            }
        });
    }

    //ALERT DIALOG DE CONFIRMAR CADASTRO
    public void showAlertDialogCadastro(int layoutDialog, Context contexto, Activity activity) {
        builderDialog = new AlertDialog.Builder(contexto);
        View LayoutView = activity.getLayoutInflater().inflate(layoutDialog, null);
        AppCompatButton dialogButtom = LayoutView.findViewById(R.id.botao_ok_dialog);
        builderDialog.setView(LayoutView);
        alertDialog = builderDialog.create();
        alertDialog.show();

        // Quando clicado no botão de "Ok" no custom dialog
        dialogButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Desabilitando o dialog
                alertDialog.dismiss();

            }
        });
    }


    public void Progress(ProgressDialog progressDialog, String titulo, String mensagem, Context contexto) {
        progressDialog = new ProgressDialog(contexto);
        progressDialog.setTitle(titulo);
        progressDialog.setMessage(mensagem);
        progressDialog.show();
    }

}
