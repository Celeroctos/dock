import java.util.Stack;

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
            try {
                Thread.sleep(Config.REPEATER_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.notify();
        }
        return item;
    }

    /**
     * Run thread
     */
    @Override
    public void run() {
        while (true) {
            try {
                lock.wait();
            } catch (InterruptedException ignored) {
                break;
            }
            Stack<Request> stack;
            synchronized (this) {
                stack = (Stack<Request>) this.clone();
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

    private Thread thread
        = new Thread(this);

    private Object lock;
}
