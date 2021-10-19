package com.example.tcc_sgd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;

public class BancoFirestore {
    //Atributos
    AlertDialog.Builder builderDialog;
    AlertDialog alertDialog;
    FirebaseFirestore bancoDeDados = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public void enviarFeedBack(){

    }

    public void pesquisarInformacao(){

    }

    public void cadastrarUsuario(  EditText email, EditText senha, EditText nome,EditText senhaConfirmar,EditText telefone,EditText data_nasc,
                                   EditText sobrenome, View view, Context contexto, Activity activity){
        String nomeCompleto = nome.getText().toString() + " " + sobrenome.getText().toString();

        if(!nome.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !senha.getText().toString().isEmpty()
                && !senhaConfirmar.getText().toString().isEmpty() && !sobrenome.getText().toString().isEmpty() && !data_nasc.getText().toString().isEmpty()
                && !telefone.getText().toString().isEmpty())  {
            if (senhaConfirmar.getText().toString().equals(senha.getText().toString())) {
                CadastrarUsuario_Valores cadastro = new CadastrarUsuario_Valores(nomeCompleto,
                        email.getText().toString(), data_nasc.getText().toString(),
                        telefone.getText().toString(), senha.getText().toString());

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(), senha.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            bancoDeDados.collection("Usuarios").document(email.getText().toString())
                                    .set(cadastro).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    showAlertDialog(R.layout.dialog_cadastrar_conta_usuario, contexto,activity);
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
                        ;
                    }
                });

            }else{
                Toast.makeText(view.getContext(), "Sua senha precisa ser a mesma", Toast.LENGTH_SHORT).show();
            }
        } else Toast.makeText(view.getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();


    }

    public void loginUsuario(View view, Context context, EditText emailLogin, EditText senhaLogin, Activity activity){
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

    //ALERT DIALOG DE CONFIRMAR CADASTRO
    public void showAlertDialog(int layoutDialog, Context contexto, Activity activity) {
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

}
