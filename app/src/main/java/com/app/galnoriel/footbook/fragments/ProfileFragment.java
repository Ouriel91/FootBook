package com.app.galnoriel.footbook.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.galnoriel.footbook.R;
import com.app.galnoriel.footbook.adapters.MembersListAdapter;
import com.app.galnoriel.footbook.classes.Player;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.FirebaseApp;
import com.google.firebase.provider.FirebaseInitProvider;

public class ProfileFragment extends Fragment {

    private RecyclerView profileRV;

    //all layout ids end with ' prf ' (for PRofile Fragment)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //region recycler adapter
        //setting gridLayout
        profileRV = view.findViewById(R.id.profile_rv);
        profileRV.setHasFixedSize(true);
        profileRV.setLayoutManager(new GridLayoutManager(getActivity(),2)); //getcontext?


        //list of players - make sure it's scroll

        //region list
        List<Player> players = new ArrayList<>();
        players.add(new Player(1,"Ouriel","Modii'n"));
        players.add(new Player(2,"Gal","Givatiim"));
        players.add(new Player(1,"Ouriel","Modii'n"));
        players.add(new Player(2,"Gal","Givatiim"));
        players.add(new Player(1,"Ouriel","Modii'n"));
        players.add(new Player(2,"Gal","Givatiim"));
        players.add(new Player(1,"Ouriel","Modii'n"));
        players.add(new Player(2,"Gal","Givatiim"));
        players.add(new Player(1,"Ouriel","Modii'n"));
        players.add(new Player(2,"Gal","Givatiim"));

//endregion
        MembersListAdapter adapter = new MembersListAdapter(getActivity(),players);
        profileRV.setAdapter(adapter);
//endregion
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
