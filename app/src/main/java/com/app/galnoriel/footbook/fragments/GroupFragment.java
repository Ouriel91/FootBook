package com.app.galnoriel.footbook.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.galnoriel.footbook.MainActivity;
import com.app.galnoriel.footbook.R;
import com.app.galnoriel.footbook.adapters.MembersListAdapter;
import com.app.galnoriel.footbook.classes.CustomSharedPrefAdapter;
import com.app.galnoriel.footbook.classes.Game;
import com.app.galnoriel.footbook.classes.GroupPlay;
import com.app.galnoriel.footbook.classes.Player;
import com.app.galnoriel.footbook.interfaces.AccessGroupDB;
import com.app.galnoriel.footbook.interfaces.AccessPlayerDB;
import com.app.galnoriel.footbook.interfaces.MainToGroupFrag;
import com.app.galnoriel.footbook.interfaces.MoveToTab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment implements MainToGroupFrag, View.OnClickListener {
    //all layout ids end with ' grf ' (for GRoup Fragment)

    RecyclerView groupRV;
    List<Player> playersList = new ArrayList<>();
    ArrayList<String> admins_id,member_id;
    MembersListAdapter adapter;
    boolean isAdmin = false;

    private android.support.v7.app.AlertDialog alertDialog;
    public MoveToTab grfShowTab;
    public AccessGroupDB grfGroupDB;
    public AccessPlayerDB grfPlayerDB;
    CustomSharedPrefAdapter spref;
    private ImageView thumbnailIV,ngPitchIV,ngDateIV,ngPriceIV,ngLocationIV;
    private TextView nameTV,wherePlayTV,whenPlayTV,ngPitchTV,ngDateTV,ngPriceTV,ngLocationTV;
    private LinearLayout addMemberLin;
    private Bitmap bitmap = null;
    private Uri uri;
    private static final int IMAGE_CAPTURE_REQUEST = 1;
    private static final int IMAGE_PICK_REQUEST = 2;

    @Override
    public void onResume() {
        super.onResume();
        //request group again
    }

    @Override
    public void onPause() {
        super.onPause();
//        save to server
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        spref = new CustomSharedPrefAdapter(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_group, container, false);
        ((MainActivity)getActivity()).sendToGroupFrag = this;
    //region view assignments
        thumbnailIV = view.findViewById(R.id.thumbnail_grf);
        groupRV = view.findViewById(R.id.group_rv);
        groupRV.setHasFixedSize(true);
        groupRV.setLayoutManager(new GridLayoutManager(getActivity(),2)); //getcontext?
        addMemberLin = view.findViewById(R.id.members_title_lin_grf);
        thumbnailIV = view.findViewById(R.id.thumbnail_grf);
        ngPitchIV = view.findViewById(R.id.pitch_ic_grf);
        ngDateIV = view.findViewById(R.id.next_date_ic_grf);
        ngPriceIV = view.findViewById(R.id.price_ic_grf);
        ngLocationIV = view.findViewById(R.id.next_loc_ic_grf);
        nameTV = view.findViewById(R.id.name_tv_grf);
        wherePlayTV = view.findViewById(R.id.region_tv_grf);
        whenPlayTV = view.findViewById(R.id.pref_time_tv_grf);
        ngPitchTV = view.findViewById(R.id.pitch_tv_prf);
        ngDateTV = view.findViewById(R.id.next_game_date_grf);
        ngPriceTV = view.findViewById(R.id.price_tv_grf);
        ngLocationTV = view.findViewById(R.id.next_game_location_grf);
        //endregion
        addMemberLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addmember function!!
            }
        });
        if (isAdmin) {
//        thumbnailIV.setOnClickListener(this);
//        ngPitchIV.setOnClickListener(this);
//        ngDateIV.setOnClickListener(this);
//        ngPriceIV.setOnClickListener(this);
//        ngLocationIV.setOnClickListener(this);
            nameTV.setOnClickListener(this);
            wherePlayTV.setOnClickListener(this);
            whenPlayTV.setOnClickListener(this);
            ngPitchTV.setOnClickListener(this);
            ngDateTV.setOnClickListener(this);
            ngPriceTV.setOnClickListener(this);
            ngLocationTV.setOnClickListener(this);
        }
        //region clicklisteners

        //endregion


        //region list
//        playersList.add(new Player(1+"","Ouriel","Modii'n"));
//        playersList.add(new Player(2+"","Gal","Givatiim"));
//        playersList.add(new Player(1+"","Ouriel","Modii'n"));
//        playersList.add(new Player(2+"","Gal","Givatiim"));
//        playersList.add(new Player(1+"","Ouriel","Modii'n"));
//        playersList.add(new Player(2+"","Gal","Givatiim"));
//        playersList.add(new Player(1+"","Ouriel","Modii'n"));
//        playersList.add(new Player(2+"","Gal","Givatiim"));


//endregion

        //region touchHelper
        ItemTouchHelper.SimpleCallback callback = createMemberListCallBack();
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(groupRV);
        //endregion

        //region next game frame
        //if no next game is set, nextgame layout = gone , add animation instead
