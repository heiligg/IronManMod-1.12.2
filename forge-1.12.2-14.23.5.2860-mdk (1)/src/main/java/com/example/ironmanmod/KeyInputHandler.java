package com.example.ironmanmod;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

public class KeyInputHandler {

    private boolean flightToggled = false;
    private boolean flyKeyPreviouslyPressed = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) return;

        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof IronManArmor)) return;

        IronManArmor armor = (IronManArmor) chest.getItem();

        // --- Repulsor (R key) ---
        if (ClientProxy.repulsorKey.isPressed()) {
            // send packet to server, not client-side shoot
            IronManMod.network.sendToServer(new RepulsorPacket());
        }

        // --- Flight toggle (F key, single press) ---
        boolean flyKeyPressed = ClientProxy.flyKey.isKeyDown();
        if (flyKeyPressed && !flyKeyPreviouslyPressed && armor.getEnergy(chest) >= 5) {
            flightToggled = !flightToggled;
        }
        flyKeyPreviouslyPressed = flyKeyPressed;

        // --- Apply flight ---
        if (flightToggled && armor.getEnergy(chest) > 0) {
            player.capabilities.allowFlying = true;

            // Vertical control
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                player.motionY += 0.1;
                armor.consumeEnergy(chest, 1);
            } else if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                player.motionY -= 0.1;
                armor.consumeEnergy(chest, 1);
            } else {
                player.motionY = 0; // hover
            }

            // Horizontal movement
            double speed = 0.05;
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                speed = 0.1; // boost
            }
            double yaw = Math.toRadians(player.rotationYaw);
            player.motionX += -Math.sin(yaw) * speed;
            player.motionZ += Math.cos(yaw) * speed;

            // Constant energy drain while flying
            armor.consumeEnergy(chest, 1);

            // Stop flight if energy runs out
            if (armor.getEnergy(chest) <= 0) flightToggled = false;
        } else {
            player.capabilities.allowFlying = false;
        }

        // Prevent vanilla double-jump creative flight
        player.capabilities.isFlying = false;

        // Reset toggle when landing
        if (player.onGround) flightToggled = false;
    }
}
