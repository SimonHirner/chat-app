package edu.hm.dako.chat.AuditLogServer;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * 
 * Es wird ein Fenster erstellt, welches Informationen ueber das AuditLog anzeigt
 * 
 * @author Armin Lewig
 *
 */

public class AdministrationGuiForTcp extends JFrame {
		
	/**
	 * Es wird hier nur mitgezaehlt, aber nicht auf die AuditLog Datei zugegriffen
	 */
	
	JFrame information = new JFrame("AuditLog Informationen");

	public AdministrationGuiForTcp() {
		information.setLayout(null);
		information.setVisible(true);
		information.setSize(400, 400);
		information.setResizable(false);
		information.setLocationRelativeTo(null);
		information.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		numberOfClients();
		numberOfAuditLogPdus();
		numberOfPduTypes();
	}
	
	//Gibt die Anzahl der Benutzer an
	private void numberOfClients() {
		int numberOfUserNames = AuditLogTcpServer.userNames.size();
		String number = String.valueOf(numberOfUserNames);
		
		JLabel text = new JLabel("Anzahl der Benutzer: " + number);
		text.setBounds(5,-30,200,100);
		information.add(text);
	}

	//Gibt die Anzahl aller AuditLog-PDUs an
	private void numberOfAuditLogPdus() {
		String number = Long.toString(AuditLogTcpServer.counterAuditlog);
		JLabel text = new JLabel("Anzahl aller AuditLog-PDUs: " + number);
		text.setBounds(5,-10,200,100);
		information.add(text);
	}
	
	//Gibt an, wie oft jemand neu eingeloggt ist, wie oft jemand eine Nachricht geschrieben hat und wie oft sich jemand ausgeloggt hat
	private void numberOfPduTypes() {
		//Anzahl der Logins
		String numberLogin = Long.toString(AuditLogTcpServer.counterLogin);
		JLabel textLogin = new JLabel("Anzahl der Logins: " + numberLogin);
		textLogin.setBounds(5,10,200,100);
		information.add(textLogin);
		
		//Anzahl aller Nachrichten
		String numberChat = Long.toString(AuditLogTcpServer.counterChat);
		JLabel textChat = new JLabel("Anzahl der Nachrichten: " + numberChat);
		textChat.setBounds(5,30,200,100);
		information.add(textChat);
		
		//Anzahl der Logouts
		String numberLogout = Long.toString(AuditLogTcpServer.counterLogout);
		JLabel textLogout = new JLabel("Anzahl der Logouts: " + numberLogout);
		textLogout.setBounds(5,50,200,100);
		information.add(textLogout);
	}
}