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
import com.app.galnoriel.footbook.classes.GroupPlay;

import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupViewHolder> {

    private List<GroupPlay> groupPlayList;
    private Context mContext;

    public GroupListAdapter(Context mContext, List<GroupPlay> groupPlayList) {
        this.groupPlayList = groupPlayList;
        this.mContext = mContext; //to inflate cardview layout in view holder
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        //inflate the group cardView
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_list_card, viewGroup, false);
        GroupViewHolder holder = new GroupViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder groupViewHolder, int i) {

        GroupPlay groupPlay = groupPlayList.get(i);

        String nameGroup = groupPlay.getName();
        String dateGroup = groupPlay.getTime();

        groupViewHolder.nameGroupCardTV.setText(nameGroup);
        groupViewHolder.dateGroupCardTV.setText(dateGroup);

        //need to replace to url from firebase and load the image with Glide
        //that images is for checking only

        groupViewHolder.teamAvatarGroupCardIV.setImageResource(R.drawable.team_avatar);
    }

    @Override
    public int getItemCount() {
        return groupPlayList.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder{

        //view holder to cardView items
        TextView nameGroupCardTV,dateGroupCardTV;
        ImageView teamAvatarGroupCardIV;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);

            nameGroupCardTV = itemView.findViewById(R.id.name_group_card);
            teamAvatarGroupCardIV = itemView.findViewById(R.id.team_avatar_group_card);
            dateGroupCardTV = itemView.findViewById(R.id.date_group_card);
        }
    }
}
