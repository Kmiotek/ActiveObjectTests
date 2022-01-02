import org.jcsp.lang.*;

/** Producer class: produces 100 random integers and sends them on
 * output channel, then sends -1 and terminates.
 * The random integers are in a given range [start...start+100)
 */
public class Producer2 implements CSProcess {
    private One2OneChannelInt[] bufferChannels;
    private One2OneChannelInt supervisorChannel;
    private final int id;

    public Producer2 (final One2OneChannelInt supervisor, One2OneChannelInt[] bufferChannels, int id) {
        supervisorChannel = supervisor;
        this.id = id;
        this.bufferChannels = bufferChannels;
    } // constructor
    public void run () {
        boolean running = true;
        while(running) {
            System.out.println("Asking prod " + id);
            supervisorChannel.out().write(1);
            System.out.println("Getting answer prod " + id);
            int index = supervisorChannel.in().read();
            System.out.println("Got answer prod " + id);
            if (index != -1) {
                System.out.println("Communicating with buffer " + index + " prod " + id);
                bufferChannels[index].out().write(1);
            }
        } // for
        System.out.println("Producer" + id + " ended.");
    } // run
} // class Producer2