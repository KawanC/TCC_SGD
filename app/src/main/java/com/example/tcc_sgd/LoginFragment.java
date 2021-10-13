package com.example.tcc_sgd;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginFragment extends Fragment {
View view;

    float v = 0;
    EditText email, emailTela, nomeTela, senha;
    TextView redefinir_senha;
    FirebaseAuth auth;
    ImageView mostrarSenha_Login;
    int mostrarSenha_Login_contador = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_fragment, container, false);


        email = root.findViewById(R.id.email_login);
        senha = root.findViewById(R.id.senha_login);
        Button login = root.findViewById(R.id.login);
        redefinir_senha = root.findViewById(R.id.esqueci_senha);
        auth = FirebaseAuth.getInstance();
        mostrarSenha_Login = root.findViewById(R.id.imageViewSenha_Login);


        email.setTranslationX(800);
        senha.setTranslationX(800);
        redefinir_senha.setTranslationX(800);
        login.setTranslationX(800);

        email.setAlpha(v);
        senha.setAlpha(v);
        redefinir_senha.setAlpha(v);
        login.setAlpha(v);

        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        senha.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        redefinir_senha.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        login.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();



        redefinir_senha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailLogin = email.getText().toString();

                if(emailLogin.isEmpty()){
                    Toast.makeText(view.getContext(), "Ensira ao menos o seu email para recuperar a senha!", Toast.LENGTH_SHORT).show();

                }else{
                    email.getText();
                    auth.sendPasswordResetEmail(emailLogin).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                }}
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

                String emailChecar = email.getText().toString();
                String senhaChegar = senha.getText().toString();

                if(emailChecar.isEmpty() || senhaChegar.isEmpty()){
                    Toast.makeText(view.getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();

                }else{

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(emailChecar, senhaChegar).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                                email.getText().clear();
                                senha.getText().clear();
                                getActivity().finish();
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
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser usuarioLogado = FirebaseAuth.getInstance().getCurrentUser();
        if(usuarioLogado != null){
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }
}
