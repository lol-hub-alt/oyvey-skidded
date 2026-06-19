package me.alpha432.oyvey.features.modules.player;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

public class Flight extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public Flight() {
        super("Flight", "Enables player flight", Category.PLAYER, true, false, false);
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            mc.player.getAbilities().allowFlying = true;
            mc.player.getAbilities().flying = true;
            mc.player.getAbilities().setFlySpeed(0.05f);  // Adjust flight speed here
            mc.player.sendAbilitiesUpdate();
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.getAbilities().allowFlying = false;
            mc.player.getAbilities().flying = false;
            mc.player.getAbilities().setFlySpeed(0.05f);
            mc.player.sendAbilitiesUpdate();
        }
    }

    @Override
    public void onUpdate() {
        if (mc.player == null) return;

        PlayerEntity player = mc.player;

        // Handle vertical motion for flying
        boolean jump = InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_SPACE);
        boolean sneak = InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT);

        if (jump) {
            player.setVelocity(player.getVelocity().x, 0.5, player.getVelocity().z); // Ascend
        } else if (sneak) {
            player.setVelocity(player.getVelocity().x, -0.5, player.getVelocity().z); // Descend
        } else {
            player.setVelocity(player.getVelocity().x, 0, player.getVelocity().z); // No vertical movement
        }
    }

    @Subscribe
    private void onPacketReceive(PacketEvent.Receive event) {
        // Cancel velocity packets to avoid knockback interrupting flight
        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket || event.getPacket() instanceof ExplosionS2CPacket) {
            event.cancel();
        }
    }
}
