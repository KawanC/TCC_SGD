package com.example.tcc_sgd;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LoginAdapter extends FragmentPagerAdapter {

      private Context context;
      int total;

    public LoginAdapter(@NonNull FragmentManager fm, Context context, int total) {
        super(fm);
        this.context = context;
        this.total = total;
    }

    @Override
    public int getCount() {
        return total;
    }

    public Fragment getItem(int posicao){
        switch (posicao){
            case 0:
                LoginFragment loginFragment = new LoginFragment();
                return loginFragment;

            case 1:
                CadastrarFragment cadastrarFragment = new CadastrarFragment();
                return cadastrarFragment;

            default:
                return null;

        }
    }

}
