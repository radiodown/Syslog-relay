
import java.net.UnknownHostException;

import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;

/**
 * Syslog server.
 *
 * @author Josef Cacek
 */
public class Server {

    public static final int SYSLOG_PORT = 9898;
    public static final String SYSLOG_HOST = "127.0.0.1";
    public static final String SYSLOG_PROTOCOL = "udp";


    public static void main(String[] args) throws SyslogRuntimeException, UnknownHostException {

        System.setProperty("jsse.enableSNIExtension", "false");
        System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
        System.setProperty("sun.security.ssl.allowLegacyHelloMessages", "true");

        SyslogServer.shutdown();

        SyslogServerConfigIF config = new UDPSyslogServerConfig();
        config.setUseStructuredData(true);
        config.setHost(SYSLOG_HOST);
        config.setPort(SYSLOG_PORT);

        // start syslog server
        SyslogServer.createThreadedInstance(SYSLOG_PROTOCOL, config);
    }
}
