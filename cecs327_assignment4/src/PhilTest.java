import java.util.Arrays;
import java.util.concurrent.locks.*;

public class PhilTest {
    private static int WAITING=0, EATING=1, THINKING=2;
    private static final int NUM_PHILS = 5;
    private static Lock lock = new ReentrantLock();
    private static Condition phil [] = new Condition[NUM_PHILS];
    private static int states [] = new int[NUM_PHILS];

    public static void init ( ) {
        for (int k=0; k<NUM_PHILS; k++) {
            phil[k] = lock.newCondition();
            states[k] = THINKING;
        }
    }

    public static void main (String a[]) {
        init();
        Philosopher p[] = new Philosopher[NUM_PHILS];
        for (int k=0; k<p.length; k++) {
            p[k] = new Philosopher(lock, phil, states, NUM_PHILS);
            p[k].start();
        }
        //wait for all philosopher(thread) to finish
        for(Philosopher i:p) {
        	try {
				i.join();
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
        }
        //Debugging: print out wait time of each philosopher
        for(Philosopher i:p) {
        	System.out.printf("Philosopher %d's wait time:", i.getId()%NUM_PHILS);
        	System.out.println(i.getWaitTimeList());
        	System.out.print("\t\tExecution Time: " );
        	System.out.println(i.getExecutionTimeList());
        	System.out.printf("\t\tNumber of stall: %d\n\n", i.getWaitTimeList().size());
        }
    }

}
