package com.app.galnoriel.footbook.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.app.galnoriel.footbook.MainActivity;
import com.app.galnoriel.footbook.R;
import com.app.galnoriel.footbook.Services.TimerService;
import com.app.galnoriel.footbook.classes.CustomSharedPrefAdapter;
import com.app.galnoriel.footbook.classes.Game;
import com.app.galnoriel.footbook.interfaces.GetGameFromMain;
import com.app.galnoriel.footbook.interfaces.MainToGameFrag;

import java.util.Locale;

public class GameFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener, MainToGameFrag {
    //region global declaration
    private View view;
    private Game nextGame;
    private ToggleButton start_timer;
    private CountDownTimer mCountDownTimer;
    private TextView dateTV,locationTV,pitchTV,priceTV;
    private ImageView pitchIV,priceIV;
    private long startTimeInMillis;
    private long leftTimeInMillis = 0;
    private long endTime;
    private boolean isTimerRunning;
    private Resources res;
    public GetGameFromMain nextGameFromMain;
    private CustomSharedPrefAdapter sPref;
    private TextView timer_tv;
    private int time = 0;
    public MainToGameFrag getFromMain;

//endregion


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sPref = new CustomSharedPrefAdapter(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        displayGame();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_game, container, false);
        ((MainActivity)getActivity()).sendGametoFrag = this;
        res = getResources();
//        nextGAmeReciever = this ;

        //region score layout and listeners
        Button left_score = view.findViewById(R.id.left_score_btn_gamf);
        Button right_score = view.findViewById(R.id.right_score_btn_gamf);
        //click to increase score, long click to cancel goal (decrease)
        left_score.setOnClickListener(this);
        left_score.setOnLongClickListener(this);
        right_score.setOnClickListener(this);
        right_score.setOnLongClickListener(this);
        //endregion

        //region timer layout and listeners
        start_timer = view.findViewById(R.id.start_timer_gamf);
        ImageButton reset_timer = view.findViewById(R.id.reset_timer_gamf);
        timer_tv = view.findViewById(R.id.timer_tv);
        timer_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getResources().getString(R.string.change_game_time))
                        .setMessage(getResources().getString(R.string.scroll_game_time_change));
                final View dialogView = getLayoutInflater().inflate(R.layout.edit_time_picker,null);

                final NumberPicker timerNumberPicker = dialogView.findViewById(R.id.timer_num_picker);
                timerNumberPicker.setMaxValue(90);
                timerNumberPicker.setMinValue(1);
                timerNumberPicker.setWrapSelectorWheel(true);

                if (!start_timer.isChecked()){
                    builder.setView(dialogView)
                            .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    time = timerNumberPicker.getValue();
                                    timer_tv.setText(time+":00");
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });

        start_timer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //need some check
                if (leftTimeInMillis == 0){
                    Long millisInput = 0L;
                    if (time == 0){
                        Snackbar.make(MainActivity.coordinatorLayout,getResources().getString(R.string.enter_valid_time),Snackbar.LENGTH_SHORT).show();
                        start_timer.setChecked(false);
                        return;
                    }

                    try {
                        millisInput = Long.valueOf(time) *60000;
                    }catch (NumberFormatException e){}

                    setTime(millisInput);
                }
                if (isChecked){ //play

                    startTimer();

                }
                else { //pause
                    pauseTimer();
                }
            }
        });
        reset_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start_timer.isChecked()){
                    start_timer.setChecked(false);
                }

                timer_tv.setText(time+":00");
                leftTimeInMillis=0;
            }
        });
