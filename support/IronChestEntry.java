package net.nexustools.chesty.support;

public class IronChestEntry {
	public String internalName;
	public String friendlyName;
	public String textureLocation;
	public int subType;
	public int size;
	public int rowLength;

	public IronChestEntry(String internalName, String friendlyName, String textureLocation, int subType, int size, int rowLength) {
		this.internalName = internalName;
		this.friendlyName = friendlyName;
		this.textureLocation = textureLocation;
		this.subType = subType;
		this.size = size;
		this.rowLength = rowLength;
	}
}
