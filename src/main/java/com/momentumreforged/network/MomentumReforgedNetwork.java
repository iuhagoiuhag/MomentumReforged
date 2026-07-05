package com.momentumreforged.network;

import com.momentumreforged.MomentumReforged;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public final class MomentumReforgedNetwork {
    public static final Identifier HANDSHAKE_ID =
            Identifier.fromNamespaceAndPath(MomentumReforged.MOD_ID, "handshake");

    public static final CustomPacketPayload.Type<HandshakePayload> HANDSHAKE_TYPE =
            new CustomPacketPayload.Type<>(HANDSHAKE_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, HandshakePayload> HANDSHAKE_CODEC =
            StreamCodec.ofMember(HandshakePayload::encode, HandshakePayload::decode);

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(HANDSHAKE_TYPE, HANDSHAKE_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(HANDSHAKE_TYPE, HANDSHAKE_CODEC);
    }

    public static void registerClientHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(HANDSHAKE_TYPE, (payload, context) -> {
            context.client().execute(() -> {
                ClientPlayNetworking.send(HandshakePayload.INSTANCE);
            });
        });
    }
}
