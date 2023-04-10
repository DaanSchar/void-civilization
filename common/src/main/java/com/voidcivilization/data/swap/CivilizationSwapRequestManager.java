package com.voidcivilization.data.swap;

import com.mojang.authlib.GameProfile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CivilizationSwapRequestManager {

    private static final Map<GameProfile, Map<GameProfile, CivilizationSwapRequest>> requests = new HashMap<>();

    public static void addRequest(CivilizationSwapRequest request) {
        if (!requests.containsKey(request.getSender())) {
            requests.put(request.getSender(), new HashMap<>());
        }

        requests.get(request.getSender()).put(request.getReceiver(), request);
    }

    public static Optional<CivilizationSwapRequest> getRequest(GameProfile sender, GameProfile receiver) {
        if (!requests.containsKey(sender)) {
            return Optional.empty();
        }

        return Optional.ofNullable(requests.get(sender).get(receiver));
    }

    public static void deleteRequest(GameProfile sender, GameProfile receiver) {
        if (!requests.containsKey(sender)) {
            return;
        }

        requests.get(sender).remove(receiver);
    }

}
