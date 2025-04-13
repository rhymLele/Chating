package com.duc.chatting.chat.models;

import java.io.Serializable;

public class BlockStatus implements Serializable {
    public final boolean youBlockedThem;
    public final boolean theyBlockedYou;

    public BlockStatus(boolean youBlockedThem, boolean theyBlockedYou) {
        this.youBlockedThem = youBlockedThem;
        this.theyBlockedYou = theyBlockedYou;
    }

    public boolean isAnyBlocked() {
        return youBlockedThem || theyBlockedYou;
    }
}
