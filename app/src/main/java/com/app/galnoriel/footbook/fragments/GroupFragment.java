package com.app.galnoriel.footbook.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
    //all layout ids end with ' grf ' (for Group Fragment)

    RecyclerView groupRV;
    List<Player> playersList = new ArrayList<>();
    ArrayList<String> admins_id,member_id;
    MembersListAdapter adapter;
    boolean isAdmin = false;
    private android.support.v7.app.AlertDialog alertDialog;
    public MoveToTab showTab;
    public AccessGroupDB groupDB;
    public AccessPlayerDB playerDB;
    CustomSharedPrefAdapter spref;
    private ImageView thumbnailIV,ngPitchIV,ngDateIV,ngPriceIV,ngLocationIV;
    private TextView nameTV,wherePlayTV,whenPlayTV,ngPitchTV,ngDateTV,ngPriceTV,ngLocationTV;
    private LinearLayout addMemberLin;
    private Bitmap bitmap = null;
    private Uri uri;
    private static final int IMAGE_CAPTURE_REQUEST = 1;
    private static final int IMAGE_PICK_REQUEST = 2;


    public AccessGroupDB grfGroupDB;
    public AccessPlayerDB grfPlayerDB;

    @Override
    public void onResume() {
        super.onResume();
//        grfGroupDB.requestGroupFromServer(spref.getDisplayGroupId(),MainActivity.TAB_GROUP);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (isAdmin){
            grfGroupDB.updateGroupInServer(createGroupFromView());
        }
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
        ngPitchIV = view.findViewById(R.id.next_pitch_ic_grf);
        ngDateIV = view.findViewById(R.id.next_date_ic_grf);
        ngPriceIV = view.findViewById(R.id.price_ic_grf);
        ngLocationIV = view.findViewById(R.id.next_loc_ic_grf);
        nameTV = view.findViewById(R.id.name_tv_grf);
        wherePlayTV = view.findViewById(R.id.region_tv_grf);
        whenPlayTV = view.findViewById(R.id.pref_time_tv_grf);
        ngPitchTV = view.findViewById(R.id.next_pitch_tv_grf);
        ngDateTV = view.findViewById(R.id.next_game_date_grf);
        ngPriceTV = view.findViewById(R.id.next_price_tv_grf);
        ngLocationTV = view.findViewById(R.id.next_game_location_grf);
        //endregion
        addMemberLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addmember function!!
            }
        });
