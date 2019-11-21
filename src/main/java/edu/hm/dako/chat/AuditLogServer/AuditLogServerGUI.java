package edu.hm.dako.chat.AuditLogServer;


import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.sun.tools.javac.util.Log;
import javafx.event.EventHandler;
import edu.hm.dako.chat.common.ExceptionHandler;
import edu.hm.dako.chat.common.ImplementationType;
import edu.hm.dako.chat.common.SystemConstants;
import edu.hm.dako.chat.server.ServerFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Benutzeroberfläche zum Starten und Stoppen des AuditLogServers.
 * 
 * @author Julia Huber
 */

public class AuditLogServerGUI extends Application {
    
    private static Logger log = Logger.getLogger(AuditLogServerInterface.class);
    
    // Standardvalues for AuditLogServer
    static final String DEFAULT_AUDITLOGSERVER_NAME = "localhost";
    static final String DEFAULT_AUDITLOGSERVER_PORT = "40001";

    // Standard-Puffergroessen fuer Serverport in Bytes
    static final String DEFAULT_SENDBUFFER_SIZE = "30000";
    static final String DEFAULT_RECEIVEBUFFER_SIZE = "800000";
    static final String MAX_SENDBUFFER_SIZE = "500000";
    static final String MAX_RECEIVEBUFFER_SIZE = "500000";

    final VBox pane = new VBox(5);

    // Name der AuditLog-Datei
    static final String auditLogFile = new String("ChatAuditLog.dat");

    // Zaehler fuer ankommende AuditLog-PDUs
    protected long counter = 0;

    // Interface der AuditLogServerImplementierung
    private static AuditLogServerInterface auditLogServer;

    // Combobox fue AuditLogServer Implementierung
    private ComboBox<String> comboBoxAuditLogServerType;

    // Textfelder und Labels der GUI
    private TextField auditLogServerHostnameOrIp;
    private TextField auditLogServerPort;
    private TextField sendBufferSize;
    private TextField receiveBufferSize;
    private Label auditLogServerHostnameOrIpLabel;
    private Label auditLogServerPortLabel;
    private Label sendBufferSizeLabel;
    private Label receiveBufferSizeLabel;
    private Label auditLogConnectionTypeLabel;

    private Button startButton;
    private Button stopButton;
    
    //Gitb an ob AuditLogServer startbereit ist
    private boolean startable = true;

    
    // Implementierungsarten für Combobox
    ObservableList<String> auditLogServerImplTypeOptions = FXCollections.observableArrayList(
            SystemConstants.AUDIT_LOG_SERVER_TCP_IMPL, SystemConstants.AUDIT_LOG_SERVER_UDP_IMPL);

