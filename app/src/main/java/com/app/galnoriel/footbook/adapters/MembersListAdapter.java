package com.app.galnoriel.footbook.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.galnoriel.footbook.R;
import com.app.galnoriel.footbook.classes.Player;

import java.util.List;

public class MembersListAdapter extends RecyclerView.Adapter <MembersListAdapter.MembersViewHolder> {

    public List<Player> playerList;
    private Context mContext;

    public MembersListAdapter(Context mContext, List<Player> playerList) {
        this.playerList = playerList;
        this.mContext = mContext; //context to inflate in MembersViewHolder
    }


    @NonNull
    @Override
    public MembersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        //inflate layout of card view and store it in viewHolder
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_members_list, viewGroup, false);
        MembersViewHolder holder = new MembersViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MembersViewHolder membersViewHolder, int i) {

        Player player = playerList.get(i);

        String memberName = player.getName();

        membersViewHolder.nameMemberCardTV.setText(memberName);

        //need to replace with url to firebase and load the image with glide or picasso
        membersViewHolder.playerAvatarMemberCardIV.setImageResource(R.drawable.player_avatar);
        membersViewHolder.positionMemberCardIV.setImageResource(R.drawable.goalkeeper);

    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public class MembersViewHolder extends RecyclerView.ViewHolder{

        //fields in recycler view that referenced to viewHolder
        public TextView nameMemberCardTV;
        public ImageView positionMemberCardIV, playerAvatarMemberCardIV;

        public MembersViewHolder(@NonNull View itemView) {
            super(itemView);

            nameMemberCardTV = itemView.findViewById(R.id.name_member_card);
            playerAvatarMemberCardIV = itemView.findViewById(R.id.avatar_player_member_card);
            positionMemberCardIV = itemView.findViewById(R.id.position_member_card);
        }
    }
}
