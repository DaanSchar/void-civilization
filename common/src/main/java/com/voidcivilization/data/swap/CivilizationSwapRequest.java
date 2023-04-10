package com.voidcivilization.data.swap;

import com.mojang.authlib.GameProfile;

import java.util.Date;

public class CivilizationSwapRequest {

    private final GameProfile sender;
    private final GameProfile receiver;
    private final Date date;

    public CivilizationSwapRequest(GameProfile sender, GameProfile receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = new Date();
    }

    public boolean isExpired() {
        return (new Date().getTime() - date.getTime()) > 1000 * 60 * 5;
    }

    public GameProfile getSender() {
        return sender;
    }

    public GameProfile getReceiver() {
        return receiver;
    }
}
