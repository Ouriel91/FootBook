package com.app.galnoriel.footbook.interfaces;


import com.app.galnoriel.footbook.classes.GroupPlay;

public interface UpdateGroupDB {
    String createNewGroupInServer(GroupPlay group);
    String updateGroupInServer(GroupPlay group);

}
