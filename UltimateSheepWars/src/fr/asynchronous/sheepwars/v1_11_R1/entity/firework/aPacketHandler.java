package fr.asynchronous.sheepwars.v1_11_R1.entity.firework;

import java.util.List;

import org.bukkit.entity.Player;

public abstract class aPacketHandler implements iNmsObject{
	
	/**
	 * Send
	 */
	public final void send(Player...players){
		if(players.length==0)return;
		
		try{
			Object packet=this.build();
			for(Player player:players)if(player!=null)ProtocolUtils.refl_sendPacket0(player, packet);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public final void send(List<Player> players){
		this.send(players.toArray(new Player[players.size()]));
	}
}