package com.example.tcc_sgd;

import android.app.AlertDialog;
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

public class CadastrarFragment extends Fragment {

    AlertDialog.Builder builderDialog;
    AlertDialog alertDialog;
    ViewGroup root;
    EditText email, senha, nome, senhaConfirmar, telefone, data_nasc, sobrenome;
    ImageView mostrarSenha_Cadastrar1, mostrarSenha_Cadastrar2;
    int mostrarSenha_Cadastrar_contador = 0;


    FirebaseFirestore feed = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        root = (ViewGroup) inflater.inflate(R.layout.cadastrar_fragment, container, false);
        nome = root.findViewById(R.id.nome_cadastrar);
        email = root.findViewById(R.id.email_cadastrar);
        senha = root.findViewById(R.id.senha_cadastrar);
        telefone = root.findViewById(R.id.telefone_cadastrar);
        sobrenome = root.findViewById(R.id.sobrenome_cadastrar);
        data_nasc = root.findViewById(R.id.data_nascimento_cadastrar);
        senhaConfirmar = root.findViewById(R.id.confsenha_cadastrar);
        Button cadastrar = root.findViewById(R.id.BotaoCadastrar);
        mostrarSenha_Cadastrar1 = root.findViewById(R.id.imageViewSenha_Cadastrar1);
        mostrarSenha_Cadastrar2 = root.findViewById(R.id.imageViewSenha_Cadastrar2);


        //Criando a maskara para o campo cadastro de celular
        SimpleMaskFormatter cell = new SimpleMaskFormatter("(NN)NNNNNN-NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(telefone, cell);
        telefone.addTextChangedListener(mtw);
        //Fim da mascara do telefone

        //Criando a maskara da data
        SimpleMaskFormatter data = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher stw = new MaskTextWatcher(data_nasc, data);
        data_nasc.addTextChangedListener(stw);
        //Fim da mascara da data



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
               String nomeCompleto = nome.getText().toString() + " " + sobrenome.getText().toString();

               if(!nome.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !senha.getText().toString().isEmpty() && !senhaConfirmar.getText().toString().isEmpty())  {
                   if (senhaConfirmar.getText().toString().equals(senha.getText().toString())) {
                       CadastrarUsuario_Valores cadastro = new CadastrarUsuario_Valores(nomeCompleto,
                               email.getText().toString(), data_nasc.getText().toString(),
                               telefone.getText().toString(), senha.getText().toString());

                       FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(), senha.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if (task.isSuccessful()) {
                                   feed.collection("Usuarios").document(email.getText().toString())
                                           .set(cadastro).addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void unused) {
                                           showAlertDialog(R.layout.dialog_cadastrar_conta_usuario);
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
