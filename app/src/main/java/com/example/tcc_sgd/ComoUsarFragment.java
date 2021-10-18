package com.example.tcc_sgd;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class ComoUsarFragment extends Fragment {

    ViewPager viewPager;
    Adapter adapter;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    List<Model> models;
    View view;
    Button comecar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_como_usar, container, false);

        comecar = view.findViewById(R.id.btnNext);
        models = new ArrayList<>();
        models.add(new Model(R.drawable.sgd_sem_fundo_nome, "Como usar o Aplicativo SGD?", "Siga os passos para aprender."));
        models.add(new Model(R.drawable.sgd_sem_fundo_nome, "Passo 1:", "Pesquisei por um estabelecimento no campo de busca"));
        models.add(new Model(R.drawable.sgd_sem_fundo_nome, "Passo 2:", "Envie um feedback sobre o estabelecimento pesquisado"));
        models.add(new Model(R.drawable.sgd_sem_fundo_nome, "Passo 3:", "Veja as informações do estabelecimento pesquisado através de outros feedbacks enviados."));
        adapter = new Adapter(models, view.getContext());
        viewPager = view.findViewById(R.id.viewPagerComoUsar);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130,0,130,0);

        Integer[] colors_temp = {
                getResources().getColor(R.color.colorItem1),
                getResources().getColor(R.color.colorItem2),
                getResources().getColor(R.color.colorItem3),
                getResources().getColor(R.color.colorItem4)
        };

        colors = colors_temp;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position < (adapter.getCount() -1) && position < (colors.length -1)){
                    viewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1]));
                }else{
                    viewPager.setBackgroundColor(colors[colors.length -1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        comecar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    return view;
    }
}