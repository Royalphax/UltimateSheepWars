package fr.asynchronous.sheepwars.v1_8_R3.entity.firework;

 import java.lang.reflect.Field;

 public class PacketHandlerEntityStatus extends AbstractPacketHandler{

     private int entityId;
     private byte statusId;
     private static String mcVersion;
     private static Class<?> packetClass;
     private static Field fieldEntityId, fieldStatusId;

     static{
         try{
             PacketHandlerEntityStatus.mcVersion=org.bukkit.Bukkit.getServer().getClass().getName().split("\\.")[3];
             PacketHandlerEntityStatus.packetClass=Class.forName("net.minecraft.server."+PacketHandlerEntityStatus.mcVersion+
                     ".PacketPlayOutEntityStatus");
             PacketHandlerEntityStatus.fieldEntityId=PacketHandlerEntityStatus.packetClass.getDeclaredField("a");
             PacketHandlerEntityStatus.fieldEntityId.setAccessible(true);
             PacketHandlerEntityStatus.fieldStatusId=PacketHandlerEntityStatus.packetClass.getDeclaredField("b");
             PacketHandlerEntityStatus.fieldStatusId.setAccessible(true);
         }catch(Exception e){
             e.printStackTrace();
         }
     }

     public PacketHandlerEntityStatus(int entityId, byte statusId){
         this.entityId=entityId;
         this.statusId=statusId;
     }

     /**
     * Build
     */
     @Override
     public Object build(){
         try{
             Object packet=PacketHandlerEntityStatus.packetClass.newInstance();
        
             PacketHandlerEntityStatus.fieldEntityId.set(packet, this.entityId);
             PacketHandlerEntityStatus.fieldStatusId.set(packet, this.statusId);
        
             return packet;
         }catch(Exception e){
             e.printStackTrace();
             return null;
         }
     }
 }
  