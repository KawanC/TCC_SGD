package com.example.tcc_sgd;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
View view;

    float v = 0;
    EditText email;
    EditText senha;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_fragment, container, false);


        email = root.findViewById(R.id.email_login);
        senha = root.findViewById(R.id.senha_login);
        TextView esqueciSenha = root.findViewById(R.id.esqueci_senha);
        Button login = root.findViewById(R.id.login);


        email.setTranslationX(800);
        senha.setTranslationX(800);
        esqueciSenha.setTranslationX(800);
        login.setTranslationX(800);

        email.setAlpha(v);
        senha.setAlpha(v);
        esqueciSenha.setAlpha(v);
        login.setAlpha(v);

        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        senha.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        esqueciSenha.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        login.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();


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
                                Intent intent = new Intent(view.getContext(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception erro) {
                                    Toast.makeText(view.getContext(), "Erro ao logar" + erro, Toast.LENGTH_SHORT).show();
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
        }
    }
}
