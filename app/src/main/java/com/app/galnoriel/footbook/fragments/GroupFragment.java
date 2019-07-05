package com.app.galnoriel.footbook.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.galnoriel.footbook.R;
import com.app.galnoriel.footbook.adapters.MembersListAdapter;
import com.app.galnoriel.footbook.classes.Player;


import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {
    //all layout ids end with ' grf ' (for GRoup Fragment)

    RecyclerView profileRV;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_group, container, false);


               //region recycler adapter
        //setting gridLayout
        profileRV = view.findViewById(R.id.profile_rv);
        profileRV.setHasFixedSize(true);
        profileRV.setLayoutManager(new GridLayoutManager(getActivity(),2)); //getcontext?


        //list of players - make sure it's scroll

        //region list
        List<Player> players = new ArrayList<>();
        players.add(new Player(1+"","Ouriel","Modii'n"));
        players.add(new Player(2+"","Gal","Givatiim"));
        players.add(new Player(1+"","Ouriel","Modii'n"));
        players.add(new Player(2+"","Gal","Givatiim"));
        players.add(new Player(1+"","Ouriel","Modii'n"));
        players.add(new Player(2+"","Gal","Givatiim"));
        players.add(new Player(1+"","Ouriel","Modii'n"));
        players.add(new Player(2+"","Gal","Givatiim"));
        players.add(new Player(1+"","Ouriel","Modii'n"));
        players.add(new Player(2+"","Gal","Givatiim"));

//endregion
        MembersListAdapter adapter = new MembersListAdapter(getActivity(),players);
        profileRV.setAdapter(adapter);
//endregion

    //region next game frame
        //if no next game is set, nextgame layout = gone , add animation instead
//        ConstraintLayout next_game_lay = view.findViewById(R.id.next_game_lay_group_frag);
//        next_game_lay.setVisibility(View.GONE);
        //endregion

        //region add group members
        view.findViewById(R.id.groups_title_grf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroupMembers();
            }
        });
        //endregion



        return view;
    }

    private void addGroupMembers() {
        Snackbar.make(getView(),"Create add members to group from server",Snackbar.LENGTH_LONG).show();
    }
}
