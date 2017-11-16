package fr.asynchronous.sheepwars.v1_8_R3.entity.firework;

 import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

 public class FireworkSpawner{

     public static void spawn(Location location, FireworkEffect effect, ArrayList<Player> players){
         int entityId=FireworkSpawner.getNextEntityId();
    
         //Spawn
         new PacketHandlerSpawnEntity(entityId, 76/*firework entityId*/, location.getX(), location.getY(), location.getZ(),
                 location.getYaw(), location.getPitch(), 0, 0, 0, 0).send(players);
    
         //Set FireworkMeta
         ItemStack fireworkItem=new ItemStack(Material.FIREWORK);
         FireworkMeta meta=(FireworkMeta)fireworkItem.getItemMeta();
         meta.addEffect(effect);
         fireworkItem.setItemMeta(meta);
    
         scWatchableObject[] metadata=new scWatchableObject[]{
                 new scWatchableObject(5, 8, FireworkSpawner.toNMSItemStack(fireworkItem)),
                 new scWatchableObject(0, 4, (byte)0),
                 new scWatchableObject(0, 3, (byte)0),
                 new scWatchableObject(4, 2, ""),
                 new scWatchableObject(1, 1, (short)300),
                 new scWatchableObject(0, 0, (byte)0)
         };
    
         new PacketHandlerEntityMetadata(entityId, Arrays.asList(metadata)).send(players);
    
         //Play Effect
         new PacketHandlerEntityStatus(entityId, (byte)17/*Explode Status*/).send(players);
    
         //Destroy Entity (very important, otherwise clients will crash)
         new PacketHandlerEntityDestroy(entityId).send(players);
     }

     /**
     * Get a new entityID
     */
     private static int currentEntityId=Integer.MAX_VALUE;
     public static int getNextEntityId(){
         return FireworkSpawner.currentEntityId++;
     }

     /**
     * Convert an org.bukkit.ItemStack to a net.minecraft.server.<version>.ItemStack
     */
     private static Method asNMSCopy;
     static{
         try{
             FireworkSpawner.asNMSCopy=Class.forName("org.bukkit.craftbukkit."+
                     Bukkit.getServer().getClass().getName().split("\\.")[3]+
                     ".inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class);
         }catch(Exception a){
             a.printStackTrace();
         }
     }
     public static Object toNMSItemStack(ItemStack item){
         try{
             return FireworkSpawner.asNMSCopy.invoke(null, item);
         }catch(Exception a){
             a.printStackTrace();
             return null;
         }
     }
 }
  