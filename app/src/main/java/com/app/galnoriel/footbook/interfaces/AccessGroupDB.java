package com.app.galnoriel.footbook.interfaces;


import com.app.galnoriel.footbook.classes.GroupPlay;

public interface AccessGroupDB {
    String createNewGroupInServer(GroupPlay group);
    String updateGroupInServer(GroupPlay group);
    String requestGroupFromServer(String group_id, int frag);

}
