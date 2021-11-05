package com.example.tcc_sgd;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class CadastrarFragment extends Fragment {


    private ViewGroup root;
    private EditText email, senha, nome, senhaConfirmar, telefone, data_nasc, sobrenome;
    private Button limparDados;
    private ImageView mostrarSenha_Cadastrar1, mostrarSenha_Cadastrar2;
    private ProgressDialog progressDialog;
    int mostrarSenha_Cadastrar_contador = 0;

    //METODOS DO BANCO
    private BancoFirestore metodoBanco = new BancoFirestore();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        root = (ViewGroup) inflater.inflate(R.layout.cadastrar_fragment, container, false);

        //Criando Tela de loading
        progressDialog = new ProgressDialog(root.getContext());
        progressDialog.setMessage("Estamos cadastrando sua conta, por favor aguarde...");

        //Pegando Ids dos componentes da tela
        nome = root.findViewById(R.id.nome_cadastrar);
        email = root.findViewById(R.id.email_cadastrar);
        senha = root.findViewById(R.id.senha_cadastrar);
        telefone = root.findViewById(R.id.telefone_cadastrar);
        limparDados = root.findViewById(R.id.limparDadosCadastro);
        sobrenome = root.findViewById(R.id.sobrenome_cadastrar);
        data_nasc = root.findViewById(R.id.data_nascimento_cadastrar);
        senhaConfirmar = root.findViewById(R.id.confsenha_cadastrar);
        Button cadastrar = root.findViewById(R.id.BotaoCadastrar);
        mostrarSenha_Cadastrar1 = root.findViewById(R.id.imageViewSenha_Cadastrar1);
        mostrarSenha_Cadastrar2 = root.findViewById(R.id.imageViewSenha_Cadastrar2);

        //Criando a maskara para o campo cadastro de celular
        SimpleMaskFormatter cell = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(telefone, cell);
        telefone.addTextChangedListener(mtw);
        //Fim da mascara do telefone

        //Criando a maskara da data
        SimpleMaskFormatter data = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher stw = new MaskTextWatcher(data_nasc, data);
        data_nasc.addTextChangedListener(stw);
        //Fim da mascara da data

        //Criando a maskara do nome
        SimpleMaskFormatter nomeP = new SimpleMaskFormatter("LLLLLLLLLL");
        MaskTextWatcher ttw = new MaskTextWatcher(nome, nomeP);
        nome.addTextChangedListener(ttw);
        //Fim da maskara do nome

        //Criando a maskara do sobrenome
        SimpleMaskFormatter nomeS = new SimpleMaskFormatter("LLLLLLLLLL");
        MaskTextWatcher rtw = new MaskTextWatcher(sobrenome, nomeS);
        sobrenome.addTextChangedListener(rtw);
        //Fim da maskara do sobrenome

        //Metodo para mostrar a senha para o usuario
        mostrarSenha_Cadastrar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mostrarSenha_Cadastrar_contador) {
                    case 0:
                        senha.setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
                        mostrarSenha_Cadastrar1.setImageResource(R.drawable.ic_senha_mostrar);
                        mostrarSenha_Cadastrar_contador++;
                        break;
                    case 1:
                        senha.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                        mostrarSenha_Cadastrar1.setImageResource(R.drawable.ic_senha_esconder);
                        mostrarSenha_Cadastrar_contador--;
                        break;
                }
            }
        });
        //Metodo para mostrar a senha ocnfirmada para o usuario
        mostrarSenha_Cadastrar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mostrarSenha_Cadastrar_contador){
                    case 0:
                    senhaConfirmar.setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
                    mostrarSenha_Cadastrar2.setImageResource(R.drawable.ic_senha_mostrar);
                    mostrarSenha_Cadastrar_contador++;

                    break;
                    case 1:
                        senhaConfirmar.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                        mostrarSenha_Cadastrar2.setImageResource(R.drawable.ic_senha_esconder);
                        mostrarSenha_Cadastrar_contador--;
                        break;
                }
            }
        });

      cadastrar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                progressDialog.show();
                metodoBanco.cadastrarUsuario(email, senha, nome, senhaConfirmar, telefone, data_nasc, sobrenome, root, root.getContext(), getActivity());
                progressDialog.dismiss();
            }
       });

      limparDados.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              metodoBanco.limparDadosCadastro(email, senha, senhaConfirmar, nome, sobrenome, telefone, data_nasc);
          }
      });
        return root;


    }

}
