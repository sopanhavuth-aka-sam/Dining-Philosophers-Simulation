import java.util.ArrayList;
import java.util.concurrent.locks.*;

public class Philosopher extends Thread {
	//calculate execution time also? he want more metric to compare in the report
    private static int WAITING=0, EATING=1, THINKING=2;
    private Lock lock;
    private Condition phil [];
    private int states [];
    private int NUM_PHILS;
    private int id;
    private final int TURNS = 20;
    private long startTime, endTime; //for calculating wait time
    private ArrayList<Long> waitTime;
    private ArrayList<Long> executionTime;

    public Philosopher (Lock l, Condition p[], int st[], int num) {
        this.lock = l;
        this.phil = p;
        this.states = st;
        this.NUM_PHILS = num;
        this.waitTime = new ArrayList<Long>();
        this.executionTime = new ArrayList<Long>();
    }

    public void run () {
        id = (int)(Thread.currentThread().getId()%NUM_PHILS);
        for (int k=0; k<TURNS; k++) {

            try {
                sleep(100);
            } catch (Exception ex) { }
            
            long startTime = System.nanoTime();
            takeSticks(id);

            try {
                sleep(20);
            } catch (Exception ex) { }

            putSticks(id);
            this.executionTime.add(System.nanoTime() - startTime);
            
        }
    }

    public void takeSticks (int id) {
        lock.lock();
        try {
            if (states[leftof(id)]!=EATING && states[rightof(id)]!=EATING) {
                states[id] = EATING;
                System.out.printf("Philosopher %d is eating\n", id);
            }
            else {
                states[id] = WAITING;
                long startTime = System.nanoTime();
                phil[id].await();
                waitTime.add(System.nanoTime() - startTime);
            }
        }catch (InterruptedException e) {
            System.exit(-1);
        } finally {
            lock.unlock();
        }
    }


    public void putSticks (int id) {
        lock.lock();
        try {
            states[id] = THINKING;
            if (states[leftof(id)]==WAITING && states[leftof(leftof(id))]!=EATING) {
                phil[leftof(id)].signal();
                states[leftof(id)] = EATING;
                System.out.printf("Philosopher %d is eating\n", leftof(id));
            }
            if (states[rightof(id)] == WAITING && states[rightof(rightof(id))] != EATING) {
                phil[rightof(id)].signal();
                states[rightof(id)] = EATING;
                System.out.printf("Philosopher %d is eating\n", rightof(id));
            }
        } finally {
            lock.unlock();
        }
    }

    private int leftof (int id) { // clockwise
        int retval = id-1;
        if (retval < 0) { // not valid id
            retval = NUM_PHILS-1;
        }
        return retval;
    }

    private int rightof (int id) {
        int retval = id+1;
        if (retval == NUM_PHILS) { // not valid id
            retval=0;
        }
        return retval;
    }
    
    public ArrayList<Long> getWaitTimeList() {
    	return this.waitTime;
    }
    
    public ArrayList<Long> getExecutionTimeList() {
    	return this.executionTime;
    }

}
