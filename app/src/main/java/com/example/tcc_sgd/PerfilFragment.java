package com.example.tcc_sgd;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class PerfilFragment extends Fragment {

    private TextView email, nome;
    private Button deslogar;
    View view;
    FirebaseFirestore feed = FirebaseFirestore.getInstance();
    String usuarioID;

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
        deslogar = view.findViewById(R.id.buttonDeslogar);

        deslogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);

            }
        });
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        DocumentReference documentReference = feed.collection("Usuarios").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    nome = getActivity().findViewById(R.id.nome_Usuario);
                    nome.setText(documentSnapshot.getString("nome"));
                    email = getActivity().findViewById(R.id.textViewEmailUsuario);
                    email.setText(documentSnapshot.getString("email"));
                }
            }
        });

    }
}