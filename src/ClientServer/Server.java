package ClientServer;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * <p>Materialien zu den zentralen
 * Abiturpruefungen im Fach Informatik ab 2012 in
 * Nordrhein-Westfalen.</p>
 * <p>Klasse Server</p>
* Ein Server ist ein vereinfachter ServerSocket, der zus&auml;tzliche Funktionen hat.<br>
* Es k&ouml;nnen beliebig viele Kontakte mit Clientverbindungen aufgebaut werden.<br>
* Der Dialog mit den Clients wird nebenl&auml;ufig realisiert.
* <p>NW-Arbeitsgruppe: Materialentwicklung zum Zentralabitur
 * im Fach Informatik</p>
 *
 * @version 2011-12-12
 */ 
public abstract class Server
{

   // Objekte
    private ServerSocket serverSocket;
    private ArrayList<ServerConnection> verbindungen;
    private ServerSchleife schleife;
    
    // Attribute
    protected int zPort;
    
    /**
    Verbindung des Servers mit einem Client.<br>
    Kann nebenl&aumlaeufig die empfangenen Nachrichten bearbeiten.
    @author Horst Hildebrecht
    @version 1.0 
    */
    protected class ServerConnection extends Connection
    {
        // Objekte
        Server server;
        
        private String n;

		//Connection sConnection;
        /*
        Die ServerVerbindung wurde inialisiert.
        @param pSocket Socket, der die Verbindung beschreibt
        @param pServer Server, den die ServerVerbindung kennen lernt
        */
        public ServerConnection(Socket pSocket, Server pServer)
        {
            super(pSocket);
            server = pServer;
        }
        
        /**
        Solange der Client Nachrichten sendete, wurden diese empfangen und an die Server weitergereicht.<br>
        Abgebrochene Verbindungen wurden erkannt.
        */
        public void run()
        {
            String lNachricht;
                               
            while (!this.isClosed())
            {
                lNachricht = this.receive();
                if (lNachricht == null)
                {
                    if (!this.isClosed())
                    {
                        server.closeConnection(this.getRemoteIP(), this.getRemotePort());
                    }
                }
                else
                    server.processMessage(this.getRemoteIP(), this.getRemotePort(), lNachricht);
            }
        }

		public String getN() {
			return n;
		}

		public void setN(String n) {
			this.n = n;
		}
                
    }   
     
    private class ServerSchleife extends Thread
    {
    
        private Server server;
        
        public ServerSchleife(Server pServer)
        {
            server = pServer;
        }
        
        public void run()
        {
            while (true) // ewige Schleife
            {
                try
                {
                    Socket lClientSocket = server.serverSocket.accept();
                    ServerConnection lNeueSerververbindung = new ServerConnection(lClientSocket, server);
                    // Der Client laeuft in einem eigenen Thread, damit mehrere Clients gleichzeitig
                    // auf den Server zugreifen koennen.
                    server.ergaenzeVerbindung(lNeueSerververbindung);
                    lNeueSerververbindung.start();
                 }

                catch (Exception pFehler)
                {
                    System.err.println("Fehler beim Erwarten einer Verbindung in Server: " + pFehler);
                    pFehler.printStackTrace();
                    return;
                }    
             }
         }               
    }

       /**
    Der Server ist initialisiert.
    @param pPortNr Portnummer des Sockets
     * @throws IOException 
    */
    public Server(int pPortNr) throws IOException
    {
           serverSocket = new ServerSocket(pPortNr);
           zPort = pPortNr;
           verbindungen = new ArrayList<>();
           schleife = new ServerSchleife(this);
           schleife.start();
   
    }
    
    public String toString()
    {
        return "Server von ServerSocket: " + serverSocket;
    }
    
    private void ergaenzeVerbindung(ServerConnection pVerbindung)
    {
        verbindungen.add(pVerbindung);
        this.processNewConnection(pVerbindung.getRemoteIP(), pVerbindung.getRemotePort());
    }
    
    /**
    Liefert die Serververbindung der angegebenen IP mit dem angegebenen Port, null falls nicht vorhanden.
    @param pClientIP IP-Nummer des Clients der gesuchten Verbindung
    @param pClientPort Port-Nummer des Clients der gesuchten Verbindung
    */  
    public ServerConnection SerververbindungVonIPUndPort(String pClientIP, int pClientPort)
    {
        ServerConnection lSerververbindung;
        
        Iterator<ServerConnection> it = verbindungen.iterator();
        
        while (it.hasNext())
        {
            lSerververbindung = (ServerConnection) it.next();
            if (lSerververbindung.getRemoteIP().equals(pClientIP) && lSerververbindung.getRemotePort() == pClientPort)
                return lSerververbindung;
        }   
    
        return null; // IP nicht gefunden
    }
          