//region clicklisteners
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
                if (isAdmin) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_choices, null);

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
                            startActivityForResult(intent, IMAGE_CAPTURE_REQUEST);
                            alertDialog.dismiss();
                        }
                    });
                    unConfirmIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, IMAGE_PICK_REQUEST);
                            alertDialog.dismiss();
                        }
                    });
                }
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
                //TODO: check why run time crash here
                showTab.goToFrag(MainActivity.TAB_PROFILE,player_id);
            }
        });
        groupRV.setAdapter(adapter);
        for (Player player:playersList) {
            Log.d("Displaying PLAYER: ",player.get_id());
        }

    }
    @Override
    public void onGetPlayerComplete(Player player) {
        //add to array
        for (Player p: playersList) {
            if (p.get_id().equals(player.get_id())) { //found reoccurring
                Log.d("fetched same userId: ", player.get_id());
                if (!(p.getPosition().equals(player.getPosition()) && p.getName().equals(player.getName()))) {
                    //if true --> found update for name or pref t ime
                    //update list from server
                    playersList.remove(p);
                    playersList.add(player);
                    Log.d("updated fetch group : ", player.get_id());
                }
                return;
            }
        }
//new player on list
        playersList.add(player);
        Log.d("done fetch ", "from groupfr, plyers list is: "+playersList.toString());
        //refresh list
        refreshList();
    }

    @Override
    public void onGetGroupComplete(GroupPlay group) {
        displayGroup(group);
    }


    private void displayGroup(GroupPlay g) {

        playersList = new ArrayList<>();
        admins_id = new ArrayList<>();
        member_id = new ArrayList<>();
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
        groupRV.setLayoutManager(new GridLayoutManager(getActivity(),2));
        //list of player is member in group - make sure it's scroll
        //will show name, avatar and position_ic
        for (String admin: g.getAdmins_id()){
            Log.d("displayGroup","found admin: "+admin);
            if (spref.getUserId().equals(admin))
                isAdmin = true;
        }
        admins_id = g.getAdmins_id();
        try {
            for (String id : g.getMembers_id()) {
                Log.d("Trying to fetch group: ", id);
                grfPlayerDB.requestPlayerFromServer(id,MainActivity.TAB_GROUP);
                if (!member_id.contains(id))
                    member_id.add(id);
            }

        }catch (Exception e ) {e.printStackTrace();}

        refreshList();
    }

    private GroupPlay createGroupFromView() {
        GroupPlay defaultGroup = spref.getDisplayGroup();
        String name,wherePlay,whenPlay,ngpitch,ngprice,ngdate,ngLocation,picture;
        Game nextGame;
        ngprice = "Free";
        ngpitch = "Asphalt";
        ngLocation = "Not Set";
        picture = "";
        ngdate = "";
        boolean nextGameExist = true;
        try{name = nameTV.getText().toString();}
        catch (Exception e){e.printStackTrace();name = defaultGroup.getName();}
        try{wherePlay = wherePlayTV.getText().toString();}
        catch (Exception e){e.printStackTrace();wherePlay = defaultGroup.getWherePlay();}
        try{whenPlay = whenPlayTV.getText().toString();}
        catch (Exception e){e.printStackTrace();whenPlay = defaultGroup.getWhenPlay();}
        try{ngpitch = ngPitchTV.getText().toString();}
        catch (Exception e){e.printStackTrace();nextGameExist = false;}
        try{ngdate = ngDateTV.getText().toString();}
        catch (Exception e){e.printStackTrace();nextGameExist = false;}
        try{ngLocation = ngLocationTV.getText().toString();}
        catch (Exception e){e.printStackTrace();}
        try{ngprice = ngPriceTV.getText().toString();}
        catch (Exception e){e.printStackTrace(); }
        try {picture = thumbnailIV.getTag().toString();}
        catch (Exception e){e.printStackTrace();}
        if (nextGameExist)
            nextGame = new Game(ngpitch,ngdate,ngprice,ngLocation);
        else nextGame = null;
        Log.d("createGroupFromView", "group admin -> "+admins_id.toString()+"\tmembers -> "+member_id.toString());
        return new GroupPlay(spref.getDisplayGroupId(),name,wherePlay,whenPlay,picture,member_id,admins_id,nextGame);
    }

    @Override
    public void onClick(View v) {
        if (isAdmin) {
            Log.d("onClick override", "can edit: "+isAdmin);
            TextView tv = (TextView) v;
            String hint = "";
            Resources r = getResources(); //just because im lazy
            Drawable img = r.getDrawable(R.drawable.group_ic_tab); //set default img
            int arrayId = R.array.pitch_spinner;  //set defualt array id which the adapter will use to initialize
            boolean withSpinner = true;
            //check which textView was pressed and change the params
            switch (v.getId()) {
                case R.id.region_tv_grf:
                    img = r.getDrawable(R.drawable.where);
                    arrayId = R.array.where_play_spinner;
                    break;
                case R.id.pref_time_tv_grf:
                    withSpinner = false;
                    hint = r.getString(R.string.preffered_time);
                    img = r.getDrawable(R.drawable.clock);
                    break;
                case R.id.name_tv_grf:
                    withSpinner = false;
                    hint = r.getString(R.string.group_name);
                    img = r.getDrawable(R.drawable.team_avatar);
                    break;
                case R.id.next_price_tv_grf:
                    withSpinner = false;
                    hint = r.getString(R.string.price);
                    img = r.getDrawable(R.drawable.price);
                    break;
                case R.id.next_game_location_grf:
                    withSpinner = false;
                    hint = r.getString(R.string.next_game_location);
                    img = r.getDrawable(R.drawable.map);
                    break;
                case R.id.next_game_date_grf:
                    withSpinner = false;
                    hint = r.getString(R.string.next_game_when);
                    img = r.getDrawable(R.drawable.calendar);
                    break;
                case R.id.next_pitch_tv_grf:
                    img = r.getDrawable(R.drawable.football_field_ic);
                    arrayId = R.array.pitch_spinner;
                    break;

            }
            //make alert dialog pop dynamically
            ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(getContext(), arrayId,
                    android.R.layout.simple_spinner_item); //create adapter for the spinner in showEditDialog function
            showEditDialog(withSpinner, img, hint, v, spAdapter);
        }
    }

    private void showEditDialog(final boolean withSpinner, Drawable img, String hint, View v, final ArrayAdapter<CharSequence> spAdapter) {
        // withSpinner? if true -> show spinner instead of edit text
        // img -> this will be the image show in dialog
        // hint -> in case dialog is with edit text , this will be its hint
        // v -> the TextView pressed that called the dialog, use it to change it's text after confirmation
        //spAdapter -> relevant only in case spinner is on. this will be the value adapter for the spinner
        //make alert dialog pop dynamically
        final TextView tvFromFragment = (TextView) v;
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final View dialogEditView = getLayoutInflater().inflate(R.layout.dialog_edit_user_profile,null);
        final EditText editText = dialogEditView.findViewById(R.id.text_edit_dialog);
        final Spinner spinner = dialogEditView.findViewById(R.id.spinner_edit_dialog);
        ImageView icon = dialogEditView.findViewById(R.id.image_edit_dialog);
        builder.setView(dialogEditView);
        final AlertDialog dialog = builder.create();
        if (!hint.isEmpty()) editText.setHint(hint);
        if (withSpinner){
            spinner.setVisibility(View.VISIBLE);
            spinner.setAdapter(spAdapter);
            editText.setVisibility(View.GONE);

        }
        icon.setImageDrawable(img);
        dialogEditView.findViewById(R.id.confirm_btn_edit_dialog).setOnClickListener(new View.OnClickListener() {
            //create button will create group on server and move to group frag

            @Override
            public void onClick(View v) {
                final String newValue;
                if (withSpinner)
                    newValue = spinner.getSelectedItem().toString();
                else
                    newValue = editText.getText().toString();
                if (!newValue.isEmpty())
                    tvFromFragment.setText(newValue);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
