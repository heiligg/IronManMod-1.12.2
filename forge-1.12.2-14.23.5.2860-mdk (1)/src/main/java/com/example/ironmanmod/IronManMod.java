package com.example.ironmanmod;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = IronManMod.MODID, name = IronManMod.NAME, version = IronManMod.VERSION)
public class IronManMod {

    public static final String MODID = "ironmanmod";
    public static final String NAME = "Iron Man Mod";
    public static final String VERSION = "1.0";

    // Armor pieces
    public static Item ironmanHelmet;
    public static Item ironmanChest;
    public static Item ironmanLegs;
    public static Item ironmanBoots;

    // Network wrapper for packets
    public static SimpleNetworkWrapper network;

    // Proxies
    @SidedProxy(
            clientSide = "com.example.ironmanmod.ClientProxy",
            serverSide = "com.example.ironmanmod.CommonProxy"
    )
    public static CommonProxy proxy;

    // Custom armor material
    public static final ItemArmor.ArmorMaterial IRONMAN_ARMOR_MATERIAL =
            EnumHelper.addArmorMaterial(
                    "IRONMAN",
                    MODID + ":ironman",
                    40,                       // durability multiplier
                    new int[]{3, 8, 6, 3},    // damage reduction [boots, chest, legs, helmet]
                    25,                       // enchantability
                    net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_IRON,
                    2.0F                      // toughness
            );

    // Packet ID counter
    private static int packetId = 0;
    private static int nextPacketId() {
        return packetId++;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        // Initialize network channel
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        network.registerMessage(
                RepulsorPacket.Handler.class,
                RepulsorPacket.class,
                nextPacketId(),
                Side.SERVER
        );

        // Register armor pieces
        ironmanHelmet = new IronManArmor(IRONMAN_ARMOR_MATERIAL, 1, EntityEquipmentSlot.HEAD)
                .setRegistryName(MODID, "ironman_helmet")
                .setUnlocalizedName(MODID + ".ironman_helmet")
                .setCreativeTab(CreativeTabs.COMBAT);
        ForgeRegistries.ITEMS.register(ironmanHelmet);

        ironmanChest = new IronManArmor(IRONMAN_ARMOR_MATERIAL, 1, EntityEquipmentSlot.CHEST)
                .setRegistryName(MODID, "ironman_chest")
                .setUnlocalizedName(MODID + ".ironman_chest")
                .setCreativeTab(CreativeTabs.COMBAT);
        ForgeRegistries.ITEMS.register(ironmanChest);

        ironmanLegs = new IronManArmor(IRONMAN_ARMOR_MATERIAL, 2, EntityEquipmentSlot.LEGS)
                .setRegistryName(MODID, "ironman_legs")
                .setUnlocalizedName(MODID + ".ironman_legs")
                .setCreativeTab(CreativeTabs.COMBAT);
        ForgeRegistries.ITEMS.register(ironmanLegs);

        ironmanBoots = new IronManArmor(IRONMAN_ARMOR_MATERIAL, 1, EntityEquipmentSlot.FEET)
                .setRegistryName(MODID, "ironman_boots")
                .setUnlocalizedName(MODID + ".ironman_boots")
                .setCreativeTab(CreativeTabs.COMBAT);
        ForgeRegistries.ITEMS.register(ironmanBoots);

        // Register client-side rendering
        proxy.registerRenderers();
    }
}
