package edu.hm.dako.chat.AuditLogServer;

import edu.hm.dako.chat.common.AuditLogPDU;

//Zus�tzliche Imports
import edu.hm.dako.chat.connection.EndOfFileException;
import edu.hm.dako.chat.tcp.TcpConnection;
import edu.hm.dako.chat.tcp.TcpServerSocket;

//Zus�tzliche Imports f�r FileWriter
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

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
	static final String auditLogFile = new String("ChatAuditLog.txt");

	// Zaehler fuer ankommende AuditLog-PDUs
	static long counterAuditlog = 0;	
	
	// alle Benutzernamen
	static ArrayList<String> userNames = new ArrayList<>();
	
	//Zaehler AuditLogType Login
	static int counterLogin = 0;
	
	//Zaehler AuditLogType Logout
	static int counterLogout = 0;
	
	//Zaehler AuditLogType Chat
	static int counterChat = 0;
	
	
	public static void main(String[] args) {

		PropertyConfigurator.configureAndWatch("log4j.auditLogServer_tcp.properties", 60 * 1000);
		System.out.println("AuditLog-TcpServer gestartet, Port: " + AUDIT_LOG_SERVER_PORT);
		log.info("AuditLog-TcpServer gestartet, Port: " + AUDIT_LOG_SERVER_PORT);

		//TODO: Implementierung des AuditLogServers auf TCP-Basis hier ergaenzen
		
		try {
			//Server Socket f�r AuditLogServer erzeugen
			TcpServerSocket auditLogServerSocket = new TcpServerSocket(AUDIT_LOG_SERVER_PORT, DEFAULT_SENDBUFFER_SIZE, DEFAULT_RECEIVEBUFFER_SIZE);
			//Verbindung mit ChatServer erzeugen und aufbauen
			TcpConnection auditLogServerConnection = (TcpConnection) auditLogServerSocket.accept();
			
			//FileWriter erzeugen	
            FileWriter fileWriter = new FileWriter("ChatAuditLog.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            //Kopf von AuditLogFile erstellen
            bufferedWriter.write("-------- AuditLog: --------");
            bufferedWriter.newLine();
             
            //Empfangene AuditLogPDUs in AuditLogFile schreiben
            try {
            	AuditLogPDU receivedAuditLogPDU = null;
            	do {
            		receivedAuditLogPDU = (AuditLogPDU) auditLogServerConnection.receive(100000);
            		bufferedWriter.write(receivedAuditLogPDU.toString());
            		counterAuditlog++;
            		
            		//wenn sich der Benutzername noch nicht in der Liste befindet, wird dieser hinzugef�gt
            		if(!userNames.contains(receivedAuditLogPDU.getUserName())) {
            			userNames.add(receivedAuditLogPDU.getUserName());
            		}
            		  
            		//je nachdem welcher AuditLogTyp vorliegt wird der entsprechende Zaehler hochgezaehlt
            		if(receivedAuditLogPDU.getPduType().toString() == "Login ") {
            			counterLogin++;
            		} else if(receivedAuditLogPDU.getPduType().toString() == "Logout") {
            			counterLogout++;
            		} else if(receivedAuditLogPDU.getPduType().toString() == "Chat  ") {
            			counterChat++;
            		}
            		
            	} while (receivedAuditLogPDU != null);
            } catch (EndOfFileException e) {
            	//Ordnungsgemaesses Beenden wegen Verbindungsabruch
    			bufferedWriter.close();
    			auditLogServerConnection.close();
    			auditLogServerSocket.close();
            }
            
            //Ordnungsgem��es Beenden wegen Timeout
			bufferedWriter.close();
			auditLogServerConnection.close();
			auditLogServerSocket.close();
			
		} catch (Exception exception) {
			//Andere Exceptions behandeln
			System.out.println("Schwerwiegender Fehler!");
		}
		
		// oeffnet das Informationsfenster
		new AdministrationGuiForTcp();
		
	}
}
