package com.example.tcc_sgd;

import static android.content.Context.LOCATION_SERVICE;

//import static com.example.tcc_sgd.R.id.barra_pesquisa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MapsFragment extends Fragment {

    //private SearchView searchView;
    private View view;
    private EditText editTextPesquisa;
    private FloatingActionButton BotaoFeedback, BotaoInformacao;
    private Button botaoinformacaodois;
    private SeekBar seekBar;
    private TextView textViewMovimentacao, textViewNomeInformacao, textViewEnderecoInformaco, textViewTelefoneInformacao,
            textViewTamanhoInformacao, textViewNomeFeedBack, textViewEnderecoFeedBack;
    private RadioGroup radioGroupEstabelecimento;
    Estabelecimento estabelecimento;
    private int numeroMovimentacao;
    private RadioButton radioButtonPequeno, radioButtonMedio, radioButtonGrande;
    private ImageView imageViewLocalizacao;


    //private AppCompatButton btn_sucesso;

    AlertDialog.Builder builderDialog;
    AlertDialog alertDialog;

    // A classe FusedLocationProviderCliente irá fornecer os métodos para interagir com o GPS
    private FusedLocationProviderClient servicoLocalizacao;

    // Referenciando o Mapa que será montado na tela, mas tambem podemos
    //usar os métodos para posicionar, adicionar marcador e entre outros.
    private GoogleMap mMap;

    // Variáveis para armazenar os pontos recuperados pelo GPS do celular do usuario
    private double latitude, longitude;

    // Variável para armazenar se o usuario permitiu ou nao o uso do GPS
    private boolean permitiuGPS = false;

    // Variável para armazenar o ponto retornoado pelo GPS do celular do usuario.
    Location ultimaPosicao;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);


        editTextPesquisa = view.findViewById(R.id.campo_pesquisa);
        // Inicializando o Google Places
        Places.initialize(view.getContext().getApplicationContext(), "AIzaSyDm2buo0TV0GNdmCvA0HPas-ojkn6in2jk");
        editTextPesquisa.setFocusable(false);
        editTextPesquisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicializando a lista do Google Places
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                        , Place.Field.LAT_LNG, Place.Field.NAME);
                // Criando uma intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).setCountry("BR").setTypeFilter(TypeFilter.ESTABLISHMENT).build(view.getContext());
                // Startando a activity
                startActivityForResult(intent, 100);
            }
        });
        // Chamando o serviço de localização do Andrdoid e atribuindo ao nosso objeto
        servicoLocalizacao = LocationServices.getFusedLocationProviderClient(view.getContext());

        // Verificando se o  usuário já deu permissão para o uso do GPS.
        // Quando o usuário clicar para permitir ou não o uso e acesso aos dados de localização,
        // será executado o método onRequestPermissionsResults.
        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},120);
        }else{
            permitiuGPS = true;
        }
        // Recuperação do gerenciador de localização
        LocationManager gpsHabilitado = (LocationManager)getContext().getSystemService(LOCATION_SERVICE);
        // Verificando se o GPS está habilitado.
        if(!gpsHabilitado.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //... abrindo a tela de configurações para o usuario habilitar ou nao o GPS.
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            Toast.makeText(view.getContext(), "Para este aplicativo é necessário habilitar o GPS", Toast.LENGTH_LONG).show();
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == Activity.RESULT_OK){

            // Se deu certo, inicializamos o place
            Place place = Autocomplete.getPlaceFromIntent(data);

            estabelecimento = new Estabelecimento(place.getName(), place.getAddress(),
            place.getPhoneNumber(), 0,"Teste" ,"place.getTypes()" ,"horaFeedback");
            //editTextPesquisa.setText(place.getAddress());
            // Adicionando o marcador no local pesquisado
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
        }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            // Se nao deu certo, inicializamos o status
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(view.getContext(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        // Quando o mapa estiver na tela do celular do usuario, o metodo
        // abaixo sera iniciado.
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            recuperarPosicaoAtual();
            adicionaComponentesVisuais();
        }
    };

    // Adicionando o botão para centralizar o mapa na posição atual.
    private void adicionaComponentesVisuais() {
        // Caso o objeto do mapa não existir, cancelamos o return.
        if (mMap == null) {
            return;
        }
        try {
            // Verificando se o usuário permitiu o acesso ao GPS, caso ele permitiu
            if (permitiuGPS) {
                // Adicionando o botão que quando o usuario clicar,
                // irá para a posição atual do seu aparelho/GPS.
                mMap.setMyLocationEnabled(true);
                // Habilitando o botão.
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else { // Caso o usuário não permitiu o acesso ao GPS.
                mMap.setMyLocationEnabled(false); // Removendo o botão.
                mMap.getUiSettings().setMyLocationButtonEnabled(false); // Desabilitando o botão

                // Limpando a última posição.
                ultimaPosicao  = null;

                // Pedindo a permissão do acesso ao GPS novamente
                if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},120);
                }
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // O método recuperarPosicaoAtual irá receber todas as atualizações enviadas pelo GPS do
    // celular do usuario
    private void recuperarPosicaoAtual() {
        try {
            // Testando se o usuario permitiu o uso dos dados de localização do seu dispositivo.
            if (permitiuGPS) {
                Task locationResult = servicoLocalizacao.getLastLocation();
                // Quando os dados estiverem recuperados
                locationResult.addOnCompleteListener((Activity) view.getContext(), new OnCompleteListener() {
                    @Override
                    public void onComplete(Task task) {
                        if (task.isSuccessful()) {
                            // Recuperando os dados da localização da última posição
                            ultimaPosicao = (Location) task.getResult();

                            // Caso os dados forem um valor válido
                            if(ultimaPosicao != null){
                                // Movendo a câmera para o ponto recuperado e aplicando
                                // um Zoom de 15.
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(ultimaPosicao.getLatitude(),
                                                ultimaPosicao.getLongitude()), 15));
                            }
                        } else {
                            // Exibindo um Toast se o valor do GPS não for válido
                            Toast.makeText(view.getContext().getApplicationContext(), "Não foi possível recuperar a posição.", Toast.LENGTH_LONG).show();
                            Log.e("TESTE_GPS", "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("TESTE_GPS", e.getMessage());
        }

        // Metodo do botão do feedback.
        BotaoFeedback = view.findViewById(R.id.ButtonFeedback);
        BotaoFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        view.getContext(), R.style.BottomSheetDialogTheme
                );
                View bottomSheetView = LayoutInflater.from(view.getContext().getApplicationContext())
                        .inflate(
                                R.layout.layout_bottom_sheet,
                                (LinearLayout) view.findViewById(R.id.bottomSheetContainer)
                        );

                if(estabelecimento != null) {
                    //SETANDO NOME E ENDEREÇO DOS ESTABELECIMENTOS PESQUISADOS
                    textViewNomeFeedBack = bottomSheetView.findViewById(R.id.nomeEstabelecimentoFeedback);
                    textViewEnderecoFeedBack = bottomSheetView.findViewById(R.id.enderecoEstabelecimento);

                    textViewNomeFeedBack.setText(estabelecimento.getNome());
                    textViewEnderecoFeedBack.setText(estabelecimento.getEndereco());


                    //EVENTO DA SEEKBAR PARA MOSTRAR AO USUARIO (VAZIO, POUCO MOVIMENTADO...)
                    seekBar = bottomSheetView.findViewById(R.id.seekBar); //PEGANDO ID DA SEEKBAR PELO BOTTOMSHEET
                    textViewMovimentacao = bottomSheetView.findViewById(R.id.textViewMovimento);

                    // EVENTO LISTINER QUE "ESCUTA O MOVIMENTO" DA SEEK BAR
                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                            // NECESSÁRIO ESTAR AQUI DENTRO NOVAMENTE POIS SE NÃO O LISTINER NÃO "EXERGA" O TEXTVIEW
                            textViewMovimentacao = bottomSheetView.findViewById(R.id.textViewMovimento);
                            switch (progress) {
                                //PROGRESS SÃO OS ESTAGIOS DA SEEKBAR, QUE VAI DE 0 A 3
                                case 0:
                                    textViewMovimentacao.setText("Vazio (0)");
                                    textViewMovimentacao.setTextColor(Color.parseColor("#000000")); //MUDANDO COR DO TEXTO
                                    seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.MULTIPLY); //MUDANDO COR DA SEEKBAR
                                    seekBar.getThumb().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN); //MUDANDO COR DO PONTEIRO DA SEEKBAR
                                    break;
                                case 1:
                                    textViewMovimentacao.setText("Pouco Movimentado - 50 pess");
                                    textViewMovimentacao.setTextColor(Color.parseColor("#008000"));
                                    seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#008000"), PorterDuff.Mode.MULTIPLY);
                                    seekBar.getThumb().setColorFilter(Color.parseColor("#008000"), PorterDuff.Mode.SRC_IN);
                                    break;
                                case 2:
                                    textViewMovimentacao.setText("Movimentado");
                                    textViewMovimentacao.setTextColor(Color.parseColor("#FFFF00"));
                                    seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#FFFF00"), PorterDuff.Mode.MULTIPLY);
                                    seekBar.getThumb().setColorFilter(Color.parseColor("#FFFF00"), PorterDuff.Mode.SRC_IN);
                                    break;
                                case 3:
                                    textViewMovimentacao.setText("Cheio");
                                    textViewMovimentacao.setTextColor(Color.parseColor("#FF0000"));
                                    seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.MULTIPLY);
                                    seekBar.getThumb().setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.SRC_IN);
                                    break;
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                            //ACIONADO AO CLICAR NA SEEKBAR
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            //ACIONADO AO "SOLTAR" A SEEKBAR
                        }
                    });


                    //EVENTO DO BOTÃO QUE FICA DENTRO DO BOTTOM SHET
                    bottomSheetView.findViewById(R.id.buttonEnviar).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            radioGroupEstabelecimento = bottomSheetView.findViewById(R.id.radioGroupTamanho);
                            textViewMovimentacao = bottomSheetView.findViewById(R.id.textViewMovimento);
                            int radioId = radioGroupEstabelecimento.getCheckedRadioButtonId(); //pegando id do botão selecionado

                            //ATUALIZANDO A HORA
                            String horaFeedback = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
                            estabelecimento.setHora(horaFeedback);

                            switch (radioId) {
                                case R.id.radioButtonPequeno:
                                    if (textViewMovimentacao.getText().equals("Vazio")) {
                                        numeroMovimentacao = 0;
                                    }
                                    if (textViewMovimentacao.getText().equals("Pouco Movimentado")) {
                                        numeroMovimentacao = 50;
                                    }
                                    if (textViewMovimentacao.getText().equals("Movimentado")) {
                                        numeroMovimentacao = 100;
                                    }
                                    if (textViewMovimentacao.getText().equals("Cheio")) {
                                        numeroMovimentacao = 200;
                                    }

                                    break;

                                case R.id.radioButtonMedio:
                                    if (textViewMovimentacao.getText().equals("Vazio")) {
                                        numeroMovimentacao = 0;
                                    }
                                    if (textViewMovimentacao.getText().equals("Pouco Movimentado")) {
                                        numeroMovimentacao = 75;
                                    }
                                    if (textViewMovimentacao.getText().equals("Movimentado")) {
                                        numeroMovimentacao = 150;
                                    }
                                    if (textViewMovimentacao.getText().equals("Cheio")) {
                                        numeroMovimentacao = 250;
                                    }

                                    break;

                                case R.id.radioButtonGrande:
                                    if (textViewMovimentacao.getText().equals("Vazio")) {
                                        numeroMovimentacao = 0;
                                    }
                                    if (textViewMovimentacao.getText().equals("Pouco Movimentado")) {
                                        numeroMovimentacao = 100;
                                    }
                                    if (textViewMovimentacao.getText().equals("Movimentado")) {
                                        numeroMovimentacao = 200;
                                    }
                                    if (textViewMovimentacao.getText().equals("Cheio")) {
                                        numeroMovimentacao = 400;
                                    }
                                    break;
                            }

                            radioButtonPequeno = bottomSheetView.findViewById(R.id.radioButtonPequeno);
                            radioButtonMedio = bottomSheetView.findViewById(R.id.radioButtonMedio);
                            radioButtonGrande = bottomSheetView.findViewById(R.id.radioButtonGrande);

                            if (radioButtonPequeno.isChecked() || radioButtonMedio.isChecked() || radioButtonGrande.isChecked()) {
                                //Toast.makeText(view.getContext(), "TESTE BUTTON", Toast.LENGTH_SHORT).show();
                                // CODIGO BANCO DE DADOS
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference();
                                String idRegistro = myRef.push().getKey() +  textViewNomeFeedBack.getText().toString();

                                int radioIdBanco = radioGroupEstabelecimento.getCheckedRadioButtonId();
                                switch (radioIdBanco) {
                                    case R.id.radioButtonPequeno:
                                        myRef.child(idRegistro).child("Tamanho Estabelecimento").setValue("Pequeno");
                                        break;
                                    case R.id.radioButtonMedio:
                                        myRef.child(idRegistro).child("Tamanho Estabelecimento").setValue("Medio");
                                        break;
                                    case R.id.radioButtonGrande:
                                        myRef.child(idRegistro).child("Tamanho Estabelecimento").setValue("Grande");
                                        break;
                                }
                                myRef.child(idRegistro ).child("Movimentação Estabelecimento").setValue(numeroMovimentacao);
                                myRef.child(idRegistro).child("Nome Estabelecimento").setValue(textViewNomeFeedBack.getText().toString());
                                myRef.child(idRegistro).child("Hora Feedback").setValue(estabelecimento.getHora());
                                myRef.child(idRegistro).child("Tipo Estabelecimento").setValue("TESTE" );
                                myRef.child(idRegistro).child("Tamanho Estabelecimento").setValue(estabelecimento.getTamanhoEstabelecimento());

                                myRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        showAlertDialog(R.layout.dialog_sucesso_feedback);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        showAlertDialog(R.layout.dialog_erro_feedback);
                                    }
                                });
                                bottomSheetDialog.dismiss();
                            } else {
                                Toast.makeText(view.getContext(), "Por favor selecione o tamanho do estabelecimento para completar o seu feedback.", Toast.LENGTH_LONG).show();
                            }
                        }

                        // Metodo do custom dialog.
                        public void showAlertDialog(int layoutDialog) {
                            builderDialog = new AlertDialog.Builder(view.getContext());
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
                    });
                    bottomSheetDialog.setContentView(bottomSheetView);
                    bottomSheetDialog.show();

                }else{
                    Toast.makeText(view.getContext(), "Por favor pesquise um estabelecimento antes de clicar no botão de FeedBack!", Toast.LENGTH_SHORT).show();
                }

                imageViewLocalizacao = bottomSheetView.findViewById(R.id.imagemDuvida);
                imageViewLocalizacao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlertDialogDois(R.layout.dialog_informacao_estabelecimento);
                    }
                });

            }
        });

        // Metodo do botão da informação do estabelecimento
        BotaoInformacao = view.findViewById(R.id.buttonInformacao);
        BotaoInformacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        view.getContext(), R.style.BottomSheetDialogTheme
                );
                View bottomSheetView = LayoutInflater.from(view.getContext().getApplicationContext())
                        .inflate(
                                R.layout.layout_informacao_estabelecimento,
                                (LinearLayout) view.findViewById(R.id.bottomSheetContainer)
                        );

                if(estabelecimento != null) {

                    bottomSheetDialog.setContentView(bottomSheetView);
                    bottomSheetDialog.show();

                    textViewMovimentacao = bottomSheetView.findViewById(R.id.textViewMovimentoInfo);
                    textViewNomeInformacao = bottomSheetView.findViewById(R.id.nomeEstabelecimentoInfo);
                    textViewTelefoneInformacao = bottomSheetView.findViewById(R.id.telefoneEstabelecimentoInfo);
                    textViewEnderecoInformaco = bottomSheetView.findViewById(R.id.enderecoEstabelecimentoInfo);
                    textViewTamanhoInformacao = bottomSheetView.findViewById(R.id.tamanhoEstabelecimentoInfo);

                    textViewNomeInformacao.setText(estabelecimento.getNome());
                    textViewEnderecoInformaco.setText(estabelecimento.getEndereco());
                   // textViewTelefoneInformacao.setText("Telefone: " + estabelecimento.getTelefone());
                    textViewTamanhoInformacao.setText("Tamanho do estabelecimento: " + estabelecimento.getTamanhoEstabelecimento());
                } else Toast.makeText(view.getContext(), "Por favor pesquise um estabelecimento antes de clicar no botão de informações!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAlertDialogDois(int dialog_informacao_estabelecimento) {
        builderDialog = new AlertDialog.Builder(view.getContext());
        View LayoutView = getLayoutInflater().inflate(dialog_informacao_estabelecimento, null);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

}
