package fr.royalpha.sheepwars.v1_9_R1.entity.firework;

import org.bukkit.inventory.ItemStack;

public class jDataWatcherObject<T> implements iNmsObject{
 
    public static final jDataWatcherObject<Byte> entity_ay=new jDataWatcherObject<>("ax", "Entity");
    public static final jDataWatcherObject<Integer> entity_az=new jDataWatcherObject<>("ay", "Entity");
    public static final jDataWatcherObject<String> entity_aA=new jDataWatcherObject<>("az", "Entity");
    public static final jDataWatcherObject<Boolean> entity_aB=new jDataWatcherObject<>("aA", "Entity");
    public static final jDataWatcherObject<Boolean> entity_aC=new jDataWatcherObject<>("aB", "Entity");
    public static final jDataWatcherObject<ItemStack> entityfireworks_FIREWORK_ITEM=new jDataWatcherObject<>("FIREWORK_ITEM", "EntityFireworks");
 
    private final Object nms;
 
    private jDataWatcherObject(String fieldname, String classname){
        this.nms=ProtocolUtils.refl_fieldGet0(fieldname, classname);
    }
 
    /**
    * Build
    */
    @Override
    public Object build(){
        return this.nms;
    }
}
