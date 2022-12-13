import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.*;

public class ONode extends JFrame implements ActionListener {
    
    /* GUI */
    JLabel label;

    // RTP variables:
    // ----------------
    DatagramPacket senddp; // UDP packet containing the video frames (to send)A
    DatagramSocket socket; // socket to be used to send and receive UDP packet
    public static int RTP_dest_port = 25000; // destination port for RTP packets
    InetAddress ClientIPAddr; // Client IP address

    static String VideoFileName; // video file to request to the server

    // Video constants:
    // ------------------
    int imagenb = 0; // image nb of the image currently transmitted
    VideoStream video; // VideoStream object used to access video frames
    static int MJPEG_TYPE = 26; // RTP payload type for MJPEG video
    static int FRAME_PERIOD = 100; // Frame period of the video to stream, in ms
    static int VIDEO_LENGTH = 500; // length of the video in frames

    Timer sTimer; // timer used to send the images at the video frame rate
    byte[] sBuf; // buffer used to store the images to send to the client
    static int BUFFER_SIZE = 15000; //Buffer size

    /* The list of neighbours read from the command line */
    public static List<InetAddress> neighbours = new ArrayList<>();

    /* Tells if the current ONode acts as a stream server */
    public static boolean isContentProvider = false;

    /* Tells if the current ONode acts as client */
    public static boolean isClient = false;

    public ONode() {
        // init Frame
        super("Servidor");

        // init para a parte do servidor
        sTimer = new Timer(FRAME_PERIOD, this); 
        sTimer.setCoalesce(true);
        sBuf = new byte[BUFFER_SIZE];

        try {
            socket = new DatagramSocket(); 
            video = new VideoStream(VideoFileName);
            System.out.println("[RTP SERVER]: File to send: " + VideoFileName);
        } catch (SocketException e) {
            System.out.println("[RTP SERVER]: Socket error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[RTP SERVER]: Video error: " + e.getMessage());
            
        }

        // Handler to close the main window
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // stop the timer and exit
                sTimer.stop();
                System.exit(0);
            }
        });

        // GUI:
        label = new JLabel("Send frame #            ", JLabel.CENTER);
        getContentPane().add(label, BorderLayout.CENTER);

        sTimer.start();

        
    }

    public static void main(String[] args) throws IOException {
        parseArguments(args);

        /* Start monitoring the network and exchanging routing tables */
        Thread networkMonitorListener = new Thread(new NetworkMonitorListener());
        networkMonitorListener.start();   
        Thread networkMonitor = new Thread(new NetworkMonitor(neighbours));
        networkMonitor.start(); 

        byte[] buf = new byte[BUFFER_SIZE];
        DatagramSocket socket = new DatagramSocket(RTP_dest_port);
        if(ONode.isClient){
            Thread client = new Thread(new Client(neighbours,socket));
            client.start();
        }
        if (isContentProvider) {
            ONode o = new ONode();
            o.pack();
            o.setVisible(true);
        } else {
            try {
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    Thread t = new Thread(new VideoForwarder(neighbours,socket,packet));
                    t.start();
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // if the current image nb is less than the length of the video
        if (imagenb < VIDEO_LENGTH) {
            // update current imagenb

            imagenb++;


            try {
                // get next frame to send from the video, as well as its size
                int image_length = video.getnextframe(sBuf);

                // Builds an RTPpacket object containing the frame
                RTPpacket rtp_packet = new RTPpacket(MJPEG_TYPE, imagenb, imagenb * FRAME_PERIOD, sBuf, image_length);

                // get to total length of the full rtp packet to send
                int packet_length = rtp_packet.getlength();

                // retrieve the packet bitstream and store it in an array of bytes
                byte[] packet_bits = new byte[packet_length];
                
                rtp_packet.getpacket(packet_bits);
                
                senddp = new DatagramPacket(packet_bits, packet_length, InetAddress.getByName("127.0.0.1") , RTP_dest_port);
             
                Thread videoForwarder = new Thread(new VideoForwarder(neighbours, socket, senddp));
                videoForwarder.start();
                
                // print the header bitstream
                //rtp_packet.printheader();

                // update GUsocketI
                label.setText("Send frame #" + imagenb);
            } catch (Exception ex) {
                System.out.println("Exception caught: " + ex);
                System.exit(0);
            }
        } else {
            sTimer.stop();
        }
    }

    /**
     * Parse the command line arguments 
     * @param args The arguments read
     * @throws IOException
     */
    public static void parseArguments(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Syntax : ONode [-c] [-f file] [neighbour ipaddresses]");
            System.out.println("        -c: client receive video");
            System.out.println("        -f file: file to stream");
            System.exit(1);
        }
        System.out.println(args.length);

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-f")) {
                if (args.length - 1 >= i + 1) {
                    VideoFileName = args[i + 1];
                    i += 1;
                    File f = new File(VideoFileName);
                    if (f.exists()) {
                        isContentProvider = true;
                    } else {
                        System.err.println("[ERROR] File: " + VideoFileName + " not found");
                        System.exit(1);
                    }
                } else {
                    System.err.println("[ERROR] Syntax error: No file specified with -f flag");
                    System.out.println("Syntax : ONode [-f file] [neighbour ipaddresses]");
                    System.exit(1);
                }
            } else {
                if(args[i].equals("-c")){
                    ONode.isClient = true;
                } else {
                   neighbours.add(InetAddress.getByName(args[i])); 
                }
            }
        }

        System.out.println("Number of neighbours: " + neighbours.size());
        int index = 1;
        for (InetAddress neighbour  : neighbours) {
            System.out.println(index + "º: " + neighbour);
            index++;
        }
    }
}
