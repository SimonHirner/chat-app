package edu.hm.dako.chat.AuditLogServer;

import edu.hm.dako.chat.common.AuditLogPDU;
import edu.hm.dako.chat.connection.ConnectionTimeoutException;
import edu.hm.dako.chat.connection.EndOfFileException;
import edu.hm.dako.chat.tcp.TcpConnection;
import edu.hm.dako.chat.tcp.TcpServerSocket;
import edu.hm.dako.chat.udp.UdpServerConnection;
import edu.hm.dako.chat.udp.UdpServerSocket;
import javafx.concurrent.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class AuditLogUdpServer implements AuditLogServerInterface{

	private static Logger log = Logger.getLogger(AuditLogUdpServer.class);

	// UDP-Serverport fuer AuditLog-Service
	static int AUDIT_LOG_SERVER_PORT;

	// Standard-Puffergroessen fuer Serverport in Bytes
	static int DEFAULT_SENDBUFFER_SIZE;
	static int DEFAULT_RECEIVEBUFFER_SIZE;

	// Name der AuditLog-Datei
	static final String auditLogFile = new String("ChatAuditLog.dat");

	// Zaehler fuer ankommende AuditLog-PDUs
	protected long counter = 0;
	
	//Serversocket
	private UdpServerSocket auditLogServerSocket;
	
	//Serververbindung
	private UdpServerConnection auditLogServerConnection;
	
	//File-Writer
	FileWriter fileWriter;
	BufferedWriter bufferedWriter;
	
	//Maximale Wartezeit (ms) auf Nachricht vom ChatServer
	static final int CONNECTION_WAITING_TIME = 150000;
	
	//Konstruktor
    public AuditLogUdpServer(int serverPort, int sendBufferSize, int receiveBuffersize) {
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
	    PropertyConfigurator.configureAndWatch("log4j.auditLogServer_udp.properties", 60 * 1000);
        System.out.println("AuditLog-UdpServer gestartet, Port: " + AUDIT_LOG_SERVER_PORT);
        log.info("AuditLog-UdpServer gestartet, Port: " + AUDIT_LOG_SERVER_PORT);
        
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
        
        try {
            //TODO: Implementierung des AuditLogServers auf UDP-Basis hier ergaenzen
        
            //Server Socket fï¿½r AuditLogServer erzeugen
            auditLogServerSocket = new UdpServerSocket(AUDIT_LOG_SERVER_PORT, DEFAULT_SENDBUFFER_SIZE, DEFAULT_RECEIVEBUFFER_SIZE);
        
            //Verbindung mit ChatServer erzeugen und aufbauen
            auditLogServerConnection = (UdpServerConnection) auditLogServerSocket.accept();
            
            //FileWriter erzeugen   
            fileWriter = new FileWriter("ChatAuditLog.txt", true);
            bufferedWriter = new BufferedWriter(fileWriter);

            //Kopf von AuditLogFile erstellen
            bufferedWriter.write("-------- AuditLog: --------");
            bufferedWriter.newLine();
            
            //Empfangene AuditLogPDUs in AuditLogFile schreiben
            AuditLogPDU receivedAuditLogPDU = null;
            while(!Thread.currentThread().isInterrupted() && !auditLogServerSocket.isClosed()) {
                try {
                receivedAuditLogPDU = (AuditLogPDU) auditLogServerConnection.receive(CONNECTION_WAITING_TIME);
                bufferedWriter.write(receivedAuditLogPDU.toString());
                bufferedWriter.flush();
                } catch (EndOfFileException endOfFileException) {
                    //Verbindugnsabbruch
                    System.out.println("Verbindungsabbruch");
                    //Neue Verbindung suchen
                    auditLogServerConnection = (UdpServerConnection) auditLogServerSocket.accept();
                } catch (ConnectionTimeoutException connectionTimeoutException) {
                    //Timeout
                    System.out.println("Verbindugnsabbruch");
                    //Neue Verbindung suchen
                    auditLogServerConnection = (UdpServerConnection) auditLogServerSocket.accept();
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
	
	public void end() throws Exception {}

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