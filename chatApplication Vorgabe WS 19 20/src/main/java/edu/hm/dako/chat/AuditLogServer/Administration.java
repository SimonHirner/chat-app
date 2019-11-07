package edu.hm.dako.chat.AuditLogServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Administration {
	
	private static void ladeDatei(String datName) {
		
		
		// Zaehler fuer ankommende AuditLog-PDUs
		long counterAuditlog = 0;	
		
		// alle Benutzernamen
		ArrayList<String> userNames = new ArrayList<>();
		
		//Zaehler AuditLogType Login
		int counterLogin = 0;
		
		//Zaehler AuditLogType Logout
		int counterLogout = 0;
		
		//Zaehler AuditLogType Chat
		int counterChat = 0;
		

        File file = new File(datName);

        if (!file.canRead() || !file.isFile())
            System.exit(0);

            BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(datName));
            String zeile = null;
            while ((zeile = in.readLine()) != null) {
            	if(zeile.contains("AuditLogPdu")) {
            		counterAuditlog++;
            	}
            	if(zeile.contains("Login")) {
            		counterLogin++;
            	}
            	if(zeile.contains("Chat")) {
            		counterChat++;
            	}
            	if(zeile.contains("Logout")) {
            		counterLogout++;
            	}
//                System.out.println(zeile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }
        }
        System.out.println("Anzahl der AuditLog-PDUs: " + counterAuditlog);
        System.out.println("Anzahl der Logins: " + counterLogin);
        System.out.println("Anzahl der Chat Nachrichten: " + counterChat);
        System.out.println("Anzahl der Logouts: " + counterLogout);
    }
	
	
	public static void main(String[] args) {
        String dateiName = "ChatAuditLog.txt";
        ladeDatei(dateiName);
    }
}
