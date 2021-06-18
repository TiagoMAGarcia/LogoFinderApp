package serializables;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

public class Data implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private ArrayList<byte[]> images;
private byte [] logo;
private InetAddress address;
private ArrayList<String> type;

	public Data(ArrayList<byte[]> image, byte[] logo, InetAddress address, ArrayList<String> type) {
		this.images = image;
		this.logo = logo;
		this.type = type;
		this.address = address;
	}
	
	public byte[] getLogo() {
		return logo;
	}
	
	public ArrayList<byte[]> getImage() {
		return images;
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	public ArrayList<String> getType() {
		return type;
	}
}
