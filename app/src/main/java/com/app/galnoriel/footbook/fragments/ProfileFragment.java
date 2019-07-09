package com.app.galnoriel.footbook.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.galnoriel.footbook.MainActivity;
import com.app.galnoriel.footbook.R;
import com.app.galnoriel.footbook.classes.Upload;
import com.app.galnoriel.footbook.adapters.GroupListAdapter;
import com.app.galnoriel.footbook.classes.CustomSharedPrefAdapter;
import com.app.galnoriel.footbook.classes.GroupPlay;
import com.app.galnoriel.footbook.classes.Player;
import com.app.galnoriel.footbook.interfaces.AccessGroupDB;
import com.app.galnoriel.footbook.interfaces.MainToPlayerFrag;
import com.app.galnoriel.footbook.interfaces.MoveToTab;
import com.app.galnoriel.footbook.interfaces.AccessPlayerDB;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ProfileFragment extends Fragment implements MainToPlayerFrag, View.OnClickListener {
    //region declarations
    Resources res;
    private RecyclerView profileRV;
    private CustomSharedPrefAdapter sPref;
    TextView nameTV,whereFromTV,positionTV, pitchTV, wherePlayTV;
    ImageView pitchIV,chatIV,positionIV,wherePlayIV,whereFromIV,thumbnailIV;
    List<GroupPlay> groupPlayList = new ArrayList<>();
    GroupListAdapter adapter;
    public MoveToTab showTab;
    public AccessGroupDB prfGroupDB;
    public AccessPlayerDB prfPlayerDB;
    boolean canEdit = false;
    LinearLayout createGroupBtn;
    private android.support.v7.app.AlertDialog alertDialog;
    File pictureFile;
    private String pictureFilePath ="";
    private static final int IMAGE_CAPTURE_REQUEST = 1;
    private static final int IMAGE_PICK_REQUEST = 2;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    Uri photoURI;
    private Uri imageUri;
    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

//endregion


    @Override
    public void onResume() {
        super.onResume();
        prfPlayerDB.requestPlayerFromServer(sPref.getDisplayProfile().get_id(),MainActivity.TAB_PROFILE);
        if (sPref.getDisplayProfile().get_id().equals(sPref.getUserId()))
            canEdit = true;
        else canEdit = false;

    }

    @Override
    public void onPause() {
        super.onPause();
        updateProfileToServer(); //will work only if canEdit = true
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sPref = new CustomSharedPrefAdapter(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //all layout ids end with ' prf ' (for PRofile Fragment)//
        // region var assignments
        res=getResources();
        thumbnailIV = view.findViewById(R.id.thumbnail_prf);
        createGroupBtn = view.findViewById(R.id.groups_title_lay_prf);
        profileRV = view.findViewById(R.id.profile_rv);
        nameTV = view.findViewById(R.id.name_tv_prf);
        whereFromTV = view.findViewById(R.id.where_from_tv_prf);
        positionTV = view.findViewById(R.id.position_tv_prf);
        pitchTV = view.findViewById(R.id.pitch_tv_prf);
        wherePlayTV = view.findViewById(R.id.where_play_tv_prf);
        chatIV = view.findViewById(R.id.msg_btn_prf);
        pitchIV = view.findViewById(R.id.pitch_ic_prf);
        positionIV = view.findViewById(R.id.position_ic_prf);
        wherePlayIV = view.findViewById(R.id.pref_region_ic_prf);
        whereFromIV = view.findViewById(R.id.region_ic_prf);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        //endregion
//check for edit profile permissions:

        //only if has edit permission all listeners of edit will be set:

        nameTV.setOnClickListener(this);
        whereFromTV.setOnClickListener(this);
        wherePlayTV.setOnClickListener(this);
        positionTV.setOnClickListener(this);
        pitchTV.setOnClickListener(this);
//        showThumbnailImage(sPref.getUserPathImage());
        profileRV.setHasFixedSize(true);
        profileRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        groupPlayList = new ArrayList<>();

        //region movement listeners from list adapter
        adapter = new GroupListAdapter(getActivity(), groupPlayList);
        ItemTouchHelper.SimpleCallback callback = createNewCallback();
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(profileRV);
//        profileRV.setAdapter(adapter);
        //endregion

        //region thumbnail listener

        thumbnailIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canEdit) {
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

                    unConfirmIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, IMAGE_PICK_REQUEST);

                            if (mUploadTask != null && mUploadTask.isInProgress()) {
                                Snackbar.make(getView(), "Upload in progress", Snackbar.LENGTH_LONG).show();
                            } else {
                                uploadFile();
                            }

                            alertDialog.dismiss();
                        }
                    });
                }
            }

        });

        //endregion

        //add groups btn:
        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canEdit) {
                    //TODO: create dialog for join or create group
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                    View dialogView  = getLayoutInflater().inflate(R.layout.dialog_choices, null);
                    dialogView.findViewById(R.id.create_btns_linear).setVisibility(View.VISIBLE);
                    dialogView.findViewById(R.id.confirm_btns_linear).setVisibility(View.GONE);
                    Button createBtn = dialogView.findViewById(R.id.create_group_btn);
                    Button joinBtn = dialogView.findViewById(R.id.join_group_btn);
                    TextView massageTV = dialogView.findViewById(R.id.message_tv);
                    TextView titleTV = dialogView.findViewById(R.id.title_tv);
                    titleTV.setText(res.getString(R.string.find_group));
                    massageTV.setText(res.getString(R.string.create_or_search_group));

                    builder.setView(dialogView);
                    alertDialog = builder.create();
                    alertDialog.show();
                    createBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            createNewGroup();
                            alertDialog.dismiss();
                        }
                    });
                    joinBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            displayGroup();
                            alertDialog.dismiss();
                        }
                    });


                }
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

        //listener for player and groups from server to set display (implemented beneath)
        ((MainActivity)getActivity()).sendToPlayerFrag = this;

        return view;
    }

    private void showThumbnailImage(String imageUri) {
        Glide.with(getActivity()).load(imageUri)
                .apply(new RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.player_avatar))
                .into(thumbnailIV);
        thumbnailIV.setTag(imageUri);
        thumbnailIV.setBackgroundColor(res.getColor(android.R.color.transparent));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CAPTURE_REQUEST && resultCode == getActivity().RESULT_OK){
            StorageReference reference = mStorageRef.child("photos").child(photoURI.getLastPathSegment());
            reference.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String photoStringLink = uri.toString();
                            sPref.setUserPathImage(photoStringLink);
                            showThumbnailImage(sPref.getUserPathImage());
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

                        sPref.setUserPathImage(url);

                        showThumbnailImage(sPref.getUserPathImage());
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

    //region helper func
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

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                View dialogView  = getLayoutInflater().inflate(R.layout.dialog_choices, null);

                TextView titleTV = dialogView.findViewById(R.id.title_tv);
                TextView messageTV = dialogView.findViewById(R.id.message_tv);
                ImageView confirmIV = dialogView.findViewById(R.id.confirm_iv);
                ImageView unConfirmIV = dialogView.findViewById(R.id.unconfirm_iv);

                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.show();

                titleTV.setText(res.getString(R.string.remove_group));
                messageTV.setText(res.getString(R.string.sure_remove_group));
                confirmIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //change the original adapter because we remove item from
                        adapter.groupPlayList.remove(viewHolder.getAdapterPosition());
                        adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        updateToServer();
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
        return callback;
    }

    private void updateToServer() {
        if (canEdit)
            prfPlayerDB.updatePlayerInServer(createPlayerFromView());

    }
    //endregion

    private void moveItem(int fromPos, int toPos) {
        GroupPlay groupPlay = groupPlayList.get(fromPos);
        groupPlayList.remove(fromPos);
        groupPlayList.add(toPos, groupPlay);
        adapter.notifyItemMoved(fromPos, toPos);
    }

    private void createNewGroup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final View dialogSignView = getLayoutInflater().inflate(R.layout.dialog_create_group,null);
        final TextView nameGroup = dialogSignView.findViewById(R.id.name_cr_gr_dia);
        final TextView wherePlayGroup = dialogSignView.findViewById(R.id.region_cr_gr_dia);
        final TextView whenPlayGroup = dialogSignView.findViewById(R.id.time_cr_gr_dia);
        builder.setView(dialogSignView);
        final AlertDialog dialog = builder.create();
        dialogSignView.findViewById(R.id.create_btn_cr_gr_dia).setOnClickListener(new View.OnClickListener() {
            //create button will create group on server and move to group frag
            @Override
            public void onClick(View v) {
                String groupName = nameGroup.getText().toString();
                String groupWhere = wherePlayGroup.getText().toString();
                String groupWhen = whenPlayGroup.getText().toString();
                String groupCreatedId =  prfGroupDB.createNewGroupInServer(new GroupPlay("",groupName,groupWhere,groupWhen));
                showTab.goToFrag(MainActivity.TAB_GROUP,groupCreatedId);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void displayProfile(Player p) {
        //first, check if can edit anything on profile (owner of profile or not)
        if (p.get_id().equals(sPref.getUserId()))
            canEdit = true;
        else canEdit = false;
        Log.d("displayProfile", "can edit? "+canEdit);
        String pitch,position,wherePlay,picture;
        try{pitch = p.getPitch();}
        catch (Exception e){e.printStackTrace();pitch = "Asphalt";}
        changePitchIcon(pitch);
        try{position = p.getPosition();}
        catch (Exception e){e.printStackTrace();position = "Free Role";}
        changePositionIcon(position);
        try{wherePlay = p.getWherePlay();}
        catch (Exception e){e.printStackTrace();wherePlay = "Anywhere";}
        //handle profile picture:
        try{showThumbnailImage(p.getPicture());}
        catch (Exception e){e.printStackTrace();}
        pitchTV.setText(pitch);
        wherePlayTV.setText(wherePlay);
        nameTV.setText(p.getName());
        positionTV.setText(position);
        whereFromTV.setText(p.getWhereFrom());
        //region recycler adapter
        profileRV.setHasFixedSize(true);
        profileRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        //list of groups player is member in - make sure it's scroll
        //will show name, avatar and when usualy play
        for (String id:p.getGroups_ids()) {
            Log.d("displayProfile","Trying to fetch group: "+id);
            prfGroupDB.requestGroupFromServer(id,MainActivity.TAB_PROFILE);
        }
        Log.d("displayProfile: ", p.get_id());

        refreshGroupList();
    }

    private void changePositionIcon(String position) {
//        <item>Free Role</item>
//        <item>Defender</item>
//        <item>Midfielder</item>
//        <item>Striker</item>
//        <item>Goalkeeper</item>
        String[] values = res.getStringArray(R.array.position_spinner);
        Drawable icon = res.getDrawable(R.drawable.general_player);
        if (position.equals(values[0]))
            icon = res.getDrawable(R.drawable.general_player);
        else if (position.equals(values[1]))
            icon = res.getDrawable(R.drawable.defender);
        else if (position.equals(values[2]))
            icon = res.getDrawable(R.drawable.midfielder);
        else if (position.equals(values[3]))
            icon = res.getDrawable(R.drawable.striker);
        else if (position.equals(values[4]))
            icon = res.getDrawable(R.drawable.goalkeeper);
        positionIV.setImageDrawable(icon);

    }

    private void changePitchIcon(String pitch) {
//        <item>Asphalt</item>
//        <item>Synthetic</item>
//        <item>Grass</item>
        String[] values = res.getStringArray(R.array.pitch_spinner);
        Drawable icon = res.getDrawable(R.drawable.football_field_ic);
        if (pitch.equals(values[0]))
            icon = res.getDrawable(R.drawable.asphalt);
        if (pitch.equals(values[1]))
            icon = res.getDrawable(R.drawable.synthetic);
        if (pitch.equals(values[2]))
            icon = res.getDrawable(R.drawable.football_field_ic);
        pitchIV.setImageDrawable(icon);


    }

    //endregion
    private void displayGroup() {
//        Snackbar.make(getView(),"Create add groups from server",Snackbar.LENGTH_LONG).show();
        prfGroupDB.openGroupQueryDialog();

    }


    @Override
    public void onGetPlayerComplete(Player player) {
        Log.d("get Player called ", player.get_id());
        groupPlayList = new ArrayList<>();
        displayProfile(player);
    }

    @Override
    public void onGetGroupComplete(GroupPlay group) {
        Log.d("onGetGroupComplete", "trying to add group: "+group.getId());
        //add to array
        for (GroupPlay g:groupPlayList) {
            if (g.getId().equals(group.getId())) { //found reoccurring
                if (!(g.getWhenPlay().equals(group.getWhenPlay()) && g.getName().equals(group.getName()))) {
                    //if true --> found update for name or pref t ime
                    //update list from server
                    groupPlayList.remove(g);
                    groupPlayList.add(group);
                    Log.d("added fetch group : ", group.getId());
                }
                Log.d("fetched same groupid: ", group.getId());
                return;
            }
        }
        groupPlayList.add(group);
        //if got to this point -> grouop id wasnt found, so we need to add
        Log.d("onGetGroupComplete ", " in profile frag : group array is "+groupPlayList.toString() );
        //refresh list
        refreshGroupList();
    }

    @Override
    public void callUpdatePlayerFromMain() {
        updateProfileToServer();
    }

    private void refreshGroupList() {
        GroupListAdapter adapter = new GroupListAdapter(getActivity(), groupPlayList);
        adapter.setOnGroupCardClickListener(new GroupListAdapter.OnGroupCardClickListener() {
            @Override
            public void onGroupCardClick(int position, String group_id) {
                showTab.goToFrag(MainActivity.TAB_GROUP,group_id);
            }
        });
        profileRV.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) { //clicked on text view to edit it
        //setting declaration for calling the dialog. need the following:
        //boolean withSpinner, Drawable img, String hint, View v, ArrayAdapter<CharSequence> spAdapter
        //can see additional info in showEditDialog function
        if (canEdit) {
            String hint = "";
            Resources r = getResources(); //just because im lazy
            Drawable img = r.getDrawable(R.drawable.group_ic_tab); //set default img
            int arrayId = R.array.pitch_spinner;  //set defualt array id which the adapter will use to initialize
            boolean withSpinner = true;
            //check which textView was pressed and change the params
            switch (v.getId()) {
                case R.id.name_tv_prf: //change name:
                    withSpinner = false;
                    hint = r.getString(R.string.full_name);
                    break;
//            case R.id.pitch_ic_prf: //change pitch:
                case R.id.pitch_tv_prf:
                    img = r.getDrawable(R.drawable.football_field_ic);
                    arrayId = R.array.pitch_spinner;
                    break;
//            case R.id.position_ic_prf: //change position:
                case R.id.position_tv_prf:
                    img = r.getDrawable(R.drawable.striker);
                    arrayId = R.array.position_spinner;
                    break;
                case R.id.where_from_tv_prf: //change city (free text)
                    withSpinner = false;
                    hint = r.getString(R.string.address);
                    img = r.getDrawable(R.drawable.location);
                    break;
//            case R.id.pref_region_ic_prf: //change where user prefer playing
                case R.id.where_play_tv_prf:
                    img = r.getDrawable(R.drawable.where);
                    arrayId = R.array.where_play_spinner;
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
                    if (tvFromFragment.getId() == R.id.pitch_tv_prf)
                        changePitchIcon(newValue);
                    else if (tvFromFragment.getId() == R.id.position_tv_prf)
                        changePositionIcon(newValue);
                }
                else
                    newValue = editText.getText().toString();
                if (!newValue.isEmpty())
                    tvFromFragment.setText(newValue);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private Player createPlayerFromView(){
        return new Player(sPref.getUserId(),
                nameTV.getText().toString(), whereFromTV.getText().toString(),positionTV.getText().toString(),
                pitchTV.getText().toString(),wherePlayTV.getText().toString(),thumbnailIV.getTag().toString(),getGroupIdFromArray(groupPlayList));
    }

    private ArrayList<String> getGroupIdFromArray(List<GroupPlay> groupPlayList) {
        ArrayList<String> idArray = new ArrayList<>();
        for (GroupPlay g:groupPlayList) {
            idArray.add(g.getId());
        }
        return idArray;
    }

    private Player updateProfileToServer() {
        if (canEdit) {
            sPref.setDisplayProfile(createPlayerFromView());
            if (!(sPref.getDisplayProfile().getName().equals("Guest") || sPref.getDisplayProfile().getName().contains("Please Sign")))
                prfPlayerDB.updatePlayerInServer(createPlayerFromView());
            //will create player from view only if currently editing own profile
        }
        return createPlayerFromView();
    }


}


