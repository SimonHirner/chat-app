package edu.hm.dako.chat.AuditLogServer;

import edu.hm.dako.chat.common.AuditLogPDU;
import edu.hm.dako.chat.connection.ConnectionTimeoutException;
import edu.hm.dako.chat.connection.EndOfFileException;
import edu.hm.dako.chat.tcp.TcpConnection;
import edu.hm.dako.chat.tcp.TcpServerSocket;
import javafx.concurrent.Task;

// Imports fuer FileWriter
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.SocketException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 * AuditLog Server fuer die Protokollierung von Chat-Nachrichten eines Chat-Servers. 
 * Implementierung auf Basis von TCP.
 * 
 * @author mandl
 *
 */
public class AuditLogTcpServer implements AuditLogServerInterface {
	private static Logger log = Logger.getLogger(AuditLogTcpServer.class);

	// Serverport fuer AuditLog-Service
	static int AUDIT_LOG_SERVER_PORT;

	// Standard-Puffergroessen fuer Serverport in Bytes
	static int DEFAULT_SENDBUFFER_SIZE;
	static int DEFAULT_RECEIVEBUFFER_SIZE;

	// Zaehler fuer ankommende AuditLog-PDUs
	protected long counter = 0;
	
	// Server Socket
	private TcpServerSocket auditLogServerSocket;
	
	// ChatServer Verbindung
	private TcpConnection auditLogServerConnection;
	
	// Filewriter
	private FileWriter fileWriter;
	private BufferedWriter bufferedWriter;
	
	// Maximale Wartezeit (ms) auf Nachricht vom ChatServer
	static final int CONNECTION_WAITING_TIME = 500000;
	
	/**
	 * Konstruktor.  
	 * 
	 * @param serverPort
	 * @param sendBufferSize
	 * @param receiveBuffersize
	 */
	public AuditLogTcpServer(int serverPort, int sendBufferSize, int receiveBuffersize) {
	    AUDIT_LOG_SERVER_PORT = serverPort;
	    DEFAULT_SENDBUFFER_SIZE = sendBufferSize;
	    DEFAULT_RECEIVEBUFFER_SIZE = receiveBuffersize;
	        
	    // Alte AuditLog-Datei wird gelöscht
	    File auditLogFile = new File("ChatAuditLog.dat");
	    try {
	    	if(auditLogFile.exists()) {
	    		auditLogFile.delete();
	            auditLogFile.createNewFile();
	        } else {
	        	auditLogFile.createNewFile();
	        }
	    } catch (Exception e) {
	    	// Fehler beim Löschen/Erstellen der AuditLog-Datei
	    	System.out.print("Fehler bei AuditLog-Datei");
	    }    
	}
	
	/**
	 * Starten des AuditLog-Servers. Implementierung des AuditLogServers auf TCP-Basis.
	 */
	public void start() {
	    
	    PropertyConfigurator.configureAndWatch("log4j.auditLogServer_udp.properties", 60 * 1000);
        System.out.println("AuditLog-TcpServer gestartet, Port: " + AUDIT_LOG_SERVER_PORT);
        log.info("AuditLog-TcpServer gestartet, Port: " + AUDIT_LOG_SERVER_PORT);
        
        // Task der in einem Thread von der Benutzeroberfläche gestartet wird
        Task<Void> task = new Task<Void>() {
        	@Override
        	protected Void call() {
                    
        		try {                 
        			// Server Socket fuer AuditLogServer erzeugen
        			auditLogServerSocket = new TcpServerSocket(AUDIT_LOG_SERVER_PORT, DEFAULT_SENDBUFFER_SIZE, DEFAULT_RECEIVEBUFFER_SIZE);
                    
        			// Verbindung mit ChatServer erzeugen und aufbauen
        			auditLogServerConnection = (TcpConnection) auditLogServerSocket.accept();
                        
        			// FileWriter erzeugen   
        			fileWriter = new FileWriter("ChatAuditLog.dat");
        			bufferedWriter = new BufferedWriter(fileWriter);

        			AuditLogPDU receivedAuditLogPDU = null;      			
        			while (!Thread.currentThread().isInterrupted() && !auditLogServerSocket.isClosed()) {
        				try {
        					// AuditLogPDUs empfangen und in AuditLog-Datei schreiben
        					receivedAuditLogPDU = (AuditLogPDU) auditLogServerConnection.receive(CONNECTION_WAITING_TIME);
        					bufferedWriter.write(receivedAuditLogPDU.toString());
        					bufferedWriter.flush();
        					// Auswertung starten
        					AuditLogStatistic.readAuditLog("ChatAuditLog.dat");
        					AuditLogStatistic.statisticGui();
        				} catch (EndOfFileException endOfFileException) {
        					// Verbindugnsabbruch
        					System.out.println("Verbindungsabbruch");
        					// Neue Verbindung suchen
        					auditLogServerConnection = (TcpConnection) auditLogServerSocket.accept();
        				} catch (ConnectionTimeoutException connectionTimeoutException) {
        					// Timeout
        					System.out.println("Verbindugnsabbruch");
        					// Neue Verbindung suchen
        					auditLogServerConnection = (TcpConnection) auditLogServerSocket.accept();
        				}
        			}
             
        		} catch (SocketException socketException) {			
        			// Socket geschlossen
        		} catch (Exception exception) {
        			// Andere Exceptions behandeln
        			System.out.println("Schwerwiegender Fehler");
        		}
        		return null;
        	}     
        };
        
        // Thread starten, der den Task abarbeitet
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
	}

	/**
	 * Beenden des AuditLog-Servers 
	 */
    @Override
    public void stop() {
        try {
        	// FileWriter beenden
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }
            
            if(fileWriter != null) {
            	fileWriter.close();
            }
            
            // Connection abbauen
            if(auditLogServerConnection != null) {
            	auditLogServerConnection.close();
            }
            
            // Socket schliessen
            auditLogServerSocket.close();
            
            // Thread beenden
            Thread.currentThread().interrupt();
            
            System.out.println("AuditLogServer wurde ordnungsgemaess beednet.");
        } catch (Exception exception) {
                // Fehler beim Beenden
                System.out.println("AuditLogServer konnte nicht ordnungsgemaess beendet werden.");
        }
    }
}