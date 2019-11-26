package edu.hm.dako.chat.AuditLogServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * 
 * Die Klasse wertet die ChatAuditLog.txt aus und zeigt das Ergebnis in einem
 * Fenster an
 *
 */

public class AuditLogStatistic extends JFrame {

	private static final long serialVersionUID = 2271636673918984337L;
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
	// gibt an, ob die GUI schon geöffnet ist
	static boolean isGuiOpen = false;

	// die Methode liest Zeile für Zeile die mitgegebene Datei
	static void readAuditLog(String fileName) {

		// setzt Zaehler auf null
		counterAuditlog = 0;
		counterLogin = 0;
		counterLogout = 0;
		counterChat = 0;

		File file = new File(fileName);

		// wenn die Datei nicht gelesen werden kann oder es kein File ist, wird hier
		// beendet
		if (!file.canRead() || !file.isFile()) {
			System.exit(0);
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String line = null;
			while ((line = reader.readLine()) != null) {
				// es wird ueberprueft, ob "AuditLogPdu" in der Zeile steht und der
				// entsprechende Zaehler wird bei ja hochgezaehlt
				if (line.contains("AuditLogPdu")) {
					counterAuditlog++;
				}
				// es wird ueberprueft, ob "Login" in der Zeile steht und der entsprechende
				// Zaehler wird bei ja hochgezaehlt
				if (line.contains("Login")) {
					counterLogin++;
				}
				// es wird ueberprueft, ob "Chat" in der Zeile steht und der entsprechende
				// Zaehler wird bei ja hochgezaehlt
				if (line.contains("Chat")) {
					counterChat++;
				}
				// es wird ueberprueft, ob "Logout" in der Zeile steht und der entsprechende
				// Zaehler wird bei ja hochgezaehlt
				if (line.contains("Logout")) {
					counterLogout++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// BufferedReader wird geschlossen
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					System.out.println("BufferedReader konnte nicht geschlossen werden");
				}
		}
		statisticGui();
	}

	// zeigt in einem Fenster die Auswertung an
	static void statisticGui() {

		// wenn das Fenster noch nicht geoeffnet ist, wird ein neues erstellt
		if (!isGuiOpen) {
			information = new JFrame("AuditLog Auswertung");

			// setzt die Einstellungen für das Fenster
			information.setLayout(null);
			information.setVisible(false);
			information.setSize(300, 200);
			information.setResizable(false);
			information.setLocationRelativeTo(null);
			information.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

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
			// ist das Fenster schon geoeffnet, wird der Text aktualisiert
			auditLogPdus.setText("Anzahl der AuditLog-PDUs: " + counterAuditlog);
			logins.setText("Anzahl der Logins: " + counterLogin);
			messages.setText("Anzahl der Chat Nachrichten: " + counterChat);
			logouts.setText("Anzahl der Logouts: " + counterLogout);
		}
	}
}
