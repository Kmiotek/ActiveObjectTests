import org.jcsp.lang.AltingChannelInputInt;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.One2OneChannelInt;

public class Consumer implements CSProcess
{
    private One2OneChannelInt channel;
    public Consumer (final One2OneChannelInt in) {
        channel = in;
    } // constructor
    public void run () {
        AltingChannelInputInt c_in = channel.in();
        int item = c_in.read();
        System.out.println(item);
    } // run
} // class Consumer