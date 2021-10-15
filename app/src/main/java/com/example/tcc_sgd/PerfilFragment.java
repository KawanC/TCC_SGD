package com.example.tcc_sgd;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class PerfilFragment extends Fragment {

    private TextView email, nome, nomeTela, emailTela, telefone, data_nasc;
    private Button deslogar;
    View view;
    FirebaseFirestore feed = FirebaseFirestore.getInstance();
    String usuarioID;
    AlertDialog.Builder builderDialog;
    AlertDialog alertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_perfil, container, false);
       email = view.findViewById(R.id.textViewEmail);
       nome = view.findViewById(R.id.textViewNome);
       telefone = view.findViewById(R.id.textViewtelefone);
       data_nasc = view.findViewById(R.id.textViewDataNascimento);
        deslogar = view.findViewById(R.id.buttonDeslogar);

        try {
            usuarioID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            DocumentReference documentReference = feed.collection("Usuarios").document(usuarioID);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                    if(documentSnapshot != null){
                        try {
                            nome = view.findViewById(R.id.textViewNome);
                            email = view.findViewById(R.id.textViewEmail);
                            telefone = view.findViewById(R.id.textViewtelefone);
                            data_nasc = view.findViewById(R.id.textViewDataNascimento);
                            nome.setText(documentSnapshot.getString("nome"));
                            email.setText(documentSnapshot.getString("email"));
                            data_nasc.setText(documentSnapshot.getString("data_nasc"));
                            telefone.setText(documentSnapshot.getString("telefone"));
                        } catch (Exception e){
                            Toast.makeText(view.getContext(), "ERRO", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        } catch (Exception e){
            Toast.makeText(view.getContext(), "ERRO TEXTVIEW", Toast.LENGTH_SHORT).show();
        }

        deslogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(R.layout.dialog_confirmacao_sair);
            }
        });
        return view;

    }

    // Metodo do custom dialog.
    public void showAlertDialog(int layoutDialog) {
        builderDialog = new AlertDialog.Builder(view.getContext());
        View LayoutView = getLayoutInflater().inflate(layoutDialog, null);
        AppCompatButton dialogButtomSair = LayoutView.findViewById(R.id.botao_sair_dialog);
        AppCompatButton dialogButtonCancelar = LayoutView.findViewById(R.id.botao_cancelar_dialog);
        builderDialog.setView(LayoutView);
        alertDialog = builderDialog.create();
        alertDialog.show();

        // Quando clicado no botão de "Ok" no custom dialog
        dialogButtomSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Desabilitando o dialog
                //Deslogando da conta e indo para o Login
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });

        dialogButtonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Desabilitando o dialog
                alertDialog.dismiss();
            }
        });
    };


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser usuarioLogado = FirebaseAuth.getInstance().getCurrentUser();
        if(usuarioLogado != null){

        }else {
            getActivity().finish();
        }
    }
}