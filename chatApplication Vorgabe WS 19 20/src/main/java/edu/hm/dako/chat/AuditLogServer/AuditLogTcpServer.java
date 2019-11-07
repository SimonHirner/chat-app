package edu.hm.dako.chat.AuditLogServer;

// Zusätzliche Imports
import edu.hm.dako.chat.common.AuditLogPDU;
import edu.hm.dako.chat.connection.ConnectionTimeoutException;
import edu.hm.dako.chat.connection.EndOfFileException;
import edu.hm.dako.chat.tcp.TcpConnection;
import edu.hm.dako.chat.tcp.TcpServerSocket;

// Zusätzliche Imports für FileWriter
import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 * AuditLog Server fuer die Protokollierung von Chat-Nachrichten eines Chat-Servers. 
 * Implementierung auf Basis von TCP.
 * 
 * @author mandl
 *
 */
public class AuditLogTcpServer {
	private static Logger log = Logger.getLogger(AuditLogTcpServer.class);

	// Serverport fuer AuditLog-Service
	static final int AUDIT_LOG_SERVER_PORT = 40001;

	// Standard-Puffergroessen fuer Serverport in Bytes
	static final int DEFAULT_SENDBUFFER_SIZE = 30000;
	static final int DEFAULT_RECEIVEBUFFER_SIZE = 800000;

	// Name der AuditLog-Datei
	static final String auditLogFile = new String("ChatAuditLog.dat");

	// Zaehler fuer ankommende AuditLog-PDUs
	protected long counter = 0;
	
	// Maximale Wartezeit (ms) auf Nachricht vom ChatServer
	static final int CONNECTION_WAITING_TIME = 100000;

	public static void main(String[] args) {

		PropertyConfigurator.configureAndWatch("log4j.auditLogServer_tcp.properties", 60 * 1000);
		System.out.println("AuditLog-TcpServer gestartet, Port: " + AUDIT_LOG_SERVER_PORT);
		log.info("AuditLog-TcpServer gestartet, Port: " + AUDIT_LOG_SERVER_PORT);

		// Implementierung des AuditLogServers auf TCP-Basis hier ergaenzen
		
		try {
		
			// Server Socket für AuditLogServer erzeugen
			TcpServerSocket auditLogServerSocket = new TcpServerSocket(AUDIT_LOG_SERVER_PORT, DEFAULT_SENDBUFFER_SIZE, DEFAULT_RECEIVEBUFFER_SIZE);
			
			// Verbindung mit ChatServer erzeugen und aufbauen
			TcpConnection auditLogServerConnection = (TcpConnection) auditLogServerSocket.accept();
			
			// FileWriter erzeugen	
            FileWriter fileWriter = new FileWriter("ChatAuditLog.dat");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             
            // Empfangene AuditLogPDUs in AuditLogFile schreiben
            try {
            	
            	AuditLogPDU receivedAuditLogPDU = null;
            	while (!auditLogServerSocket.isClosed()) {
            		receivedAuditLogPDU = (AuditLogPDU) auditLogServerConnection.receive(CONNECTION_WAITING_TIME);
            		bufferedWriter.write(receivedAuditLogPDU.toString());
            }
            
            } catch (ConnectionTimeoutException connectionTimeoutException) {
            	//Beenden wegen Timeout
    			bufferedWriter.close();
    			auditLogServerConnection.close();
    			auditLogServerSocket.close();
    			System.out.println("Timeout - AuditLogServer ordnungsgemaess beendet");
            } catch (EndOfFileException endOfFileException) {
            	//Beenden wegen Verbindungsabbruch
    			bufferedWriter.close();
    			auditLogServerConnection.close();
    			auditLogServerSocket.close();
    			System.out.println("Verbindungsabbruch - AuditLogServer ordnungsgemaess beendet");
            }
            
            // Ordnungsgemaesses beenden
 			bufferedWriter.close();
 			auditLogServerConnection.close();
 			auditLogServerSocket.close();
 			System.out.println("Verbindungsabbruch - AuditLogServer ordnungsgemaess beendet");
			
		} catch (Exception exception) {
			// Andere Fehler
			System.out.println("Schwerwiegender Fehler");
		}
		
	}
}
