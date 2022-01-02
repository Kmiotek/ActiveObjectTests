import org.jcsp.lang.*;
/** Consumer class: reads ints from input channel, displays them,
 then
 * terminates when a negative value is read.
 */
public class Consumer2 implements CSProcess
{
    private One2OneChannelInt[] bufferChannels;
    private One2OneChannelInt supervisorChannel;
    private final int id;

    public Consumer2 (final One2OneChannelInt supervisor, One2OneChannelInt[] bufferChannels, int id)
    {
        supervisorChannel = supervisor;
        this.bufferChannels = bufferChannels;
        this.id = id;
    } // constructor
    public void run ()
    {
        boolean running = true;
        while(running) {
            System.out.println("Asking con " + id);
            supervisorChannel.out().write(1);
            System.out.println("Getting answer con " + id);
            int index = supervisorChannel.in().read();
            System.out.println("Got answer con " + id);
            if (index != -1) {
                System.out.println("Communicating with buffer " + index + " con " + id);
                bufferChannels[index].out().write(1);
                System.out.println("Waiting for buffer " + index + " con " + id);
                int item = bufferChannels[index].in().read();
            }
        } // for
        System.out.println("Consumer " + id + " ended.");
    } // run
} // class Consumer2