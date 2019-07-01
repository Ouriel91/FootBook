package com.app.galnoriel.footbook.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.galnoriel.footbook.R;

public class ProfileFragment extends Fragment {
    //all layout ids end with ' prf ' (for PRofile Fragment)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //region add groups
        view.findViewById(R.id.groups_title_lay_prf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                joinGroup();
            }
        });
        //endregion

        return view;
    }

    private void joinGroup() {
        Snackbar.make(getView(),"Create add groups from server",Snackbar.LENGTH_LONG).show();
    }
}