    public AuditLogServerGUI() {
       
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void start(final Stage stage) {

        stage.setTitle("AuditLogServerGUI");
        stage.setScene(new Scene(pane, 415, 465));
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                try {
                    auditLogServer.stop();
                } catch (Exception ex) {
                    AuditLogServerGUI.log.error("Fehler beim Schließen.");
                    ExceptionHandler.logException(ex);
                }
                
            }});

        pane.setStyle("-fx-background-color: cornsilk");
        pane.setPadding(new Insets(10, 10, 10, 10));

        pane.getChildren()
                .add(createSeparator("Eingabe", 315));
        pane.getChildren()
                .add(createInputPane());

        pane.getChildren()
                .add(createSeparator("", 360));
        pane.getChildren()
                .add(createButtonPane());

        reactOnStartButton();
        reactOnStopButton();
        stopButton.setDisable(true);
    }

    /**
     * Eingabe-Pane erzeugen
     * 
     * @return pane
     */

    private GridPane createInputPane() {
        final GridPane inputPane = new GridPane();

        final Label label = new Label("Parameter AuditLogServer");
        label.setMinSize(100, 25);
        label.setMaxSize(100, 25);

        auditLogConnectionTypeLabel = createLabel("Verbindungstyp");
        auditLogServerHostnameOrIpLabel = createLabel("AuditLogServer Hostname/IP-Adr.");
        auditLogServerPortLabel = createLabel("AuditLogServer Port");
        sendBufferSizeLabel = createLabel("Sendepuffer in Byte");
        receiveBufferSizeLabel = createLabel("Empfangsbuffer in Byte");

        inputPane.setPadding(new Insets(5, 5, 5, 5));
        inputPane.setVgap(1);

        comboBoxAuditLogServerType = createAuditLogTypeComboBox(auditLogServerImplTypeOptions);
        auditLogServerHostnameOrIp = createEditableTextfield(DEFAULT_AUDITLOGSERVER_NAME);
        auditLogServerPort = createEditableTextfield(DEFAULT_AUDITLOGSERVER_PORT);
        sendBufferSize = createEditableTextfield(DEFAULT_SENDBUFFER_SIZE);
        receiveBufferSize = createEditableTextfield(DEFAULT_RECEIVEBUFFER_SIZE);

        inputPane.add(label, 1, 3);
        inputPane.add(auditLogConnectionTypeLabel, 1, 5);
        inputPane.add(comboBoxAuditLogServerType, 3, 5);
        inputPane.add(auditLogServerHostnameOrIpLabel, 1, 7);
        inputPane.add(auditLogServerHostnameOrIp, 3, 7);
        inputPane.add(auditLogServerPortLabel, 1, 9);
        inputPane.add(auditLogServerPort, 3, 9);
        inputPane.add(sendBufferSizeLabel, 1, 11);
        inputPane.add(sendBufferSize, 3, 11);
        inputPane.add(receiveBufferSizeLabel, 1, 13);
        inputPane.add(receiveBufferSize, 3, 13);

        return inputPane;

    }

    /**
     * Pane für Buttons erzeugen
     * return HBox
     */
    private HBox createButtonPane() {
        final HBox buttonPane = new HBox(5);

        startButton = new Button("AuditLogServer starten");
        stopButton = new Button("AuditLogServer stoppen");

        buttonPane.getChildren()
                .addAll(startButton, stopButton);
        buttonPane.setAlignment(Pos.CENTER);
        return buttonPane;

    }

    /**
     * Label erzeugen
     * 
     * @param value
     * @return Label
     */
    private Label createLabel(String value) {
        final Label label = new Label(value);
        label.setMinSize(200, 25);
        label.setMaxSize(200, 25);
        return label;
    }

    /**
     * Aufbau der Combobox fuer die AuditLogServer Verbindung
     * 
     * @param options
     *            Optionen fuer Implementierungstyp
     * @return Combobox
     */
    private ComboBox<String> createAuditLogTypeComboBox(ObservableList<String> options) {
        ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.setMinSize(155, 28);
        comboBox.setMaxSize(155, 28);
        comboBox.setValue(options.get(0));
        comboBox.setStyle("-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5px, 5px, 5px, 5px");
        return comboBox;
    }

    /**
     * Trennlinie erstellen
     * 
     * @param value
     *            Text der Trennlinie
     * @param size
     *            Groesse der Trennlinie
     * @return Trennlinie
     */
    private HBox createSeparator(String value, int size) {
        // Seperator erstellen
        final HBox labeledSeparator = new HBox();
        final Separator rightSeparator = new Separator(Orientation.HORIZONTAL);
        final Label textOnSeparator = new Label(value);

        textOnSeparator.setMinWidth(size);

        rightSeparator.setMinWidth(size);
        rightSeparator.setMaxWidth(size);

        labeledSeparator.getChildren()
                .add(textOnSeparator);
        labeledSeparator.getChildren()
                .add(rightSeparator);
        labeledSeparator.setAlignment(Pos.BASELINE_LEFT);

        return labeledSeparator;
    }

    /**
     * Erstellung editierbarer Textfelder
     * 
     * @param value
     *            Feldinhalt
     * @return textField
     */
    private TextField createEditableTextfield(String value) {
        TextField textField = new TextField(value);
        textField.setMaxSize(155, 28);
        textField.setMinSize(155, 28);
        textField.setEditable(true);
        textField.setStyle("-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5px, 5px, 5px, 5px");
        return textField;
    }

    /**
     * Reaktion auf das Betaetigen des Start-Buttons.
     */
    private void reactOnStartButton() {
       
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                
                startable = true;
                // Eingabeparameter einlesen
                int serverPort = readAuditLogServerPort();
                int sendBufferSize = readAuditLogBufferSize();
                int receiveBufferSize = readAuditLogReceiveBufferSize();
                
                if(startable == true) {
                    
                    //Implemetierungstyp ermitteln
                    String auditLogConnectionType = readAuditLogComboBox();
                    
                    startButton.setDisable(true);
                    stopButton.setDisable(false);
                    
                    if(auditLogConnectionType.equals("UDP")) {
                         auditLogServer = new AuditLogUdpServer(serverPort, sendBufferSize, receiveBufferSize);
                        try {
                            auditLogServer.start();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                         auditLogServer = new AuditLogTcpServer(serverPort, sendBufferSize, receiveBufferSize);
                        try {
                            auditLogServer.start();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    
                } else {
                    setAlert("Bitte korrigieren Sie die rot markierten Felder");
                }
                
            }
        });
    }
        
    /**
     * Reaction auf das Betaetigen des Stop-Buttons.
     */

    private void reactOnStopButton() {
        stopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event ) {
                try {
                    auditLogServer.stop();
                } catch (Exception e) {
                    log.error("Fehler beim Stoppen des AuditLogServers");
                    ExceptionHandler.logException(e);
                }
                startButton.setDisable(false);
                stopButton.setDisable(true);
            }
        });

    }
    
    /**
     * AuditLogServerPort aus GUI auslesen.
     * 
     * @return AuditLogServerPort
     */
    private int readAuditLogServerPort() {
        String item = new String(auditLogServerPort.getText());
        Integer iServerPort = 0;
        if (item.matches("[0-9]+")) {
            iServerPort = new Integer(auditLogServerPort.getText());
            if ((iServerPort < 1) || (iServerPort > 65535)) {
                startable = false;
                auditLogServerPortLabel.setTextFill(Color.web(SystemConstants.RED_COLOR));
            } else {
                System.out.println("Serverport: " + iServerPort);
                auditLogServerPortLabel.setTextFill(Color.web(SystemConstants.BLACK_COLOR));
            }
        } else {
            startable = false;
            auditLogServerPortLabel.setTextFill(Color.web(SystemConstants.RED_COLOR));

        }
        return (iServerPort);
    }
    
    /**
     * AuditLogServerBufferSize aus GUI auslesen.
     * 
     * @return AuditLogServerBufferSize.
     */
    private int readAuditLogBufferSize() {
        String item = new String(sendBufferSize.getText());
        Integer iSendBufferSize = 0;
        if (item.matches("[0-9]+")) {
            iSendBufferSize = new Integer(sendBufferSize.getText());
            if ((iSendBufferSize <= 0)
                    || (iSendBufferSize > new Integer(MAX_SENDBUFFER_SIZE))) {
                startable = false;
                sendBufferSizeLabel.setTextFill(Color.web(SystemConstants.RED_COLOR));
            } else {
                sendBufferSizeLabel.setTextFill(Color.web(SystemConstants.BLACK_COLOR));

            }
        } else {
            startable = false;
            sendBufferSizeLabel.setTextFill(Color.web(SystemConstants.RED_COLOR));
        }
        return (iSendBufferSize);
        
    }
    
    private int readAuditLogReceiveBufferSize() {
        String item = new String(receiveBufferSize.getText());
        Integer iReceiveBufferSize = 0;
        if (item.matches("[0-9]+")) {
            iReceiveBufferSize = new Integer(receiveBufferSize.getText());
            if ((iReceiveBufferSize <= 0)
                    || (iReceiveBufferSize > new Integer(MAX_RECEIVEBUFFER_SIZE))) {
                startable = false;
                receiveBufferSizeLabel.setTextFill(Color.web(SystemConstants.RED_COLOR));
            } else {
                receiveBufferSizeLabel.setTextFill(Color.web(SystemConstants.BLACK_COLOR));
            }
        } else {
            startable = false;
            receiveBufferSizeLabel.setTextFill(Color.web(SystemConstants.RED_COLOR));
        }
        return (iReceiveBufferSize);
    }
    
    /**
     * AuditLogServer-Typ aus GUI auslesen
     */
    private String readAuditLogComboBox() {
        String implType;
        if (comboBoxAuditLogServerType.getValue() == null) {
             implType = new String(SystemConstants.AUDIT_LOG_SERVER_TCP_IMPL);
        } else {
        
             implType = new String(comboBoxAuditLogServerType.getValue().toString());
        }
        return (implType);
    }
    
    /**
     * Oeffnen eines Dialogfensters, wenn ein Fehler bei der Eingabe auftritt
     *
     * @param message
     */
    private void setAlert(String message) {
        ;
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Fehler!");
        alert.setHeaderText(
                "Bei den von ihnen eingegebenen Parametern ist ein Fehler aufgetreten:");
        alert.setContentText(message);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                alert.showAndWait();
                //alert.show();
            }
        });
    }
    

    private String readAuditLogServerHostnameOrIp() {
        return auditLogServerHostnameOrIp.getText();
    }
}
