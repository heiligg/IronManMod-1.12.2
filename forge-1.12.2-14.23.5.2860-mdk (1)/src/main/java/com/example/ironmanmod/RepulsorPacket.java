package com.example.ironmanmod;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RepulsorPacket implements IMessage {

    public RepulsorPacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<RepulsorPacket, IMessage> {

        @Override
        public IMessage onMessage(RepulsorPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            World world = player.world;

            player.getServerWorld().addScheduledTask(() -> {
                ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
                if (!chest.isEmpty() && chest.getItem() instanceof IronManArmor) {
                    IronManArmor armor = (IronManArmor) chest.getItem();
                    if (armor.getEnergy(chest) >= 10) {
                        // Spawn projectile
                        EntitySnowball projectile = new EntitySnowball(world, player);
                        projectile.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
                        world.spawnEntity(projectile);

                        // Drain energy on server
                        armor.consumeEnergy(chest, 10);
                    } else if (!world.isRemote) {
                        player.sendMessage(new net.minecraft.util.text.TextComponentString("Not enough energy!"));
                    }
                }
            });

            return null;
        }
    }
}
