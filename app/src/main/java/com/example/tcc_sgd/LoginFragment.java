package com.example.tcc_sgd;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {
    //Atributos
    EditText email, senha;
    TextView redefinir_senha;
    ImageView mostrarSenha_Login;
    int mostrarSenha_Login_contador = 0;
    ProgressDialog progressDialog;
    //Banco de dados
    View view;
    BancoFirestore metodoBanco = new BancoFirestore();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_login, container, false);


        email = root.findViewById(R.id.email_login);
        senha = root.findViewById(R.id.senha_login);
        Button login = root.findViewById(R.id.login);
        redefinir_senha = root.findViewById(R.id.esqueci_senha);
        mostrarSenha_Login = root.findViewById(R.id.imageViewSenha_Login);

        redefinir_senha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                metodoBanco.redfinirSenha(email, root);
            }
        });

        mostrarSenha_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mostrarSenha_Login_contador) {
                    case 0:
                        senha.setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
                        mostrarSenha_Login.setImageResource(R.drawable.ic_senha_esconder);
                        mostrarSenha_Login_contador++;
                        break;
                    case 1:
                        senha.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                        mostrarSenha_Login.setImageResource(R.drawable.ic_senha_mostrar);
                        mostrarSenha_Login_contador--;
                        break;

                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //metodoBanco.Progress(progressDialog, "Login", "Logando, por-favor aguarde.", getContext());
                metodoBanco.loginUsuario(root, root.getContext(),email, senha, getActivity());

            }
        });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        metodoBanco.verificaLoginInicial(getActivity(), getContext());
    }



}
