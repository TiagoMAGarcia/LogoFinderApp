package client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import serializables.Data;

public class Client {
	
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
	private static int PORTO;
	private static InetAddress endereco;
	private GI gi;
	private HashMap<Integer, ArrayList<Point[]>> info = new HashMap<>();

	public Client(String endereco, String PORTO ) throws IOException {
		Client.endereco = InetAddress.getByName(endereco);
		Client.PORTO = Integer.parseInt(PORTO);
	}

	public void runClient() throws IOException, ClassNotFoundException, InterruptedException {
		try {
			connectToServer();
			out.writeObject((String) "im client");
			gi = new GI(this);
			ArrayList<String> workers = (ArrayList<String>) in.readObject();
			for(String x : workers) {
				gi.addWorkers(x);
			}
			gi.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendData(ArrayList<byte[]> images, byte[] logo, ArrayList<String> type) throws IOException, ClassNotFoundException {
		out.writeObject(new Data(images, logo, endereco, type));
		while(true) {
			ArrayList<String> workers = new ArrayList<>();
			Object t = in.readObject();
			System.out.println("x");
			if(t instanceof HashMap) {
				info = (HashMap<Integer, ArrayList<Point[]>>) t;
				break;
			}
			if(t instanceof ArrayList) {
				System.out.println("oi");
				DefaultListModel<String> model = new DefaultListModel<>();
				workers = (ArrayList<String>) t;
				for(String x : workers) {
					System.out.println(x);
					gi.addWorkers(x);
					model.addElement(x);
				gi.updateWorkers(model);
				}
			}
		}	
	}
	
	public HashMap<String, ImageIcon> draw(ArrayList<BufferedImage> bufferedImages, ArrayList<String> imagesName) throws IOException {
		HashMap<String, ImageIcon> imagens = new HashMap<>();
		ArrayList<Point[]> points;
		for(Integer k = 0; k !=bufferedImages.size(); k++) {
			if(!info.get(k).isEmpty()) {
				points = info.get(k);
				for (int x = 0; x != points.size(); x++) {
					Graphics2D g2d = bufferedImages.get(k).createGraphics();
					g2d.setColor(Color.RED);
					g2d.drawRect(points.get(x)[0].x, points.get(x)[0].y, points.get(x)[1].x - points.get(x)[0].x,
							points.get(x)[1].y - points.get(x)[0].y);
					g2d.dispose();
				}
				imagens.put(imagesName.get(k), new ImageIcon(bufferedImages.get(k)));
				gi.addModel(imagesName.get(k));
			}
		}
		return imagens;
	}

	private void connectToServer() throws IOException {
		socket = new Socket(endereco, PORTO);
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Client client = new Client(args[0], args[1]);
		client.runClient();
	}
}
