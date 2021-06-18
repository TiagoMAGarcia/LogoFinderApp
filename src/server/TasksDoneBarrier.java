package server;

import java.util.ArrayList;

import serializables.TaskDone;

public class TasksDoneBarrier {
	
	private int minNumber;
	private int currentTasksNumber = 0;
	private ArrayList<TaskDone> tasks = new ArrayList<>();
	
	public TasksDoneBarrier(int x) {
		this.minNumber = x;
	}
	
	public synchronized void offer(TaskDone td) {
		tasks.add(td);
		 currentTasksNumber++;
		if(currentTasksNumber == minNumber)
			notifyAll();
	}
	
	public synchronized ArrayList<TaskDone> poll() throws InterruptedException {
		while(currentTasksNumber < minNumber) {
			wait();
		}
		notifyAll();
		return tasks;
	}
}
