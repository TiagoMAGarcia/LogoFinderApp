package serializables;

import java.awt.Point;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

public class TaskDone implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Point[]> coordenadas = new ArrayList<>();
	private InetAddress address;
	private Integer index;
	
	public TaskDone(ArrayList<Point[]> c, InetAddress address, Integer index) {
		this.address = address;
		this.coordenadas = c;
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public InetAddress getAddress() {
		return address;
	}

	public ArrayList<Point[]> getCoordenadas() {
		return coordenadas;
	}
}
