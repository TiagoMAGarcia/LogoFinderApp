package server;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import serializables.Data;
import serializables.Task;
import serializables.TaskDone;

public class Server {
	private Socket socket;
	public static int PORTO;
	private ArrayList<Fila <Task>> globalTasks = new ArrayList<>();
	private HashMap<InetAddress, TasksDoneBarrier> globalTasksDone = new HashMap<>();
	private ArrayList<Integer> workersNumber = new ArrayList<>();
	private LinkedList<DealWithClient> clients = new LinkedList<>();

	public Server(String PORTO) {
		Server.PORTO = Integer.parseInt(PORTO);
		Fila <Task> simpletasks = new Fila<>();
		Fila <Task> task90s = new Fila<>();
		Fila <Task> tasks180 = new Fila<>();
		globalTasks.add(simpletasks);
		globalTasks.add(task90s);
		globalTasks.add(tasks180);
	}

	public static void main(String[] args) throws ClassNotFoundException, InterruptedException, IOException {
		System.out.println(args[0]);
		Server x = new Server(args[0]);
		x.startServing();
	}

	public synchronized void startServing() throws IOException, ClassNotFoundException, InterruptedException {
		ServerSocket s = new ServerSocket(PORTO);
		System.out.println("server ligado");
		workersNumber.add(0);
		workersNumber.add(0);
		workersNumber.add(0);
		try {
			while (true) {
				socket = s.accept();
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				String type = (String) in.readObject();
				System.out.println(type);
				if(type.equals("im client")) {
					DealWithClient dwc = new DealWithClient(out, in);
					dwc.start();
					clients.add(dwc);
				}
				if(type.equals("im worker")) 
					new DealWithWorker(in, out, socket).start();
			}
		} finally {
			s.close();
		}
	}

	private class DealWithClient extends Thread{
		private Data info;
		private HashMap<Integer, ArrayList<Point[]>> coordinates = new HashMap<>();
		private ObjectOutputStream out;
		private ObjectInputStream in;
		private InetAddress address;
		
		public DealWithClient(ObjectOutputStream out, ObjectInputStream in) throws ClassNotFoundException, IOException {
			this.in = in;
			this.out = out;
		}
		
		public ArrayList<String> getWorkers(){
			ArrayList<String> workersType = new ArrayList<>();
			if(workersNumber.get(0) > 0)
				workersType.add(new String("Procura simples"));
			if(workersNumber.get(1) > 0)
				workersType.add(new String("Procura 90º"));
			if(workersNumber.get(2) > 0)
				workersType.add(new String("Procura 180º"));
			return workersType;
		}
		
		private void sendUpdate(ArrayList<String> update) throws IOException {
			out.writeObject(update);
			out.flush();
		}
		
		public synchronized void run() {
			try {
				out.writeObject(getWorkers());
				this.info = (Data)in.readObject();
				this.address = info.getAddress();
				globalTasksDone.put(this.address, new TasksDoneBarrier(info.getImage().size()*info.getType().size()));
			} catch (ClassNotFoundException | IOException e1) {
				e1.printStackTrace();
			}
			for(int k = 0; k != info.getImage().size(); k++) {
				try {
					for(int x = 0; x != info.getType().size(); x++) {
						if(info.getType().get(x).equals("Procura simples"))
							globalTasks.get(0).offer(new Task(info.getImage().get(k), info.getLogo(), info.getAddress(), info.getType().get(x), k));
						if(info.getType().get(x).equals("Procura 90º"))
							globalTasks.get(1).offer(new Task(info.getImage().get(k), info.getLogo(), info.getAddress(), info.getType().get(x), k));
						if(info.getType().get(x).equals("Procura 180º"))
							globalTasks.get(2).offer(new Task(info.getImage().get(k), info.getLogo(), info.getAddress(), info.getType().get(x), k));
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				ArrayList<TaskDone> td = globalTasksDone.get(address).poll();
				for(int x = 0; x != td.size(); x++) {
					if(coordinates.containsKey(td.get(x).getIndex())) {
						coordinates.get(td.get(x).getIndex()).addAll(td.get(x).getCoordenadas());
					}else {
						coordinates.put(td.get(x).getIndex(), td.get(x).getCoordenadas());
					}
				}
				out.writeObject(coordinates);
				out.flush();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class DealWithWorker extends Thread{
		private int type;
		private ObjectOutputStream out;
		private ObjectInputStream in;
		private InetAddress clientAddress;
		private Socket socket;
		private Task t;
		private TaskDone td;
		private ArrayList<Integer> wN = new ArrayList<>();
		
		public DealWithWorker(ObjectInputStream in, ObjectOutputStream out, Socket socket) throws ClassNotFoundException, IOException {
			this.socket = socket;
			this.out = out; 
			this.in = in;
			this.type = (int) in.readObject();
			this.wN.add(workersNumber.get(0));
			this.wN.add(workersNumber.get(1));
			this.wN.add(workersNumber.get(2));
			workersNumber.set(type, workersNumber.get(type)+1);
			new UpdateClient(this.wN).start();
		}
		
		public synchronized void run() {
			while(true) {
				try {
					t = (Task) globalTasks.get(type).poll();
					this.clientAddress = t.getAddress();
					out.writeObject(t);
					t = null;
					Timer timer = new Timer(120);
					timer.start();
					td = (TaskDone) in.readObject();
					globalTasksDone.get(clientAddress).offer(td);
					timer.interrupt();
					td = null;
				} catch (InterruptedException | ClassNotFoundException e) {
						e.printStackTrace();
				} catch (IOException e) {
					if((t != null && td == null) || td != null)
						globalTasksDone.get(clientAddress).offer(td);
					if(workersNumber.get(type) == 0) {
						for(int x = 0; x != globalTasks.get(type).size(); x++) {
							globalTasksDone.get(clientAddress).offer(null);
						}
					}
				}
			}
		}
		
		private class Timer extends Thread{
			private int time=0;
			private int maxTime;
			
			public Timer(int t) {
				this.maxTime = t;
			}
			
			public void run() {
				try {
					while(time < maxTime) {
						sleep(1000);
						time++;
					}
					socket.close();
					} catch (IOException | InterruptedException e) {
				}
			}
		}
		
		private class UpdateClient extends Thread{
			private ArrayList<Integer> wN;
			
			public UpdateClient(ArrayList<Integer> array) {
				this.wN = array;
			}
			
			private ArrayList<String> getWorkers(){
				ArrayList<String> workersType = new ArrayList<>();
				if(workersNumber.get(0) > 0)
					workersType.add(new String("Procura simples"));
				if(workersNumber.get(1) > 0)
					workersType.add(new String("Procura 90º"));
				if(workersNumber.get(2) > 0)
					workersType.add(new String("Procura 180º"));
				return workersType;
			}
			
			private void update() throws IOException {
				for(DealWithClient dwc : clients)
					dwc.sendUpdate(getWorkers());
			}
			
			public void run() {
				try {
					sleep(1000);
					if(wN.get(0) == 0 && workersNumber.get(0) == 1) {
						update();
					}
					if(wN.get(1) == 0 && wN.get(1) != workersNumber.get(1)) {
						update();
					}
					if(wN.get(2) == 0 && wN.get(2) != workersNumber.get(2)) {
						update();
					}
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
