import org.jcsp.lang.*;
/** Main program class for Producer/Consumer example.
 * Sets up channels, creates processes then
 * executes them in parallel, using JCSP.
 */
public final class PCMain2
{
    public static void main (String[] args)
    {
        new PCMain2(200, 100, 150);
    } // main
    public PCMain2 (int producersNo, int consumersNo, int buffersNo)
    { // Create channel objects
        Buffer[] buffers = new Buffer[buffersNo];
        Consumer2[] consumers = new Consumer2[consumersNo];
        Producer2[] producers = new Producer2[producersNo];
        One2OneChannelInt[][] channelsCon = new One2OneChannelInt[consumersNo][buffersNo];
        One2OneChannelInt[][] channelsPro = new One2OneChannelInt[producersNo][buffersNo];
        int[] sizes = new int[buffersNo];
        for (int i = 0; i<buffersNo; i++){
            One2OneChannelInt[] channelsBufCon = new One2OneChannelInt[consumersNo];
            for (int j = 0; j < consumersNo; j++) {
                One2OneChannelInt chan = Channel.one2oneInt();
                channelsBufCon[j] = chan;
                channelsCon[j][i] = chan;
            }
            One2OneChannelInt[] channelsBufPro = new One2OneChannelInt[producersNo];
            for (int j = 0; j < producersNo; j++) {
                One2OneChannelInt chan = Channel.one2oneInt();
                channelsBufPro[j] = chan;
                channelsPro[j][i] = chan;
            }
            buffers[i] = new Buffer(channelsBufPro, channelsBufCon, i);
            sizes[i] = 5;
        }
        One2OneChannelInt[] channelsSupCon = new One2OneChannelInt[consumersNo];
        for (int i = 0; i < consumersNo; i++) {
            One2OneChannelInt chan = Channel.one2oneInt();
            channelsSupCon[i] = chan;
            consumers[i] = new Consumer2(chan, channelsCon[i], i);
        }
        One2OneChannelInt[] channelsSupPro = new One2OneChannelInt[producersNo];
        for (int i = 0; i < producersNo; i++) {
            One2OneChannelInt chan = Channel.one2oneInt();
            channelsSupPro[i] = chan;
            producers[i] = new Producer2(chan, channelsPro[i], i);
        }
        Supervisor supervisor = new Supervisor(sizes, channelsSupPro, channelsSupCon);
        // Create parallel construct
        CSProcess[] procList = new CSProcess[producersNo + consumersNo + buffersNo + 1]; // Processes
        int iterator = 0;
        for (int i = 0; i<producersNo;i++){
            procList[iterator] = producers[i];
            iterator ++;
        }
        for (int i = 0; i<consumersNo;i++){
            procList[iterator] = consumers[i];
            iterator ++;
        }
        for (int i = 0; i<buffersNo;i++){
            procList[iterator] = buffers[i];
            iterator ++;
        }
        procList[iterator] = supervisor;
        Parallel par = new Parallel(procList); // PAR construct
        par.run(); // Execute processes in parallel
    } // PCMain constructor
} // class PCMain2