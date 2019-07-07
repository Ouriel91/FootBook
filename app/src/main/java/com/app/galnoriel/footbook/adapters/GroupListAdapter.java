package com.app.galnoriel.footbook.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.galnoriel.footbook.R;
import com.app.galnoriel.footbook.classes.GroupPlay;
import com.app.galnoriel.footbook.interfaces.MoveToTab;

import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupViewHolder> {

    public List<GroupPlay> groupPlayList;
    private Context mContext;
    private OnGroupCardClickListener clickListener;


    public interface OnGroupCardClickListener {
        void onGroupCardClick(int position, String group_id);
    }
    public void setOnGroupCardClickListener(OnGroupCardClickListener listener){this.clickListener = listener;}

    public GroupListAdapter(Context mContext, List<GroupPlay> groupPlayList) {
        this.groupPlayList = groupPlayList;
        this.mContext = mContext; //to inflate cardview layout in view holder
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        //inflate the group cardView
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_group_list, viewGroup, false);
        GroupViewHolder holder = new GroupViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder groupViewHolder, int i) {

        GroupPlay groupPlay = groupPlayList.get(i);
        String idGroup = groupPlay.getId();
        String nameGroup = groupPlay.getName();
        String dateGroup = groupPlay.getWhenPlay();
        String avatarGroup = null;
        try {avatarGroup = groupPlay.getPicture().toString();
//            groupViewHolder.teamAvatarGroupCardIV.setImageURI(URI.create(avatarGroup));
        }
        catch (Exception e){Log.w("cant find picture", e.getMessage());
        groupViewHolder.teamAvatarGroupCardIV.setImageResource(R.drawable.team_avatar);}
        groupViewHolder.nameGroupCardTV.setText(nameGroup);
        groupViewHolder.idGRoupCardTV.setText(idGroup);
        groupViewHolder.dateGroupCardTV.setText(dateGroup);

        //need to replace to url from firebase and load the image with Glide
        //that images is for checking only


    }

    @Override
    public int getItemCount() {
        return groupPlayList.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder{

        //view holder to cardView items
        TextView nameGroupCardTV,dateGroupCardTV,idGRoupCardTV;
        ImageView teamAvatarGroupCardIV;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            idGRoupCardTV = itemView.findViewById(R.id.id_group_card);
            nameGroupCardTV = itemView.findViewById(R.id.name_group_card);
            teamAvatarGroupCardIV = itemView.findViewById(R.id.team_avatar_group_card);
            dateGroupCardTV = itemView.findViewById(R.id.date_group_card);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null)
                        Log.e("ARRIVED \n","in ONCLICK adapter");
                        clickListener.onGroupCardClick(getAdapterPosition(),idGRoupCardTV.getText().toString());
                }
            });
        }
    }
}
