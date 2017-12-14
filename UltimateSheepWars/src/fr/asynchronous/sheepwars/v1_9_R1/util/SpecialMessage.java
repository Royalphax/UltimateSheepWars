package fr.asynchronous.sheepwars.v1_9_R1.util;

import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_9_R1.ChatClickable;
import net.minecraft.server.v1_9_R1.ChatHoverable;
import net.minecraft.server.v1_9_R1.ChatMessage;
import net.minecraft.server.v1_9_R1.ChatModifier;
import net.minecraft.server.v1_9_R1.IChatBaseComponent;

public class SpecialMessage
{
  private IChatBaseComponent base;

  public SpecialMessage(String s)
  {
    this.base = new ChatMessage(s, new Object[0]);
  }

  public IChatBaseComponent create() {
    return this.base;
  }

  public void sendToPlayer(Player player)
  {
    ((CraftPlayer)player).getHandle().sendMessage(this.base);
  }

  public IChatBaseComponent append(String string) {
    return this.base.a(string);
  }

  public IChatBaseComponent setHover(String string, ChatHoverable.EnumHoverAction hoverAction, String hoverString)
  {
    return this.base.addSibling(new ChatMessage(string, new Object[0]).setChatModifier(new ChatModifier().setChatHoverable(new ChatHoverable(hoverAction, new ChatMessage(hoverString, new Object[0])))));
  }

  public IChatBaseComponent setClick(String string, ChatClickable.EnumClickAction clickAction, String clickString)
  {
    return this.base.addSibling(new ChatMessage(string, new Object[0]).setChatModifier(new ChatModifier().setChatClickable(new ChatClickable(clickAction, clickString))));
  }
}