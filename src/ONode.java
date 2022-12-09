import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.*;

// Server class
public class ONode extends JFrame implements ActionListener {
    // GUI:
    // ----------------
    JLabel label;

    // RTP variables:
    // ----------------
    DatagramPacket senddp; // UDP packet containing the video frames (to send)A
    DatagramSocket RTPsocket; // socket to be used to send and receive UDP packet
    int RTP_dest_port = 25000; // destination port for RTP packets
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

    public static List<InetAddress> vizinhos = new ArrayList<>();

    public static boolean isContentProvider = false;

    public ONode() {
        // init Frame
        super("Servidor");

        // init para a parte do servidor
        sTimer = new Timer(FRAME_PERIOD, this); // init Timer para servidor
        sTimer.setInitialDelay(0);
        sTimer.setCoalesce(true);
        sBuf = new byte[15000]; // allocate memory for the sending buffer

        try {
            RTPsocket = new DatagramSocket(); // init RTP socket
            // DatagramSocket onodeSocket = new DatagramSocket(ONODE_PORT);
            video = new VideoStream(VideoFileName); // init the VideoStream object:
            System.out.println("Servidor: vai enviar video da file " + VideoFileName);

        } catch (SocketException e) {
            System.out.println("Servidor: erro no socket: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Servidor: erro no video: " + e.getMessage());
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
        label = new JLabel("Send frame #        ", JLabel.CENTER);
        getContentPane().add(label, BorderLayout.CENTER);

        sTimer.start();
    }

    public static void main(String[] args) throws IOException {
        parseArguments(args);
        byte[] buf = new byte[15000];
        if (isContentProvider) {
            new ONode();
        } else {
            try {
                DatagramSocket socket = new DatagramSocket(25000);
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buf, 15000);
                    socket.receive(packet);
                    Thread t = new Thread(new VideoForwarder(vizinhos,socket,packet));
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
                Thread videoForwarder = new Thread(new VideoForwarder(vizinhos, RTPsocket, senddp));
                videoForwarder.start();
                //System.out.println("Send frame #" + imagenb);
                // print the header bitstream
                //rtp_packet.printheader();

                // update GUI
                // label.setText("Send frame #" + imagenb);
            } catch (Exception ex) {
                System.out.println("Exception caught: " + ex);
                System.exit(0);
            }
        } else {
            // if we have reached the end of the video file, stop the timer
            sTimer.stop();
        }
    }

    public static void parseArguments(String[] args) throws UnknownHostException {
        if (args.length == 0) {
            System.out.println("Syntax : ONode [-f file] [neighbour ipaddresses]");
            System.exit(1);
        }

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-f")) {
                if (args.length - 1 >= i + 1) {
                    VideoFileName = args[i + 1];
                    System.out.println("File to send: " + VideoFileName);
                    i += 2;
                    File f = new File(VideoFileName);
                    if (f.exists()) {
                        // Create a Main object
                        ONode s = new ONode();
                        // show GUI: (opcional!)
                        s.pack();
                        s.setVisible(true);
                        isContentProvider = true;
                    } else {
                        System.err.println("[ERROR] File: " + VideoFileName + " not found");
                        // System.exit(1);
                    }
                } else {
                    System.err.println("[ERROR] Syntax error: No file specified with -f flag");
                    System.out.println("Syntax : ONode [-f file] [neighbour ipaddresses]");
                    System.exit(1);
                }
            } else {
                vizinhos.add(InetAddress.getByName(args[i]));
            }
        }

        System.out.println("Número de vizinhos: " + vizinhos.size());
        int index = 1;
        for (InetAddress vizinho : vizinhos) {
            System.out.println(index + "º vizinho: " + vizinho);
            index++;
        }
    }
}
