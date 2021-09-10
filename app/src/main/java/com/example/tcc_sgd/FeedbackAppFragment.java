package com.example.tcc_sgd;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedbackAppFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedbackAppFragment extends Fragment {
    private View view;
    SeekBar seekBar;
    private TextView textViewMovimentacao;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FeedbackAppFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedbackAppFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedbackAppFragment newInstance(String param1, String param2) {
        FeedbackAppFragment fragment = new FeedbackAppFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       view = inflater.inflate(R.layout.fragment_feedback_app, container, false);

        seekBar = view.findViewById(R.id.seekBar);
        textViewMovimentacao = view.findViewById(R.id.textViewMovimentacao);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress){
                    case 0: textViewMovimentacao.setText("Vazio");
                        textViewMovimentacao.setTextColor(Color.parseColor("#000000"));
                        seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.MULTIPLY);
                        seekBar.getThumb().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);

                        break;
                    case 1: textViewMovimentacao.setText("Pouco Movimentado");
                        textViewMovimentacao.setTextColor(Color.parseColor("#90EE90"));
                        seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#90EE90"), PorterDuff.Mode.MULTIPLY);
                        seekBar.getThumb().setColorFilter(Color.parseColor("#90EE90"), PorterDuff.Mode.SRC_IN);
                        break;
                    case 2: textViewMovimentacao.setText("Movimentado");
                        textViewMovimentacao.setTextColor(Color.parseColor("#FFFF00"));
                        seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#FFFF00"), PorterDuff.Mode.MULTIPLY);
                        seekBar.getThumb().setColorFilter(Color.parseColor("#FFFF00"), PorterDuff.Mode.SRC_IN);
                        break;
                    case 3: textViewMovimentacao.setText("Cheio");
                        textViewMovimentacao.setTextColor(Color.parseColor("#FF0000"));
                        seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.MULTIPLY);
                        seekBar.getThumb().setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.SRC_IN);
                        break;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }
}