package com.app.galnoriel.footbook.interfaces;

import com.app.galnoriel.footbook.classes.GroupPlay;
import com.app.galnoriel.footbook.classes.Player;

public interface MainToPlayerFrag {
    void onGetPlayerComplete(Player player);
    void onGetGroupComplete(GroupPlay group);
    void callUpdatePlayerFromMain();
}
