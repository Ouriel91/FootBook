package com.app.galnoriel.footbook.interfaces;

import com.app.galnoriel.footbook.classes.GroupPlay;
import com.app.galnoriel.footbook.classes.Player;

public interface MainToFrag {
    void onGetPlayerComplete(Player player);
    void onGetGroupComplete(GroupPlay group);
}
