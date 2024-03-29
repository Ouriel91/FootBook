package com.app.galnoriel.footbook.fragments;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.galnoriel.footbook.MainActivity;
import com.app.galnoriel.footbook.R;
import com.app.galnoriel.footbook.adapters.MembersListAdapter;
import com.app.galnoriel.footbook.classes.CustomSharedPrefAdapter;
import com.app.galnoriel.footbook.classes.Game;
import com.app.galnoriel.footbook.classes.GroupPlay;
import com.app.galnoriel.footbook.classes.Player;
import com.app.galnoriel.footbook.classes.Upload;
import com.app.galnoriel.footbook.interfaces.AccessGroupDB;
import com.app.galnoriel.footbook.interfaces.AccessPlayerDB;
import com.app.galnoriel.footbook.interfaces.MainToGroupFrag;
import com.app.galnoriel.footbook.interfaces.MoveToTab;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GroupFragment extends Fragment implements MainToGroupFrag, View.OnClickListener {
    //all layout ids end with ' grf ' (for Group Fragment)

    //region declaration
    private static final int MSG_NEXT_GAME = 1;
    private static final int MSG_ANNOUNCMENT = 0;
    Resources res;
    RecyclerView groupRV;
    List<Player> playersList = new ArrayList<>();
    ArrayList<String> admins_id,member_id;
    MembersListAdapter adapter;
    boolean isAdmin = false;
    private android.support.v7.app.AlertDialog alertDialog;
    public MoveToTab showTab;
    public AccessGroupDB grfGroupDB;
    public AccessPlayerDB grfPlayerDB;
    CustomSharedPrefAdapter spref;
    private ImageView thumbnailIV,ngPitchIV,ngDateIV,ngPriceIV,ngLocationIV, groupMessengerIV;
    private TextView nameTV,wherePlayTV,whenPlayTV,ngPitchTV,ngDateTV,ngPriceTV,ngLocationTV;
    private LinearLayout addMemberLin,nextGame;
    File pictureFile;
    private String pictureFilePath ="";
    private static final int IMAGE_CAPTURE_REQUEST = 1;
    private static final int IMAGE_PICK_REQUEST = 2;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    Uri photoURI;
    private Uri imageUri;
    FirebaseMessaging messaging = FirebaseMessaging.getInstance();
    final String API_TOKEN_KEY = "AAAA5DH5UWc:APA91bGUSMXuHx9waChZZCC01IHcChrSgzcpztdi2mkacxa-LwkBRdAgHD_ECL9AkryT37Db-AuCJsuKMBxqgTEMEME6OmUEYjpMZuL1XtpsTPwhfsIhGGu15N3OM-pBiN0dESGN-L_1";
    BroadcastReceiver receiver;
    FirebaseFirestore db;

//endregion

    @Override
    public void onResume() {
        super.onResume();
        grfGroupDB.requestGroupFromServer(spref.getDisplayGroupId(),MainActivity.TAB_GROUP);
    }

    @Override
    public void onPause() {
        super.onPause();
        updateToServer(); //will work only if isadmin = true
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
        ImageView msgNextGameBTN = view.findViewById(R.id.ng_broadcast_ic_grf);
        ImageView goToNextGameBTN = view.findViewById(R.id.ng_goto_ic_grf);
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
        groupMessengerIV = view.findViewById(R.id.group_messenger_ic_grf);

        mStorageRef = FirebaseStorage.getInstance().getReference("g_uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("g_uploads");
        db = FirebaseFirestore.getInstance();
//endregion

        //region open broadcast dialog
        groupMessengerIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!member_id.contains(spref.getUserId())) {
                    return;
                }
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                View dialogView = getLayoutInflater().inflate(R.layout.message_dialog, null);
                final EditText messageET = dialogView.findViewById(R.id.message_et);
                ImageButton sendIV = dialogView.findViewById(R.id.send_iv);
                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.show();
                sendIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message = messageET.getText().toString();
                        broadcastAllMember(message,nameTV.getText().toString());
                        alertDialog.dismiss();
                    }
                });
            }
        });
        //endregion

        //region reciever for chat
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                intent.getStringExtra("message");
            }
        };
        //endregion

        IntentFilter filter = new IntentFilter("message_received");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);


        addMemberLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grfPlayerDB.openPlayerQueryDialog(false);
            }
        });
        res = getResources();

        //region clicklisteners
        nameTV.setOnClickListener(this);
        wherePlayTV.setOnClickListener(this);
        whenPlayTV.setOnClickListener(this);
        ngPitchTV.setOnClickListener(this);
        ngDateTV.setOnClickListener(this);
        ngPriceTV.setOnClickListener(this);
        ngLocationTV.setOnClickListener(this);
        msgNextGameBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAdmin) {
                    Snackbar.make(getView(), res.getString(R.string.error_not_admin), Snackbar.LENGTH_LONG).show();
                    return;
                }
                String price = ngPriceTV.getText().toString();
                String title = nameTV.getText().toString();
                String date = ngDateTV.getText().toString();
                if (date.isEmpty()){
                    Snackbar.make(getView(), res.getString(R.string.set_ng_date_error), Snackbar.LENGTH_LONG).show();
                    return;
                }
                try{Integer.valueOf(price); price+=" ₪";}
                catch (Exception e){e.printStackTrace();}
                String msg = res.getString(R.string.next_game_announcment_pt1)+
                        date+"\n"+res.getString(R.string.next_game_announcment_pt2)+
                        price;
                broadcastAllMember(msg,title);
            }
        });
        goToNextGameBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spref.setDisplayGroup(createGroupFromView());
                spref.setNextGame(createGroupFromView().getNextGame());
                showTab.goToFrag(MainActivity.TAB_GAME);
            }
        });




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
                if (isAdmin)
                    addGroupMembersDialog();
            }
        });
        //endregion

        //region image
        thumbnailIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdmin) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_choices, null);

                    TextView titleTV = dialogView.findViewById(R.id.title_tv);
                    TextView messageTV = dialogView.findViewById(R.id.message_tv);
                    ImageView camerBtn = dialogView.findViewById(R.id.dismiss_iv_about);
                    ImageView galeryBtn = dialogView.findViewById(R.id.unconfirm_iv);
                    camerBtn.setImageDrawable(res.getDrawable(R.drawable.camera));
                    galeryBtn.setImageDrawable(res.getDrawable(R.drawable.gallery));

                    builder.setView(dialogView);
                    alertDialog = builder.create();
                    alertDialog.show();

                    titleTV.setText(getResources().getString(R.string.image_change));
                    messageTV.setText(getResources().getString(R.string.image_change_option));
                    camerBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pictureFile = null;
                            try {
                                pictureFile = getPictureFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }

                            photoURI = FileProvider.getUriForFile(getActivity(),
                                    getActivity().getPackageName()+".provider",
                                    pictureFile);
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(intent, IMAGE_CAPTURE_REQUEST);


                            alertDialog.dismiss();
                        }
                    });
                    galeryBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, IMAGE_PICK_REQUEST);

                            if (mUploadTask != null && mUploadTask.isInProgress()) {
                                Snackbar.make(getView(), getResources().getString(R.string.upload_in_progress), Snackbar.LENGTH_LONG).show();
                            } else {
                                uploadFile();
                            }

                            alertDialog.dismiss();
                        }
                    });
                }
                return ;
            }
        });
        //endregion
        messaging.subscribeToTopic("FOOTBOOK");
        return view;
    }

    private void broadcastAllMember(String message, String title) {
        for (String id: member_id){
            messaging.subscribeToTopic("FOOTBOOK");
        }
        //message format to read
                            /*
                            https://fcm.googleapis.com/fcm/send
                            Content-Type:application/json
                            Authorization:key=AIzaSyZ-1u...0GBYzPu7Udno5aA
                            {
                                 "to": "/topics/foo-bar", (OR:   "condition": "'dogs' in topics || 'cats' in topics",)
                                "data": {
                                    "message": "This is a Firebase Cloud Messaging Topic Message!",
                                }
                            }
                            */
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("to","/topics/FOOTBOOK");
            jsonObject.put("data",new JSONObject().put("message",title+" :\n"+message));
            String url = "https://fcm.googleapis.com/fcm/send";
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                //for post request, we did override on this functions
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type","application/json");
                    //copy from cloud your server key
                    headers.put("Authorization","key="+API_TOKEN_KEY);
                    return headers;
                }
                @Override
                public byte[] getBody() throws AuthFailureError {
                    return jsonObject.toString().getBytes();
                }
            };
            queue.add(request);
            queue.start();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                removePlayerFromAdapter(viewHolder,  i);
            }
        };
    }

    private void removePlayerFromAdapter(final RecyclerView.ViewHolder viewHolder, int i) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        View dialogView  = getLayoutInflater().inflate(R.layout.dialog_choices, null);
        TextView titleTV = dialogView.findViewById(R.id.title_tv);
        TextView messageTV = dialogView.findViewById(R.id.message_tv);
        ImageView confirmIV = dialogView.findViewById(R.id.dismiss_iv_about);
        ImageView unConfirmIV = dialogView.findViewById(R.id.unconfirm_iv);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.show();
        titleTV.setText(getResources().getString(R.string.remove_player));
        messageTV.setText(getResources().getString(R.string.sure_remove_player));
        confirmIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change the original adapter because we remove item from
                String id_remove = playersList.get(viewHolder.getAdapterPosition()).get_id();
                member_id.remove(id_remove);
                adapter.playerList.remove(viewHolder.getAdapterPosition());
                updateToServer();
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

    private void refreshDisplay() {
        grfGroupDB.requestGroupFromServer(spref.getDisplayGroupId(),MainActivity.TAB_GROUP);
    }

    private void moveItem(int fromPos, int toPos) {
        Player player = playersList.get(fromPos);
        playersList.remove(fromPos);
        playersList.add(toPos, player);
        member_id.clear();
        for (Player p:playersList) {
            member_id.add(p.get_id());
        }
        adapter.notifyItemMoved(fromPos, toPos);

    }

    private void addGroupMembersDialog() {
        grfPlayerDB.openPlayerQueryDialog(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAPTURE_REQUEST && resultCode == getActivity().RESULT_OK){
            StorageReference reference = mStorageRef.child("g_photos").child(photoURI.getLastPathSegment());

            reference.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String photoStringLink = uri.toString();
                            spref.setGroupPathImage(photoStringLink);
                            showThumbnailImage(spref.getGroupPathImage());


                        }
                    });


                }
            });

        }
        else if (requestCode == IMAGE_PICK_REQUEST && resultCode == getActivity().RESULT_OK)
        {
            imageUri = data.getData();

        }
    }

    private void showThumbnailImage(String imageUri) {
        Glide.with(getActivity()).load(imageUri)
                .apply(new RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.team_avatar))
                .into(thumbnailIV);
        thumbnailIV.setTag(imageUri);
        thumbnailIV.setBackgroundColor(res.getColor(android.R.color.transparent));
    }

    private void uploadFile() {

        if (imageUri != null){

            final StorageReference reference = mStorageRef.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            mUploadTask = reference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Upload upload = new Upload("blabla",taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String url = downloadUri.toString();

                        spref.setGroupPathImage(url);
                        showThumbnailImage(url);

                    }
                }
            });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "FOOTBOOK_" + timeStamp+"_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,  ".jpg", storageDir);

        pictureFilePath = image.getAbsolutePath();
        return image;
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
        Log.d("done fetch ", "from groupfragment");
        //refresh list
        refreshList();
    }

    @Override
    public void onGetGroupComplete(GroupPlay group) {
        displayGroup(group);
    }

    private void displayGroup(GroupPlay g) {
        spref.setDisplayGroupId(g.getId());
        spref.setDisplayGroup(g);
        playersList = new ArrayList<>();
        admins_id = new ArrayList<>();
        member_id = new ArrayList<>();
        String name, whenPlay,wherePlay,picture;
        Game nextGame;
        try{nextGame = g.getNextGame();}
        catch (Exception e){e.printStackTrace();nextGame = null;}
        if (nextGame!=null){
            showNextGameView();
            ngPitchTV.setText(nextGame.getPitch());
            changeNextGamePitchIcon(nextGame.getPitch());
            ngPriceTV.setText(nextGame.getPrice());
            changeNextGamePriceIcon(nextGame.getPrice());
            ngDateTV.setText(nextGame.getDate());
            ngLocationTV.setText(nextGame.getLocation());
            spref.setNextGame(nextGame);
        }else
            hideNextGameView();
        //set thumbnail
        try{showThumbnailImage(g.getPicture());}
        catch (Exception e){e.printStackTrace();}
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

    private void changeNextGamePriceIcon(String price) {
        Log.d("changeNextGamePriceIcon", "cahngin to: "+price);
        if (price.toLowerCase().equals("free") || price.equals("0") || price.isEmpty()) {
            ngPriceIV.setImageDrawable(res.getDrawable(R.drawable.price_free));
            ngPriceTV.setText(res.getString(R.string.free));
        }else
            ngPriceIV.setImageDrawable(res.getDrawable(R.drawable.price));
    }

    private void changeNextGamePitchIcon(String pitch) {
        String[] type = getResources().getStringArray(R.array.pitch_spinner);
        if (pitch.equals(type[0]))
            ngPitchIV.setImageDrawable(res.getDrawable(R.drawable.asphalt));
        else if (pitch.equals(type[1]))
            ngPitchIV.setImageDrawable(res.getDrawable(R.drawable.synthetic));
        else
            ngPitchIV.setImageDrawable(res.getDrawable(R.drawable.football_field_ic));
    }

    private void hideNextGameView() {
        //hide next game frame
    }

    private void showNextGameView() {
        //hide next game frame
    }

    private GroupPlay createGroupFromView() {
        GroupPlay defaultGroup = spref.getDisplayGroup();
        String name,wherePlay,whenPlay,ngpitch,ngprice,ngdate,ngLocation,picture;
        Game nextGame;
        ngprice = "Free";
        ngpitch = "Asphalt";
        ngLocation = "Not Set";
        picture = "No Picture";
        ngdate = "When?";
        boolean nextGameExist = true;
        try{name = nameTV.getText().toString();}
        catch (Exception e){e.printStackTrace();name = defaultGroup.getName();}
        try{wherePlay = wherePlayTV.getText().toString();}
        catch (Exception e){e.printStackTrace();wherePlay = defaultGroup.getWherePlay();}
        try{whenPlay = whenPlayTV.getText().toString();}
        catch (Exception e){e.printStackTrace();whenPlay = defaultGroup.getWhenPlay();}
        try{ngpitch = ngPitchTV.getText().toString();}
        catch (Exception e){e.printStackTrace();}
        try{ngdate = ngDateTV.getText().toString();}
        catch (Exception e){e.printStackTrace();}
        try{ngLocation = ngLocationTV.getText().toString();}
        catch (Exception e){e.printStackTrace();}
        try{ngprice = ngPriceTV.getText().toString();}
        catch (Exception e){e.printStackTrace(); }
        try {picture = thumbnailIV.getTag().toString();}
        catch (Exception e){e.printStackTrace();}
        nextGame = new Game(ngpitch,ngdate,ngprice,ngLocation);
        spref.setNextGame(nextGame);
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
                if (withSpinner) {
                    newValue = spinner.getSelectedItem().toString();
//                    change icons on display
                    if (tvFromFragment.getId() == R.id.next_pitch_tv_grf)
                        changeNextGamePitchIcon(newValue);
                }
                else
                    newValue = editText.getText().toString();
                if (tvFromFragment.getId() == R.id.next_price_tv_grf)
                    changeNextGamePriceIcon(newValue);
                if (!newValue.isEmpty())
                    tvFromFragment.setText(newValue);
                dialog.dismiss();
                updateToServer();
            }
        });
        dialog.show();
    }

    @Override
    public void callUpdateGroupFromMain() {
        //main activity called for an update from view
        updateToServer();
    }

    @Override
    public void addMemberToGroup(String id) {
//        called from main activity after showing add member dialog and query
        member_id.add(id);
        updateToServer();
        refreshList();
        try{broadcastAllMember(res.getString(R.string.new_player_added_broadcasr),spref.getDisplayGroup().getName());}
        catch (Exception e){e.printStackTrace();}
        grfGroupDB.requestGroupFromServer(spref.getDisplayGroup().getId(),MainActivity.TAB_GROUP);
    }

    public void updateToServer() {
        //store current view of group in mainactivity
        //update groupo using interface to main
        if (isAdmin){
//            ((MainActivity)getActivity()).displayingGroup = ;
            grfGroupDB.setDisplayGroupInMain(createGroupFromView());
            grfGroupDB.updateGroupInServer(createGroupFromView());

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }
}
