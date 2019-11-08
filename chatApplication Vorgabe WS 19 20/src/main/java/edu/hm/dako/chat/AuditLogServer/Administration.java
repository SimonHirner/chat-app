package edu.hm.dako.chat.AuditLogServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * Die Klasse wertet die ChatAuditLog.txt aus
 * 
 * @author Armin Lewig
 *
 */

public class Administration {
	
	private static void ladeDatei(String fileName) {	
		// Zaehler fuer ankommende AuditLog-PDUs
		long counterAuditlog = 0;	
	
		//Zaehler AuditLogType Login
		int counterLogin = 0;
		
		//Zaehler AuditLogType Logout
		int counterLogout = 0;
		
		//Zaehler AuditLogType Chat
		int counterChat = 0;
		

        File file = new File(fileName);

        //wenn die Datei nicht gelesen oder es kein File ist, wird hier geendet
        if (!file.canRead() || !file.isFile())
            System.exit(0);

            BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(fileName));
            String line = null;
            while ((line = in.readLine()) != null) {
            	//es wird ueberprueft, ob "AuditLogPdu" in der Zeile steht und der entsprechende Zaehler wird hochgezaehlt
            	if(line.contains("AuditLogPdu")) {
            		counterAuditlog++;
            	}
            	//es wird ueberprueft, ob "Login" in der Zeile steht und der entsprechende Zaehler wird hochgezaehlt
            	if(line.contains("Login")) {
            		counterLogin++;
            	}
            	//es wird ueberprueft, ob "Chat" in der Zeile steht und der entsprechende Zaehler wird hochgezaehlt
            	if(line.contains("Chat")) {
            		counterChat++;
            	}
            	//es wird ueberprueft, ob "Logout" in der Zeile steht und der entsprechende Zaehler wird hochgezaehlt
            	if(line.contains("Logout")) {
            		counterLogout++;
            	}
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	//BufferedReader wird geschlossen
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                	System.out.println("BufferedReader konnte nicht geschlossen werden");
                }
        }
        System.out.println("Anzahl der AuditLog-PDUs: " + counterAuditlog);
        System.out.println("Anzahl der Logins: " + counterLogin);
        System.out.println("Anzahl der Chat Nachrichten: " + counterChat);
        System.out.println("Anzahl der Logouts: " + counterLogout);
    }
	
	
	public static void main(String[] args) {
        String fileName = "ChatAuditLog.txt";
        ladeDatei(fileName);
    }
}
