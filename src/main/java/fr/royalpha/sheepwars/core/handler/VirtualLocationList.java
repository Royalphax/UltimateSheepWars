package fr.royalpha.sheepwars.core.handler;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class VirtualLocationList {

	final List<VirtualLocation> list;

	public VirtualLocationList(List<VirtualLocation> list) {
		this.list = list;
	}

	public List<VirtualLocation> getVirtualLocations() {
		return this.list;
	}

	public List<Location> getBukkitLocations() {
		List<Location> output = new ArrayList<>();
		for (VirtualLocation loc : this.list) {
			output.add(loc.toBukkitLocation());
		}
		return output;
	}

	public int size() {
		return list.size();
	}

	public static VirtualLocationList fromBukkitLocationList(List<Location> list) {
		List<VirtualLocation> virtualList = new ArrayList<>();
		for (Location loc : list) {
			virtualList.add(VirtualLocation.fromBukkitLocation(loc));
		}
		return new VirtualLocationList(virtualList);
	}

}
