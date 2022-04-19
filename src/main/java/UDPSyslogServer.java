import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.server.impl.net.udp.UDPNetSyslogServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class UDPSyslogServer extends UDPNetSyslogServer {

    @Override
    public void shutdown() {
        super.shutdown();
        thread = null;
    }

    @Override
    public void run() {
        this.shutdown = false;
        try {
            this.ds = createDatagramSocket();
        } catch (Exception e) {
            System.err.println("Creating DatagramSocket failed");
            e.printStackTrace();
            throw new SyslogRuntimeException(e);
        }

        byte[] receiveData = new byte[SyslogConstants.SYSLOG_BUFFER_SIZE];

        while (!this.shutdown) {
            try {
                final DatagramPacket dp = new DatagramPacket(receiveData, receiveData.length);
                this.ds.receive(dp);

                String text = new String(dp.getData(), 0, dp.getLength(), StandardCharsets.UTF_8);
                int facility = -1;
                int severity = -1;

                int brace = text.indexOf('>');

                if (text.charAt(0) == '<' && brace > 0 && brace <= 5) {
                    int pri = Integer.parseInt(text.substring(1, brace));
                    facility = pri / 8;
                    severity = pri % 8;
                    text = text.substring(brace + 1);
                }

                InetSocketAddress remote = new InetSocketAddress(dp.getAddress(), dp.getPort());
                SyslogBean syslog = new SyslogBean(new Date(), remote, facility, severity, text);
                syslog.setLocalAddress((InetSocketAddress) dp.getSocketAddress());

                System.out.println(">>> SYSLOG: " + syslog.toString());


//                SyslogConfigIF syslogConfigIF =  new UDPNetSyslogConfig("127.0.0.1", 515);
//                syslogConfigIF.setFacility(14);
//                SyslogIF syslogIF =  org.productivity.java.syslog4j.Syslog.createInstance("test2", syslogConfigIF);
//                syslogIF.info("Today is a good day!");

                SyslogIF client = Syslog.getInstance("udp");
                client.getConfig().setHost("127.0.0.1");
                client.getConfig().setPort(9898);
                client.info("Today is a good day! - ");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
