package edu.hm.dako.chat.AuditLogServer;

import java.awt.Container;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * 
 * Die Klasse wertet die ChatAuditLog.txt aus
 * 
 *
 */

public class Administration extends JFrame {

	// Zaehler fuer ankommende AuditLog-PDUs
	static long counterAuditlog;
	// Zaehler AuditLogType Login
	static int counterLogin;
	// Zaehler AuditLogType Logout
	static int counterLogout;
	// Zaehler AuditLogType Chat
	static int counterChat;

	// GUI
	static JFrame information;
	// Text für die GUI
	static JLabel auditLogPdus;
	static JLabel logins;
	static JLabel messages;
	static JLabel logouts;
	private static boolean isGuiOpen = false;

	public static void main(String[] args) {
		String fileName = "ChatAuditLog.dat";
		readAuditLog(fileName);
	}

	// die Methode liest Zeile für Zeile die mitgegebene Datei
	static void readAuditLog(String fileName) {
		
		//setzt Zaehler auf null
		counterAuditlog = 0;
		counterLogin = 0;
		counterLogout = 0;
		counterChat = 0;

		File file = new File(fileName);

		// wenn die Datei nicht gelesen oder es kein File ist, wird hier geendet
		if (!file.canRead() || !file.isFile())
			System.exit(0);

		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(fileName));
			String line = null;
			while ((line = in.readLine()) != null) {
				// es wird ueberprueft, ob "AuditLogPdu" in der Zeile steht und der
				// entsprechende Zaehler wird hochgezaehlt
				if (line.contains("AuditLogPdu")) {
					counterAuditlog++;
				}
				// es wird ueberprueft, ob "Login" in der Zeile steht und der entsprechende
				// Zaehler wird hochgezaehlt
				if (line.contains("Login")) {
					counterLogin++;
				}
				// es wird ueberprueft, ob "Chat" in der Zeile steht und der entsprechende
				// Zaehler wird hochgezaehlt
				if (line.contains("Chat")) {
					counterChat++;
				}
				// es wird ueberprueft, ob "Logout" in der Zeile steht und der entsprechende
				// Zaehler wird hochgezaehlt
				if (line.contains("Logout")) {
					counterLogout++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// BufferedReader wird geschlossen
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					System.out.println("BufferedReader konnte nicht geschlossen werden");
				}
		}
		gui();
	}

	// zeigt in einem Fenster die Auswertung an
	private static void gui() {

		if (!isGuiOpen) {
			information = new JFrame("AuditLog Auswertung");

			// setzt die Einstellungen für das Fenster
			information.setLayout(null);
			information.setVisible(true);
			information.setSize(400, 400);
			information.setResizable(false);
			information.setLocationRelativeTo(null);
			information.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			auditLogPdus = new JLabel("Anzahl der AuditLog-PDUs: " + counterAuditlog);
			auditLogPdus.setBounds(5, -30, 400, 100);
			information.add(auditLogPdus);

			logins = new JLabel("Anzahl der Logins: " + counterLogin);
			logins.setBounds(5, -10, 400, 100);
			information.add(logins);

			messages = new JLabel("Anzahl der Chat Nachrichten: " + counterChat);
			messages.setBounds(5, 10, 400, 100);
			information.add(messages);

			logouts = new JLabel("Anzahl der Logouts: " + counterLogout);
			logouts.setBounds(5, 30, 400, 100);
			information.add(logouts);

			isGuiOpen = true;
		} else {
			//aktualisiert den Text im Fenster
			auditLogPdus.setText("Anzahl der AuditLog-PDUs: " + counterAuditlog);
			logins.setText("Anzahl der Logins: " + counterLogin);
			messages.setText("Anzahl der Chat Nachrichten: " + counterChat);
			logouts.setText("Anzahl der Logouts: " + counterLogout);
		}
	}
}
