import org.jcsp.lang.*;

/** Producer class: produces 100 random integers and sends them on
 * output channel, then sends -1 and terminates.
 * The random integers are in a given range [start...start+100)
 */
public class Producer2 implements CSProcess {
    private One2OneChannelInt channel;
    private int start;
    public Producer2 (final One2OneChannelInt out, int start) {
        channel = out;
        this.start = start;
    } // constructor
    public void run () {
        int item;
        for (int k = 0; k < 100; k++) {
            item = (int)(Math.random()*100)+1+start;
            channel.out().write(item);
        } // for
        channel.out().write(-1);
        System.out.println("Producer" + start + " ended.");
    } // run
} // class Producer2