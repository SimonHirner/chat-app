package edu.hm.dako.chat.AuditLogServer;

import edu.hm.dako.chat.common.AuditLogPDU;
import edu.hm.dako.chat.connection.ConnectionTimeoutException;
//Zusï¿½tzliche Imports
import edu.hm.dako.chat.connection.EndOfFileException;
import edu.hm.dako.chat.tcp.TcpConnection;
import edu.hm.dako.chat.tcp.TcpServerSocket;
import javafx.concurrent.Task;

//Zusï¿½tzliche Imports fï¿½r FileWriter
import java.io.BufferedWriter;
import java.io.File;
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
public class AuditLogTcpServer implements AuditLogServerInterface {
	private static Logger log = Logger.getLogger(AuditLogTcpServer.class);

	// Serverport fuer AuditLog-Service
	static int AUDIT_LOG_SERVER_PORT;

	// Standard-Puffergroessen fuer Serverport in Bytes
	static int DEFAULT_SENDBUFFER_SIZE;
	static int DEFAULT_RECEIVEBUFFER_SIZE;

	// Name der AuditLog-Datei
	static final String auditLogFile = new String("ChatAuditLog.txt");

	// Zaehler fuer ankommende AuditLog-PDUs
	protected long counter = 0;
	
	//Server Socket
	private TcpServerSocket auditLogServerSocket;
	
	//ChatServer Verbindung
	private TcpConnection auditLogServerConnection;
	
	//Filewriter
	private FileWriter fileWriter;
	private BufferedWriter bufferedWriter;
	
	//Konstruktor
	public AuditLogTcpServer(int serverPort, int sendBufferSize, int receiveBuffersize) {
	        AUDIT_LOG_SERVER_PORT = serverPort;
	        DEFAULT_SENDBUFFER_SIZE = sendBufferSize;
	        DEFAULT_RECEIVEBUFFER_SIZE = receiveBuffersize;
	        
	        File auditLogFile = new File("ChatAuditLog.dat");
	        // Falls alte AuditLog-Datei vorhanden ist, wird diese gelöscht
            if(auditLogFile.exists()) {
                auditLogFile.delete();
            }
	}
	
	public void start() {
	    
	    PropertyConfigurator.configureAndWatch("log4j.auditLogServer_tcp.properties", 60 * 1000);
        System.out.println("AuditLog-TcpServer gestartet, Port: " + AUDIT_LOG_SERVER_PORT);
        log.info("AuditLog-TcpServer gestartet, Port: " + AUDIT_LOG_SERVER_PORT);
            
        Task<Void> task = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
                    
            try {
                //TODO: Implementierung des AuditLogServers auf TCP-Basis hier ergaenzen
                    
                //Server Socket fï¿½r AuditLogServer erzeugen
                auditLogServerSocket = new TcpServerSocket(AUDIT_LOG_SERVER_PORT, DEFAULT_SENDBUFFER_SIZE, DEFAULT_RECEIVEBUFFER_SIZE);
                    
                //Verbindung mit ChatServer erzeugen und aufbauen
                auditLogServerConnection = (TcpConnection) auditLogServerSocket.accept();
                        
                //FileWriter erzeugen   
                fileWriter = new FileWriter("ChatAuditLog.txt");
                bufferedWriter = new BufferedWriter(fileWriter);

                //Kopf von AuditLogFile erstellen
                bufferedWriter.write("-------- AuditLog: --------");
                bufferedWriter.newLine();
                
                //Empfangene AuditLogPDUs in AuditLogFile schreiben
                AuditLogPDU receivedAuditLogPDU = null;
                    
                while (!Thread.currentThread().isInterrupted() && !auditLogServerSocket.isClosed()) {
            
                    try {
                        receivedAuditLogPDU = (AuditLogPDU) auditLogServerConnection.receive(100000);
                        bufferedWriter.write(receivedAuditLogPDU.toString());
                        bufferedWriter.flush();
                    } catch (EndOfFileException endOfFileException) {
                        //Verbindugnsabbruch
                        System.out.println("Verbindungsabbruch");
                        //Neue Verbindung suchen
                        auditLogServerConnection = (TcpConnection) auditLogServerSocket.accept();
                    } catch (ConnectionTimeoutException connectionTimeoutException) {
                    //Timeout
                    System.out.println("Verbindugnsabbruch");
                    //Neue Verbindung suchen
                    auditLogServerConnection = (TcpConnection) auditLogServerSocket.accept();
                    }
                }
             
                     
            } catch (Exception exception) {
                //Andere Exceptions behandeln
                System.out.println("Schwerwiegender Fehler!");
            }
            
            return null;
        }
            
	};
	Thread th = new Thread(task);
    th.setDaemon(true);
    th.start();
	}

    @Override
    public void stop() throws Exception {
        try {
            
            //Ordnungsgemäßes beenden 
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }
            auditLogServerSocket.close();
            
            if(auditLogServerConnection != null) {
            auditLogServerConnection.close();
            }
            
            Thread.currentThread().interrupt();
            
            System.out.println("AuditLogServer wurde ordnungsgemäß beednet.");
            } catch (Exception exception) {
                // Fehler beim Beenden
                System.out.println("AuditLogServer konnte nicht ordnungsgemäß beendet werden.");
            }
    }
}
