package com.app.galnoriel.footbook.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import com.app.galnoriel.footbook.MainActivity;
import com.app.galnoriel.footbook.R;

import java.util.Locale;

public class GameFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    View view;

    private long startTimeInMillis;
    private long leftTimeInMillis;
    private long endTime;

    private EditText timer_view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_game, container, false);

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
        final ToggleButton start_timer = view.findViewById(R.id.start_timer_gamf);
        ImageButton reset_timer = view.findViewById(R.id.reset_timer_gamf);
        timer_view = view.findViewById(R.id.timer_view);
//endregion

        start_timer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                String input = timer_view.getText().toString();
                //validation region
                if (input.length() == 0){
                    Snackbar.make(MainActivity.coordinatorLayout,"Enter valid time",Snackbar.LENGTH_SHORT).show();
                    start_timer.setChecked(false);
                    return;
                }

                Long millisInput = 0L;

                try {
                    millisInput = Long.parseLong(input) *6000;
                }catch (NumberFormatException e){

                }

                if (millisInput == 0){
                    Snackbar.make(MainActivity.coordinatorLayout,"Enter positive time",Snackbar.LENGTH_SHORT).show();
                    start_timer.setChecked(false);
                    return;
                }
                //end region


                setTime(millisInput);
                if (isChecked){ //play


                    timer_view.setEnabled(false);
                }
                else {

                }
            }
        });

        return view;
    }

    private void setTime(Long milliSeconds) {

        startTimeInMillis = milliSeconds;
        resetTimer();
        //closeKeyboard();
    }

    private void resetTimer() {
        leftTimeInMillis = startTimeInMillis;
        updateCountdown();
        //updateWatchInterface();
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

        timer_view.setText(timeLeftFormatted);
    }

    @Override
    public void onClick(View v) {
        changeScore(v.getId(),true);
    }

    /*please add validation that make sure that score will not be negtive!! expensive brother :P */
    private void changeScore(int id, boolean increase) {
        //long click will set increase to false (cancel goal)
        //normal click wil set to true
        Button btn = view.findViewById(id);
        int score = Integer.valueOf(btn.getText().toString());
        score += increase? 1 : -1;
        btn.setText(score+"");

    }

    @Override
    public boolean onLongClick(View v) {
        changeScore(v.getId(),false);
        return true;
    }
}