//endregion

        //region next game view assignment
        priceIV = view.findViewById(R.id.price_ic_gamf);
        pitchIV = view.findViewById(R.id.pitch_ic_gamf);
        priceTV = view.findViewById(R.id.price_tv_gamf);
        pitchTV = view.findViewById(R.id.pitch_tv_gamf);
        locationTV = view.findViewById(R.id.location_tv_gamf);
        dateTV = view.findViewById(R.id.date_tv_gamf);
        //endregion
        displayGame();
        return view;
    }

    public void displayGame() {
//        try{//may fail if no group in pref
//        if (!sPref.getDisplayGroup().getMembers_id().contains(sPref.getUserId()))
//            return;}catch (Exception e){e.printStackTrace();}
        nextGame =  sPref.getNextGame();
        try {nextGame.getDate();}
        catch (Exception e){e.printStackTrace();nextGame = new Game();}
        String price = "Free";
        String pitch = "Grass";

        try {price = nextGame.getPrice();}
        catch (Exception e){e.printStackTrace();}
        try{ pitch = nextGame.getPitch();}
        catch (Exception e){e.printStackTrace();}
        priceTV.setText(price);
        pitchTV.setText(pitch);
        try{dateTV.setText(nextGame.getDate());}
        catch (Exception e){e.printStackTrace();dateTV.setText("Not set");}
            try{locationTV.setText(nextGame.getLocation());}
            catch (Exception e){locationTV.setText("Not set");}
        //handle price icon
        Log.d("changeNextGamePriceIcon", "cahngin to: "+price);
        if (price.toLowerCase().equals("free") || price.equals("0") || price.isEmpty()) {
            priceIV.setImageDrawable(res.getDrawable(R.drawable.price_free));
            priceTV.setText(res.getString(R.string.free));
        }else
            priceIV.setImageDrawable(res.getDrawable(R.drawable.price));
        //handle pitch icon
        String[] type = getResources().getStringArray(R.array.pitch_spinner);
        if (pitch.equals(type[0]))
            pitchIV.setImageDrawable(res.getDrawable(R.drawable.asphalt));
        else if (pitch.equals(type[1]))
            pitchIV.setImageDrawable(res.getDrawable(R.drawable.synthetic));
        else
            pitchIV.setImageDrawable(res.getDrawable(R.drawable.football_field_ic));

    }

    private void startTimer() {
        endTime = System.currentTimeMillis() + leftTimeInMillis;
        mCountDownTimer = new CountDownTimer(leftTimeInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                leftTimeInMillis = millisUntilFinished;
                updateCountdown();
            }
            @Override
            public void onFinish() {
                start_timer.setChecked(false);
                Intent intent = new Intent(getActivity(), TimerService.class);
                getActivity().startService(intent);

                //need to add notification
            }
        }.start();

        start_timer.setChecked(true);

    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        start_timer.setChecked(false);
    }

    private void setTime(Long milliSeconds) {
        startTimeInMillis = milliSeconds;
        updateTimer();
        closeKeyboard();
    }

    private void updateTimer() {
        leftTimeInMillis = startTimeInMillis;
        updateCountdown();
    }

    private void closeKeyboard() {
        //closing keyboard after enter time
        View view = getActivity().getCurrentFocus();
        if (view != null){
            InputMethodManager methodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            methodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void updateCountdown() {

        int hours = (int) (leftTimeInMillis / 1000) / 3600;
        int minutes = (int) ((leftTimeInMillis / 1000) % 3600) / 60;
        int seconds = (int) (leftTimeInMillis / 1000) % 60;

        String timeLeftFormatted;

        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }

        timer_tv.setText(timeLeftFormatted);
    }

    @Override
    public void onClick(View v) {
        changeScore(v.getId(),true);
    }

    private void changeScore(int id, boolean increase) {
        //long click will set increase to false (cancel goal)
        //normal click wil set to true
        Button btn = view.findViewById(id);
        int score = Integer.valueOf(btn.getText().toString());
        if (!increase && score == 0)
            return;
        score += increase? 1 : -1;
        btn.setText(String.valueOf(score));

    }

    @Override
    public boolean onLongClick(View v) {
        changeScore(v.getId(),false);
        return true;
    }

    @Override
    public void sendNextGameToFrag(Game nextGame) {
        displayGame();
    }

    //store next game info when finish fetching group from server

}
