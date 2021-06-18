package workers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import serializables.Task;
import serializables.TaskDone;

public abstract class Worker{
private static ObjectOutputStream out;
private static ObjectInputStream in;
private static Socket socket;
private static TaskDone coordinates;
private static String type;
private static InetAddress endereco;
private static int PORTO;


	public Worker(String endereco, String PORTO, String type) throws IOException, ClassNotFoundException {
	 	Worker.PORTO = Integer.parseInt(PORTO);
		Worker.endereco = InetAddress.getByName(endereco);
		Worker.type = type;
		
	}
	
	public Task getTask() throws ClassNotFoundException, IOException {
		return (Task)in.readObject();
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		if(args[2].equals("0"))
			new NormalWorker(args[0], args[1], new String("0"));
		if(args[2].equals("90"))
			new Worker90(args[0], args[1], new String("1"));
		if(args[2].equals("180"))
			new Worker180(args[0], args[1], new String("2"));
	}
	
	private static void connectToServer() throws IOException {
		socket = new Socket(endereco, PORTO);
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}
	
	public synchronized void runWorker() throws IOException, ClassNotFoundException {
		try {
			connectToServer();
			out.writeObject((String)"im worker");
			out.writeObject((int) Integer.parseInt(type));
			System.out.println(type);
			while(true) {
			Task task = (Task) in.readObject();
			getCoordinatesAndSend(task);
			}
		} catch (IOException e) {
				e.printStackTrace();
		}
	}
	
	public static BufferedImage rotate(BufferedImage bimg, double angle) {
	    int width = bimg.getWidth();    
	    int height = bimg.getHeight();
	    BufferedImage rotated = new BufferedImage(width, height, bimg.getType());  
	    Graphics2D graphic = rotated.createGraphics();
	    graphic.rotate(Math.toRadians(angle), width/2, height/2);
	    graphic.drawImage(bimg, null, 0, 0);
	    graphic.dispose();
	    return rotated;
	}
	
	protected void getCoordinatesAndSend(Task task) throws ClassNotFoundException, IOException {
		coordinates = procura(task);
		out.writeObject((TaskDone) coordinates);
	}

	protected abstract TaskDone procura(Task task) throws IOException, ClassNotFoundException;
}
