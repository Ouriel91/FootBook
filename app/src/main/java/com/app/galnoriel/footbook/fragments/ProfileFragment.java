package com.app.galnoriel.footbook.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.galnoriel.footbook.MainActivity;
import com.app.galnoriel.footbook.R;
import com.app.galnoriel.footbook.adapters.GroupListAdapter;
import com.app.galnoriel.footbook.classes.CustomSharedPrefAdapter;
import com.app.galnoriel.footbook.classes.GroupPlay;
import com.app.galnoriel.footbook.classes.Player;
import com.app.galnoriel.footbook.interfaces.UpdateGroupDB;
import com.app.galnoriel.footbook.interfaces.MainToFrag;
import com.app.galnoriel.footbook.interfaces.MoveToTab;
import com.app.galnoriel.footbook.interfaces.UpdatePlayerDB;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment implements MainToFrag {
//region declarations
    private RecyclerView profileRV;
    private CustomSharedPrefAdapter sPref;
    TextView nameTV,whereFromTV,positionTV;
    Spinner pitchSP,regionSP;
    ImageView pitchIV,chatIV;
    List<GroupPlay> groupPlayList;
    public MoveToTab showTab;
    public UpdateGroupDB updateGroupDB;
    public UpdatePlayerDB updatePlayerDB;

//endregion


    @Override
    public void onDetach() {
        super.onDetach();
        if (sPref.getUserId().equals(sPref.getDisplayProfile().get_id()))
            updatePlayerDB.updatePlayerInServer(sPref.getDisplayProfile());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sPref = new CustomSharedPrefAdapter(context);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //all layout ids end with ' prf ' (for PRofile Fragment)//
        // region var assignments
        profileRV = view.findViewById(R.id.profile_rv);
        nameTV = view.findViewById(R.id.name_tv_prf);
        whereFromTV = view.findViewById(R.id.region_tv_prf);
        positionTV = view.findViewById(R.id.position_tv_prf);
        pitchSP = view.findViewById(R.id.pitch_tv_prf);
        regionSP = view.findViewById(R.id.where_play_tv_prf);
        chatIV = view.findViewById(R.id.msg_btn_prf);
        pitchIV = view.findViewById(R.id.pitch_ic_prf);
        //endregion

        //region assign views click listeners
            //add groups:
        view.findViewById(R.id.groups_title_lay_prf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewGroup();
                joinGroup();
            }
        });
            //open chat with player:
        chatIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(getView(),"Create new chat",Snackbar.LENGTH_LONG).show();
//                openChat(sPref.getUserId(),sPref.getDisplayingUid);
            }
        });
            //change pitch: (spinner)

            //change region: (spinner)


        //endregion

        //listener for player from server to set display (implemented beneath)
        ((MainActivity)getActivity()).sendToFrag = this;

        return view;
    }

    private void createNewGroup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final View dialogSignView = getLayoutInflater().inflate(R.layout.dialog_create_group,null);
        final TextView nameGroup = dialogSignView.findViewById(R.id.name_cr_gr_dia);
        final TextView wherePlayGroup = dialogSignView.findViewById(R.id.region_cr_gr_dia);
        final TextView whenPlayGroup = dialogSignView.findViewById(R.id.time_cr_gr_dia);
        builder.setView(dialogSignView);
        final AlertDialog dialog = builder.create();
        dialogSignView.findViewById(R.id.create_btn_cr_gr_dia).setOnClickListener(new View.OnClickListener() {
            //create button will create group on server and move to group frag
            @Override
            public void onClick(View v) {
                String groupName = nameGroup.getText().toString();
                String groupWhere = wherePlayGroup.getText().toString();
                String groupWhen = whenPlayGroup.getText().toString();
                String groupCreatedId =  updateGroupDB.createNewGroupInServer(new GroupPlay("",groupName,groupWhere,groupWhen));
                showTab.goToFrag(MainActivity.TAB_GROUP,groupCreatedId);
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void displayProfile(Player p) {
        nameTV.setText(p.getName());
        positionTV.setText(p.getPosition());
        whereFromTV.setText(p.getWhereFrom());
        //region recycler adapter
        //setting gridLayout
        profileRV.setHasFixedSize(true);
        profileRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        //list of groups player is member in - make sure it's scroll
        groupPlayList = new ArrayList<>();
        //will show name, avatar and when usualy play
        if (!p.get_id().isEmpty()) {
            groupPlayList.add(new GroupPlay("maksdf", "Work Friends", "GoalTime TLV", "Wed , July 10"));
            groupPlayList.add(new GroupPlay("maskdflr", "School Amigos", "Beit Dagan", "Thu , July 18"));
        }
        GroupListAdapter adapter = new GroupListAdapter(getActivity(), groupPlayList);
        adapter.setOnGroupCardClickListener(new GroupListAdapter.OnGroupCardClickListener() {
            @Override
            public void onGroupCardClick(int position, String group_id) {
                showTab.goToFrag(MainActivity.TAB_GROUP,group_id);
            }
        });
        profileRV.setAdapter(adapter);

        //endregion
    }

    private void joinGroup() {
        Snackbar.make(getView(),"Create add groups from server",Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onGetPlayerComplete(Player player) {
        displayProfile(player);
    }

    @Override
    public void onGetGroupComplete(GroupPlay group) {
//do nothing
    }


}
