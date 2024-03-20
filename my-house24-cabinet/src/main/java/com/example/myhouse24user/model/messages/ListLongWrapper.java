package com.example.myhouse24user.model.messages;

import java.util.List;

public class ListLongWrapper {
    List<Long> idsToMarkAsRead;

    public List<Long> getIdsToMarkAsRead() {
        return idsToMarkAsRead;
    }

    public void setIdsToMarkAsRead(List<Long> idsToMarkAsRead) {
        this.idsToMarkAsRead = idsToMarkAsRead;
    }
}
