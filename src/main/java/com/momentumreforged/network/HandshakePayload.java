package com.momentumreforged.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class HandshakePayload implements CustomPacketPayload {
    public static final HandshakePayload INSTANCE = new HandshakePayload();

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return MomentumReforgedNetwork.HANDSHAKE_TYPE;
    }

    public void encode(RegistryFriendlyByteBuf buf) {}

    public static HandshakePayload decode(RegistryFriendlyByteBuf buf) {
        return INSTANCE;
    }
}
