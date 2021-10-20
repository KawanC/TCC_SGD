package com.example.tcc_sgd;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {

    BancoFirestore metodoBanco = new BancoFirestore();
    private TextView email, nome, telefone, data_nasc;
    private Button deslogar, atualizar;
    CircleImageView imagemPerfil;
    View view;
    FirebaseFirestore feed = FirebaseFirestore.getInstance();
    FirebaseDatabase imagem = FirebaseDatabase.getInstance();
    DatabaseReference myRef = imagem.getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String usuarioID;
    AlertDialog.Builder builderDialog;
    AlertDialog alertDialog;

    String emailString;

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
        imagemPerfil = view.findViewById(R.id.imageViewPerfil);
        atualizar = view.findViewById(R.id.buttonAtualizarImagemPerfil);

        emailString = email.getText().toString();

        imagem.getReference().child("Usuario").child(nome.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
 //           ImagemPerfil img = snapshot.getValue(ImagemPerfil.class);
   //           Glide.with(view.getContext()).load(img.getImagem()).into(imagemPerfil);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        atualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        imagemPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 33);
            }
        });

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
                metodoBanco.deslogarApp(getActivity(), view.getContext());
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
        metodoBanco.verificaLogin(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData() != null){
            Uri profileUri = data.getData();
            imagemPerfil.setImageURI(profileUri);

            final StorageReference reference = storage.getReference().child("Fotos_Perfil")
                    .child(email.getText().toString());

            reference.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            myRef.child("Usuario").child("imagemPerfil").setValue(uri.toString());

                            Toast.makeText(getContext(), "Ur", Toast.LENGTH_SHORT).show();

                        }
                    });
                }


            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(view.getContext(), "Dur", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}