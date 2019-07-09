package com.app.galnoriel.footbook.interfaces;

import com.app.galnoriel.footbook.classes.Player;

public interface AccessPlayerDB {
    String updatePlayerInServer(Player player);
    String requestPlayerFromServer(String playerId, int frag);
    void openPlayerQueryDialog();

}