    /**
    Eine Nachricht wurde an einen Client geschickt.
    @param pClientIP IP-Nummer des Empf&auml;ngers
    @param pClientPort Port-Nummer des Empf&auml;ngers
    @param pMessage die verschickte Nachricht
    */
    public void send(String pClientIP, int pClientPort, String pMessage)
    {
      ServerConnection lSerververbindung = this.SerververbindungVonIPUndPort(pClientIP, pClientPort);
      if (lSerververbindung != null)
        lSerververbindung.send(pMessage);
      else
        System.err.println("Fehler beim Senden: IP " + pClientIP + " mit Port " + pClientPort + " nicht vorhanden.");
    }
    
    /**
    Eine Nachricht wurde an alle verbundenen Clients geschickt.
    @param pMessage die verschickte Nachricht
    */
    public void sendToAll(String pMessage)
    {
        ServerConnection lSerververbindung;
        Iterator<ServerConnection> it = verbindungen.iterator();
        while (it.hasNext())
        {	
            lSerververbindung = (ServerConnection) it.next();
            lSerververbindung.send(pMessage);
        }   
    }
    
    /**
    Die Verbindung mit der angegebenen IP und dem angegebenen Port wurde geschlossen.<br>
    @param pClientIP IP-Nummer des Clients der zu beendenden Verbindung
    @param pClientPort Port-Nummer des Clients der zu beendenden Verbindung
    */
    public void closeConnection (String pClientIP, int pClientPort)
    {
        ServerConnection lSerververbindung = this.SerververbindungVonIPUndPort(pClientIP, pClientPort);
        if (lSerververbindung != null)
        {   this.processClosedConnection(pClientIP, pClientPort);
            lSerververbindung.close();
            this.loescheVerbindung(lSerververbindung);

        }
        else
            System.err.println("Fehler beim Schlie\u00DFen der Verbindung: IP " + pClientIP + " mit Port " + pClientPort + " nicht vorhanden.");

    }


    /**
    Eine Verbindung wurde aus der Empf&auml;ngerliste gel&ouml;scht.
    @param pVerbindung die zu l&ouml;schende Verbindung
    */
    private void loescheVerbindung(ServerConnection pVerbindung)
    {
        Iterator<ServerConnection> it = verbindungen.iterator();
        while (it.hasNext())
        {
            ServerConnection lClient = (ServerConnection) it.next();
            if (lClient == pVerbindung){
            	boolean done = false;
            	while(!done){
                    try{
                    	verbindungen.remove(lClient);
                    	done = true;
                    }
                    catch(Exception e){
                    }
            	}
            	
            }
        }   
    }
    
    /**
    Ein neuer Client hat sich angemeldet.<br>
    Diese leere Methode kann in einer Unterklasse realisiert werden (Begr&uuml;&szlig;ung).
    @param pClientIP IP-Nummer des Clients, der neu angemeldet ist
    @param pClientPort Port-Nummer des Clients, der neu angemeldet ist
    */
    public abstract void processNewConnection(String pClientIP, int pClientPort);
    
    /**
    Eine Nachricht von einem Client wurde bearbeitet.<br>
    Diese leere Methode sollte in Unterklassen &uuml;berschrieben werden.
    @param pClientIP IP-Nummer des Clients, der die Nachricht geschickt hat
    @param pClientPort Port-Nummer des Clients, der die Nachricht geschickt hat
    @param pMessage Die empfangene Nachricht, die bearbeitet werden soll
    */

    public abstract void processMessage(String pClientIP, int pClientPort, String pMessage);

    /**
    Die Verbindung mit einem Client wurde beendet oder verloren.<br>
    Diese leere Methode kann in einer Unterklasse realisiert werden.
    @param pClientIP IP-Nummer des Clients, mit dem die Verbindung beendet wurde
    @param pClientPort Port-Nummer des Clients, mit dem die Verbindung beendet wurde
    */
    public abstract void processClosedConnection(String pClientIP, int pClientPort);
    
    /**
    Der Server wurde geschlossen.
    */
    public void close()
    {
        try
        {
            schleife.interrupt();
            serverSocket.close(); serverSocket = null;
        }

        catch (Exception pFehler)
        {
            System.err.println("Fehler beim Schlie\u00DFen des Servers: " + pFehler);
        }
       
    }

    
    
}