//        FrameLayout frameLayout = view.findViewById(R.id.next_game_frame_grf);
//        frameLayout.removeAllViewsInLayout();
        //endregion

        //region add group members
        view.findViewById(R.id.groups_title_grf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroupMembers();
            }
        });
        //endregion

        //region image
        thumbnailIV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                View dialogView  = getLayoutInflater().inflate(R.layout.dialog_choices, null);

                TextView titleTV = dialogView.findViewById(R.id.title_tv);
                TextView messageTV = dialogView.findViewById(R.id.message_tv);
                ImageView confirmIV = dialogView.findViewById(R.id.confirm_iv);
                ImageView unConfirmIV = dialogView.findViewById(R.id.unconfirm_iv);

                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.show();

                titleTV.setText("Image change");
                messageTV.setText("Select image change option");
                confirmIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent,IMAGE_CAPTURE_REQUEST);
                        alertDialog.dismiss();
                    }
                });
                unConfirmIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,IMAGE_PICK_REQUEST);
                        alertDialog.dismiss();
                    }
                });

                return true;
            }
        });
        //endregion

        return view;
    }

    private ItemTouchHelper.SimpleCallback createMemberListCallBack() {
        return new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN|
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                final int fromPos = viewHolder.getAdapterPosition();
                final int toPos = target.getAdapterPosition();
                //not changing the original list, only the view
                moveItem(fromPos,toPos);
                return true;
            }
            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                View dialogView  = getLayoutInflater().inflate(R.layout.dialog_choices, null);

                TextView titleTV = dialogView.findViewById(R.id.title_tv);
                TextView messageTV = dialogView.findViewById(R.id.message_tv);
                ImageView confirmIV = dialogView.findViewById(R.id.confirm_iv);
                ImageView unConfirmIV = dialogView.findViewById(R.id.unconfirm_iv);

                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.show();

                titleTV.setText("Remove player ?");
                messageTV.setText("Are you sure that you want to remove this player?");
                confirmIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //change the original adapter because we remove item from
                        adapter.playerList.remove(viewHolder.getAdapterPosition());
                        adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        alertDialog.dismiss();
                    }
                });
                unConfirmIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        alertDialog.dismiss();
                    }
                });
            }
        };
    }

    private void moveItem(int fromPos, int toPos) {
        Player player = playersList.get(fromPos);
        playersList.remove(fromPos);
        playersList.add(toPos, player);
        adapter.notifyItemMoved(fromPos, toPos);

    }

    private void addGroupMembers() {
        Snackbar.make(getView(),"Create add members to group from server",Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CAPTURE_REQUEST && resultCode == getActivity().RESULT_OK){

            bitmap = (Bitmap) data.getExtras().get("data");
            thumbnailIV.setImageBitmap(bitmap);
        }
        else if (requestCode == IMAGE_PICK_REQUEST && resultCode == getActivity().RESULT_OK){

            uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri);
                thumbnailIV.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void refreshList() {
        adapter = new MembersListAdapter(getActivity(), playersList);
        adapter.setOnPlayerCardClickListener(new MembersListAdapter.OnPlayerCardClickListener() {
            @Override
            public void onPlayerCardClick(int position, String player_id) {
                Log.d("Clicked on id: ",player_id);
                grfShowTab.goToFrag(MainActivity.TAB_PROFILE,player_id);
            }
        });
        groupRV.setAdapter(adapter);
        for (Player player:playersList) {
            Log.d("Displayin PLAYER: ",player.get_id());
        }

    }
    @Override
    public void onGetPlayerComplete(Player player) {
        //add to array
        for (Player p: playersList) {
            if (p.get_id().equals(player.get_id())) { //found reoccurring
                if (!(p.getPosition().equals(player.getPosition()) && p.getName().equals(player.getName()))) {
                    //if true --> found update for name or pref t ime
                    //update list from server
                    playersList.remove(p);
                    playersList.add(player);
                    Log.d("added fetch group : ", player.get_id());
                }
                Log.d("fetched same userId: ",
                        player.get_id());

            }
        }
        //if got to this point -> grouop id wasnt found, so we need to add
        playersList.add(player);
        Log.d("done fetch from groupfr", ":  *********");
        //refresh list
        refreshList();
    }

    @Override
    public void onGetGroupComplete(GroupPlay group) {
        displayGroup(group);
    }


    private void displayGroup(GroupPlay g) {
        String name, whenPlay,wherePlay,picture;
        Game nextGame;
        try{nextGame = g.getNextGame();}
        catch (Exception e){e.printStackTrace();nextGame = null;}
        if (nextGame!=null){
            ngPitchTV.setText(nextGame.getPitch());
            ngDateTV.setText(nextGame.getDate());
//            ngLocationTV.setText(nextGame.getLocation().toString());
            ngPriceTV.setText(nextGame.getPrice());
            if (nextGame.getPrice().equals("Free"))
                ngPriceIV.setImageDrawable(getResources().getDrawable(R.drawable.price_free));
        }
        //set thumbnail
        try{picture = g.getPicture();}
        catch (Exception e){e.printStackTrace();picture = "default";}

        whenPlayTV.setText(g.getWhenPlay());
        wherePlayTV.setText(g.getWherePlay());
        nameTV.setText(g.getName());

        //region recycler adapter
        //setting gridLayout
        groupRV.setHasFixedSize(true);
        groupRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        //list of player is member in group - make sure it's scroll
        //will show name, avatar and position_ic
        for (String admin: g.getAdmins_id()){
            if (spref.getUserId().equals(admin))
                isAdmin = true;
        }
        try {
            for (String id : g.getMembers_id()) {
                Log.d("Trying to fetch group: ", id);
                grfPlayerDB.requestPlayerFromServer(id,MainActivity.TAB_GROUP);
            }
        }catch (Exception e ) {e.printStackTrace();}
        refreshList();
    }

    @Override
    public void onClick(View v) {
        TextView tv = (TextView) v;
        switch (v.getId()){
             case R.id.region_tv_grf:

                 break;
             case R.id.pref_time_tv_grf:
                 break;
             case R.id.pitch_tv_prf:
                 break;
             case R.id.next_game_date_grf:
                 break;
             case R.id.price_tv_grf:
                 break;
             case R.id.next_game_location_grf:
                 break;

        }
    }
}
