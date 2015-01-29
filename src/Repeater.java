import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Repeater extends Stack<Request> implements Runnable {

    /**
     * Get repeater's singleton, it will send asynchronously all request
     * which has been sent with errors
     * @return - Repeater
     */
    public static Repeater getRepeater() {
        return repeater;
    }

    /**
     * Pushes an item onto the top of this stack
     * @param item - The item to be pushed onto this stack.
     * @return - The <code>item</code> argument.
     * @see java.util.Vector#addElement
     */
    @Override
    public Request push(Request item) {
        return push(item, null);
    }

    /**
     * Pushes an item onto the top of this stack
     * @param item - The item to be pushed onto this stack.
     * @param exception - Exception
     * @return - The <code>item</code> argument.
     * @see java.util.Vector#addElement
     */
    public Request push(Request item, Exception exception) {
        boolean wasEmpty = false;
        synchronized (this) {
            if (exception != null) {
                Logger.getLogger().write("Error (" + exception.getClass().getName() + "): \"" + exception.getMessage() + "\"");
            } else {
                Logger.getLogger().write("Error (UnknownError): \"Request has been added to repeater with unknown error\"");
            }
            super.push(item);
            if (this.size() == 1) {
                wasEmpty = true;
            }
        }
        if (wasEmpty) {
            lock.lock();
            condition.signal();
            lock.unlock();
        }
        return item;
    }

    /**
     * Run thread
     */
    @Override
    public final void run() {
        Logger.getLogger().write("Running repeater");
        while (true) {
            lock.lock();
            try {
                Logger.getLogger().write("Waiting for requests ...");
                while (size() == 0) {
                    condition.await();
                }
                Logger.getLogger().write("Catch (" + size() + ") requests, working ...");
            } catch (InterruptedException ignored) {
                break;
            }
            lock.unlock();
            Stack<Request> stack;
            synchronized (this) {
                stack = (Stack<Request>) clone();
                if (!isEmpty()) {
                    clear();
                }
            }
            for (Request r : stack) {
                try {
                    r.send();
                } catch (Exception e) {
                    try {
                        push(new Request(
                            r.getUrl(),
                            r.getMethod(),
                            r.getData(),
                            r.getCallback()
                        ), e);
                    } catch (Exception ignored) {
                    }
                }
            }
            Logger.getLogger().write("Waiting for (" + Config.REPEATER_DELAY + ") to resent requests");
            try {
                Thread.sleep(Config.REPEATER_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Locked singleton constructor
     */
    private Repeater() {
        thread.start();
    }

    private static Repeater repeater
        = new Repeater();

    /**
     * Get repeater's thread
     * @return - Thread
     */
    public Thread getThread() {
        return thread;
    }

    private Thread thread = new Thread(this);

    /**
     * Get repeater's reentrant lock
     * @return - Lock
     */
    public Lock getLock() {
        return lock;
    }

    private Lock lock = new ReentrantLock();

    /**
     * Get condition variable for empty list
     * @return - Condition variable
     */
    public Condition getCondition() {
        return condition;
    }

    private Condition condition = lock.newCondition();
}
