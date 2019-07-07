package com.app.galnoriel.footbook.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.galnoriel.footbook.R;
import com.app.galnoriel.footbook.adapters.GroupListAdapter;
import com.app.galnoriel.footbook.adapters.MembersListAdapter;
import com.app.galnoriel.footbook.classes.GroupPlay;
import com.app.galnoriel.footbook.classes.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {
    //all layout ids end with ' grf ' (for GRoup Fragment)

    RecyclerView groupRV;
    List<Player> players;
    MembersListAdapter adapter;

    private ImageView thumbnailIV;
    private Bitmap bitmap = null;
    private Uri uri;
    private static final int IMAGE_CAPTURE_REQUEST = 1;
    private static final int IMAGE_PICK_REQUEST = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_group, container, false);

        thumbnailIV = view.findViewById(R.id.thumbnail_grf);

        groupRV = view.findViewById(R.id.group_rv);
        groupRV.setHasFixedSize(true);
        groupRV.setLayoutManager(new GridLayoutManager(getActivity(),2)); //getcontext?

        //region list
        //just for try
        players = new ArrayList<>();
        players.add(new Player(1+"","Ouriel","Modii'n"));
        players.add(new Player(2+"","Gal","Givatiim"));
        players.add(new Player(1+"","Ouriel","Modii'n"));
        players.add(new Player(2+"","Gal","Givatiim"));
        players.add(new Player(1+"","Ouriel","Modii'n"));
        players.add(new Player(2+"","Gal","Givatiim"));
        players.add(new Player(1+"","Ouriel","Modii'n"));
        players.add(new Player(2+"","Gal","Givatiim"));


//endregion
//region movement listeners from list adapter

        adapter = new MembersListAdapter(getActivity(),players);
        ItemTouchHelper.SimpleCallback callback = createNewCallback();
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(groupRV);
        groupRV.setAdapter(adapter);
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

        //region image
        thumbnailIV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                builder.setTitle("Image change")
                        .setMessage("Select image change option")
                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent,IMAGE_CAPTURE_REQUEST);
                            }
                        })
                        .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent,IMAGE_PICK_REQUEST);
                            }
                        })
                        .show();
                return true;
            }
        });
        //endregion

        return view;
    }

    private ItemTouchHelper.SimpleCallback createNewCallback() {

       ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN|
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Remove player")
                        .setMessage("Are you sure that you want to remove this player?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //change the original adapter because we remove item from
                                adapter.playerList.remove(viewHolder.getAdapterPosition());
                                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            }
                        })
                        .show();
            }
        };

       return callback;
    }

    private void moveItem(int fromPos, int toPos) {

        Player player = players.get(fromPos);
        players.remove(fromPos);
        players.add(toPos, player);
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
}
