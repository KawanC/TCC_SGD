package com.example.tcc_sgd;

import static android.content.Context.LOCATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import static com.example.tcc_sgd.R.id.barra_pesquisa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

public class MapsFragment extends Fragment {

    private View view;
    private SearchView searchView;

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

        searchView = view.findViewById(R.id.barra_pesquisa);
        // Metodo da barra de pesquisa (SearchView)
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String localizacao = searchView.getQuery().toString();
                List<Address> addressList = null;
                if (localizacao != null || !localizacao.equals("")) {
                    Geocoder geocoder = new Geocoder(view.getContext());
                    try {
                        addressList = geocoder.getFromLocationName(localizacao, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(localizacao));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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