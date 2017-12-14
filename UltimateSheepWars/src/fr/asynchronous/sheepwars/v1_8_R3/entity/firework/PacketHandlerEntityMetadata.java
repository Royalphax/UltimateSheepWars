package fr.asynchronous.sheepwars.v1_8_R3.entity.firework;

 import java.lang.reflect.Field;
 import java.util.LinkedList;
 import java.util.List;

 public class PacketHandlerEntityMetadata extends AbstractPacketHandler{

     private int entityId;
     private List<scWatchableObject> metadata;
     private static String mcVersion;
     private static Class<?> packetClass;
     private static Field fieldEntityId, fieldMetadata;

     static{
         try{
             PacketHandlerEntityMetadata.mcVersion=org.bukkit.Bukkit.getServer().getClass().getName().split("\\.")[3];
             PacketHandlerEntityMetadata.packetClass=Class.forName("net.minecraft.server."+PacketHandlerEntityMetadata.mcVersion+
                     ".PacketPlayOutEntityMetadata");
             PacketHandlerEntityMetadata.fieldEntityId=PacketHandlerEntityMetadata.packetClass.getDeclaredField("a");
             PacketHandlerEntityMetadata.fieldEntityId.setAccessible(true);
             PacketHandlerEntityMetadata.fieldMetadata=PacketHandlerEntityMetadata.packetClass.getDeclaredField("b");
             PacketHandlerEntityMetadata.fieldMetadata.setAccessible(true);
         }catch(Exception e){
             e.printStackTrace();
         }
     }

     public PacketHandlerEntityMetadata(int entityId, List<scWatchableObject> metadata){
         this.entityId=entityId;
         this.metadata=metadata;
     }

     /**
     * Build
     */
     @SuppressWarnings({"rawtypes", "unchecked"})
     @Override
     public Object build(){
         try{
             Object packet=PacketHandlerEntityMetadata.packetClass.newInstance();
        
             PacketHandlerEntityMetadata.fieldEntityId.set(packet, this.entityId);
        
             List watchableObjects=new LinkedList();
             for(scWatchableObject watchableObject:this.metadata)watchableObjects.add(watchableObject.toNMSObject());
             PacketHandlerEntityMetadata.fieldMetadata.set(packet, watchableObjects);
        
             return packet;
         }catch(Exception e){
             e.printStackTrace();
             return null;
         }
     }
 }
  