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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.galnoriel.footbook.GlobConst;
import com.app.galnoriel.footbook.MainActivity;
import com.app.galnoriel.footbook.R;
import com.app.galnoriel.footbook.adapters.GroupListAdapter;
import com.app.galnoriel.footbook.classes.CustomSharedPrefAdapter;
import com.app.galnoriel.footbook.classes.GroupPlay;
import com.app.galnoriel.footbook.classes.Player;
import com.app.galnoriel.footbook.interfaces.FragAndMain;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment implements FragAndMain {
//region declarations
    private RecyclerView profileRV;
    private CustomSharedPrefAdapter sPref;
    TextView nameTV,whereFromTV,positionTV;
    Spinner pitchSP,regionSP;
    ImageView pitchIV,chatIV;
    List<GroupPlay> groupPlayList;

//endregion

    //all layout ids end with ' prf ' (for PRofile Fragment)//

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sPref = new CustomSharedPrefAdapter(context);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //region var assignments
        profileRV = view.findViewById(R.id.profile_rv);
        nameTV = view.findViewById(R.id.name_tv_prf);
        whereFromTV = view.findViewById(R.id.region_tv_prf);
        positionTV = view.findViewById(R.id.position_tv_prf);
        pitchSP = view.findViewById(R.id.pitch_spin_prf);
        regionSP = view.findViewById(R.id.pref_region_spin_prf);
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

        //region set display
//        db.collection(GlobConst.DB_USER_TABLE).document(sPref.getUserId())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()){
//                            DocumentSnapshot playerProfile = task.getResult();
//                            if(playerProfile.exists()){
//                                Player player = new Player(playerProfile);
//                                displayProfile(player);
//                            }
//                            else
//                                Log.e("Profile not found!!!", "searched for "+sPref.getUserId());
//                        }
//                    }
//                });
        ((MainActivity)getActivity()).sendToFrag = this;
        //endregion


        return view;
    }

    private void createNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogSignView = getLayoutInflater().inflate(R.layout.dialog_create_group,null);
        dialogSignView.findViewById(R.id.create_btn_cr_gr_dia).setOnClickListener(new View.OnClickListener() {
            //create button will create group on server and move to group frag
            @Override
            public void onClick(View v) {
//TODO:create new group
            }
        });

        builder.setView(dialogSignView);
        builder.create().show();
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
        //***important** use in basic constructor of groupPlay is not show date of
        //game , because it's null
        if (!p.get_id().isEmpty()) {
            groupPlayList.add(new GroupPlay("maksdf", "Work Friends", "GoalTime TLV", "Wed , July 10"));
            groupPlayList.add(new GroupPlay("maskdflr", "School Amigos", "Beit Dagan", "Thu , July 18"));

        }
        GroupListAdapter adapter = new GroupListAdapter(getActivity(), groupPlayList);
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
