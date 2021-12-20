

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.ServerSocket;
import java.io.IOException;

public class Server{
    
    private static int server_port = 8080;
    private static int num_threads = 4;

    private static ServerSocket listener;
    public  static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(num_threads);

    public static HashMap<String, String> user_data = new HashMap<String, String>();

    public static void main(String args[]) throws IOException{
        
        //user login information
        user_data.put("root", "root01");
        user_data.put("john", "john01");
        user_data.put("david", "david01");
        user_data.put("mary", "mary01");


        listener = new ServerSocket(server_port);
        System.out.println("Opening the Server, Please wait_ \n"); // server waiting for connection 
        
        //Client Instruction 
        System.out.println ("Instruction For Client:    ");  
        System.out.println ("To Login: Login Username Password") ;
        System.out.println ("To ADD record:   ADD Firstname  Lastname  Phone-number ");
        System.out.println ("To DELETE - DELETE Record-ID") ;
        System.out.println ("To search records: LOOK Num Key" ) ;
        System.out.println ("To view LIST: LIST" ) ;
        System.out.println ("To view Users: WHO" ) ;
        System.out.println ("To Shutdown: SHUTDOWN");
        System.out.println ("To Logout: LOGOUT") ;
        System.out.println ("To QUIT: QUIT\n") ;

        while(true){
            Socket client = listener.accept();
            ClientHandler clientThread = new ClientHandler(client);
            clients.add(clientThread);
            pool.execute(clientThread);
        }
        

    }

    //to shutdown the server 
    public static void shutdown() throws IOException{
        for(int i = 0; i < clients.size(); i++)
            clients.get(i).shutdown();
        System.out.println("SHUTDOWN Closing the Server!");
        listener.close();
        System.exit(0);
    }
}