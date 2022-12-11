import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPUtils {

    /**
     * Compute network address from IP and mask
     * @param ip
     * @param mask
     * @return
     * @throws UnknownHostException
     */
    public static InetAddress computeNetworkAddress(InetAddress ip,
            InetAddress mask) throws UnknownHostException {
        int addr = InetAddressToInt(ip);
        int maskInt = InetAddressToInt(mask);
        return InetAddress.getByName(IpToString(addr & maskInt));
    }

    /** Convert ip to an integer
     * @param ip InetAddress to convert
     * @return Integer
     */
    public static int InetAddressToInt(InetAddress ip) {
        if (ip == null)
            return -1;
        byte[] adr = ip.getAddress();

        int[] i = new int[4];
        for (int j = 0; j < 4; j++) {
            i[j] = (int) ((adr[j] < 0) ? (256 + adr[j]) : adr[j]);
        }
        return i[3] + (i[2] << 8) + (i[1] << 16) + (i[0] << 24);
    }

    /**
     * Convert an IP address stored in an int to its string representation.
     * @param address
     * @return
     */
    public static String IpToString(int address) {
        StringBuffer sa = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            sa.append(0xff & address >> 24);
            address <<= 8;
            if (i != 4 - 1)
                sa.append('.');
        }
        return sa.toString();
    }
}