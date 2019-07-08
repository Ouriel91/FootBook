package com.app.galnoriel.footbook.interfaces;

public interface MoveToTab {
    void goToFrag(int to,String... params);

    String requestGroupFromServer(String group_id, int frag);
}
