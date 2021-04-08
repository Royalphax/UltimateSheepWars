package fr.royalpha.sheepwars.v1_15_R1;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.version.AAnvilGUI;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class AnvilGUI extends AAnvilGUI {
    public AnvilGUI(Player player, SheepWarsPlugin plugin, AnvilClickEventHandler handler, String itemName, String... itemLore) {
        super(player, plugin, handler, itemName, itemLore);
    }

    private class AnvilContainer extends ContainerAnvil {
        public AnvilContainer(int c, EntityHuman entity) {
            super(c, entity.inventory, ContainerAccess.at(entity.world, new BlockPosition(0, 0, 0)));
        }

        @Override
        public boolean canUse(EntityHuman entityhuman) {
            return true;
        }
    }

    @Override
    public void open() {
        EntityPlayer p = ((CraftPlayer) player).getHandle();
        int c = p.nextContainerCounter();
        fr.royalpha.sheepwars.v1_15_R1.AnvilGUI.AnvilContainer container = new fr.royalpha.sheepwars.v1_15_R1.AnvilGUI.AnvilContainer(c, p);
        inv = container.getBukkitView().getTopInventory();
        for (AnvilSlot slot : items.keySet())
            inv.setItem(slot.getSlot(), items.get(slot));
        p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, Containers.ANVIL, new ChatMessage("Repairing")));
        p.activeContainer = p.defaultContainer;
        //p.activeContainer.windowId = c;
        p.activeContainer.addSlotListener(p);
    }
}
