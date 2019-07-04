package com.app.galnoriel.footbook.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import com.app.galnoriel.footbook.R;

public class GameFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    View view;
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
        ToggleButton start_timer = view.findViewById(R.id.start_timer_gamf);
        ImageButton reset_timer = view.findViewById(R.id.reset_timer_gamf);
//endregion
        return view;
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
        score += increase? 1 : -1;
        btn.setText(score+"");

    }

    @Override
    public boolean onLongClick(View v) {
        changeScore(v.getId(),false);
        return true;
    }
}
