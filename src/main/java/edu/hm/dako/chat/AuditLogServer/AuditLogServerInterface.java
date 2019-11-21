package edu.hm.dako.chat.AuditLogServer;

/**
 * Einheitliche Schnittstelle aller AuditLogServer
 * 
 * @author Julia Huber
 */
public interface AuditLogServerInterface {

    /**
     * Startet den Server
     * @throws Exception 
     */
    void start() throws Exception;

    /**
     * Stoppt den Server
     *
     * @throws Exception
     */
    void stop() throws Exception;
}