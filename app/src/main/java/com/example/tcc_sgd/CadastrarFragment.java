package com.example.tcc_sgd;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

public class CadastrarFragment extends Fragment {

    AlertDialog.Builder builderDialog;
    AlertDialog alertDialog;
    ViewGroup root;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        root = (ViewGroup) inflater.inflate(R.layout.cadastrar_fragment, container, false);
        EditText email = root.findViewById(R.id.email_cadastrar);
        EditText nome = root.findViewById(R.id.nome_cadastrar);
        EditText senha = root.findViewById(R.id.senha_cadastrar);
        EditText confirmarsenha = root.findViewById(R.id.confsenha_cadastrar);
        Button cadastrar = root.findViewById(R.id.BotaoCadastrar);

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(R.layout.dialog_cadastrar_conta_usuario);
            }
        });
        return root;
    }
    public void showAlertDialog(int layoutDialog) {
        builderDialog = new AlertDialog.Builder(root.getContext());
        View LayoutView = getLayoutInflater().inflate(layoutDialog, null);
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
}
