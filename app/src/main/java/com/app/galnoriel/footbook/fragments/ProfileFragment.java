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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.galnoriel.footbook.R;
import com.app.galnoriel.footbook.adapters.GroupListAdapter;

import com.app.galnoriel.footbook.classes.GroupPlay;
import com.app.galnoriel.footbook.classes.Player;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.FirebaseApp;
import com.google.firebase.provider.FirebaseInitProvider;

public class ProfileFragment extends Fragment {

    private RecyclerView groupRV;

    //all layout ids end with ' prf ' (for PRofile Fragment)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
//region recycler adapter
        groupRV = view.findViewById(R.id.group_rv);
        groupRV.setHasFixedSize(true);
        groupRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<GroupPlay> groupPlayList = new ArrayList<>();

        //***important** use in basic constructor of groupPlay is not show date of
        //game , because it's null
        groupPlayList.add(new GroupPlay(1,"Hapoel petah tikva","Moshava"));
        groupPlayList.add(new GroupPlay(2,"Maccabi Haifa","Sammy ofer"));
        groupPlayList.add(new GroupPlay(1,"Hapoel petah tikva","Moshava"));
        groupPlayList.add(new GroupPlay(2,"Maccabi Haifa","Sammy ofer"));
        groupPlayList.add(new GroupPlay(1,"Hapoel petah tikva","Moshava"));
        groupPlayList.add(new GroupPlay(2,"Maccabi Haifa","Sammy ofer"));
        groupPlayList.add(new GroupPlay(1,"Hapoel petah tikva","Moshava"));
        groupPlayList.add(new GroupPlay(2,"Maccabi Haifa","Sammy ofer"));

        GroupListAdapter adapter = new GroupListAdapter(getActivity(), groupPlayList);
        groupRV.setAdapter(adapter);
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
