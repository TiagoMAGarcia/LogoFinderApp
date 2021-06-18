package server;

import java.util.LinkedList;
import java.util.Queue;

public class Fila <T>{
	private Queue <T> queue;
	private int c = -1;
	
	public Fila() {
	queue = new LinkedList<>();
	}
	
	public Fila(int t) {
		queue = new LinkedList<>();
		c = t;
	}
	
	public synchronized void offer(T t) throws InterruptedException {
		while(c != -1 && queue.size() == c) {
			wait();
		}
		queue.offer(t);
		notifyAll();
	}
	
	public synchronized T poll() throws InterruptedException {
		while(queue.size() == 0) {
			wait();
		}
		if(c != -1)
			notifyAll();
		return queue.poll();
	}
	
	public int size() {
		return queue.size();
	}
	
	public synchronized void clear() {
		queue.clear();
		notifyAll();
	}
	
	public synchronized T peek() throws InterruptedException {
		while(queue.size() == 0) {
			wait();
		}
		notifyAll();
		return queue.peek();
	}
}
