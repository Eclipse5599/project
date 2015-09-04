package extra;

/**
 * Overwrite doSomething when extending this class and enter updaterate in constructor
 */
public class ExecuterThread implements Runnable {

    int UPDATE_RATE = 3000;

    public ExecuterThread(int updateRate){
        UPDATE_RATE = updateRate;
    }

    public void run(){
        long pastTime = System.currentTimeMillis();
        while(true){
            if(System.currentTimeMillis() - pastTime > UPDATE_RATE) {
                doSomething();
            }
        }
    }

    public void doSomething(){};
}