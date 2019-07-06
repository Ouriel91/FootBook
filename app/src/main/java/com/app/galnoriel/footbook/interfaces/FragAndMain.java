package com.app.galnoriel.footbook.interfaces;

import com.app.galnoriel.footbook.classes.GroupPlay;
import com.app.galnoriel.footbook.classes.Player;

public interface FragAndMain {
    void onGetPlayerComplete(Player player);
    void onGetGroupComplete(GroupPlay group);
//    void goToFrag(String... params); //[0] from, [1] to , [2...n] params
}
