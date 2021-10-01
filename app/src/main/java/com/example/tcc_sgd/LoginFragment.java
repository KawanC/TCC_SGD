package com.example.tcc_sgd;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View root = (ViewGroup) inflater.inflate(R.layout.login_fragment, container, false);

        float v = 0;
        EditText email = root.findViewById(R.id.email_login);
        EditText senha = root.findViewById(R.id.senha_login);
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
                Intent intent = new Intent(root.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });





        return root;
    }


}
