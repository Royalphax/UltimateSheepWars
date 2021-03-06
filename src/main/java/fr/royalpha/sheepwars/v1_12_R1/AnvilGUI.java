package fr.royalpha.sheepwars.v1_12_R1;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.version.AAnvilGUI;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class AnvilGUI extends AAnvilGUI {
	public AnvilGUI(Player player, SheepWarsPlugin plugin, AnvilClickEventHandler handler, String itemName, String... itemLore) {
        super(player, plugin, handler, itemName, itemLore);
    }

    private class AnvilContainer extends ContainerAnvil {
        public AnvilContainer(EntityHuman entity) {
            super(entity.inventory, entity.world, new BlockPosition(0, 0, 0), entity);
        }

        @Override
        public boolean canUse(EntityHuman entityhuman) {
            return true;
        }
    }

    @Override
    public void open() {
        EntityPlayer p = ((CraftPlayer) player).getHandle();
        AnvilContainer container = new AnvilContainer(p);
        inv = container.getBukkitView().getTopInventory();
        for (AnvilSlot slot : items.keySet())
            inv.setItem(slot.getSlot(), items.get(slot));
        int c = p.nextContainerCounter();
        p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Repairing"), 0));
        p.activeContainer = container;
        p.activeContainer.windowId = c;
        p.activeContainer.addSlotListener(p);
    }
}