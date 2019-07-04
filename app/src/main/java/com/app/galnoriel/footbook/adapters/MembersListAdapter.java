package com.app.galnoriel.footbook.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.galnoriel.footbook.R;
import com.app.galnoriel.footbook.classes.Player;

import java.util.List;

public class MembersListAdapter extends RecyclerView.Adapter <MembersListAdapter.MembersViewHolder> {

    private List<Player> playerList;
    private Context mContext;

    public MembersListAdapter(Context mContext, List<Player> playerList) {
        this.playerList = playerList;
        this.mContext = mContext;
    }



    @NonNull
    @Override
    public MembersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.members_list_card, viewGroup, false);
        MembersViewHolder holder = new MembersViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MembersViewHolder membersViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public class MembersViewHolder extends RecyclerView.ViewHolder{

        public MembersViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
