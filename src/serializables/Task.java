package serializables;

import java.io.Serializable;
import java.net.InetAddress;

public class Task implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private byte[] image;
private byte[] subimage;
private InetAddress address;
private String type;
private int index;

	public Task(byte[] image, byte[] subimage, InetAddress address, String type, int index) {
		this.image = image;
		this.subimage = subimage;
		this.address = address;
		this.type = type;
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	public byte[] getImage() {
		return image;
	}
	
	public byte[] getSubimage() {
		return subimage;
	}
	
	public String getType() {
		return type;
	}
}
