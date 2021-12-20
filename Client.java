

import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Client{

    private static int server_port = 8080; // client port number maatch to server port 

    private static void print_response(String response){
        String output = "";
        for(int i = 0; i < response.length(); i++){
            if(response.charAt(i) == '<'){
                if(!(i+2 <= response.length())) break;
                if (response.charAt(i + 1) == 'n' && response.charAt(i+2) == '>'){
                    output += '\n';
                    i+=2;
                    continue;
                }
            }else if(response.charAt(i) == '|'){
                output += "   ";
                continue;
            }output += response.charAt(i);
        }
        System.out.println(output);
    }

    public static void main(String[] args) throws IOException{
        
        if (args.length < 1){
            System.out.println("Usage: client <Server IP Address>");  //client  is connecting to the server 
            System.exit(1);
		}

        //initiating the connection with the server 
        Socket soc = new Socket(args[0], server_port);
        PrintWriter out = new PrintWriter(soc.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Client Initiated!\n$>");

        try{
            String user_input, response;
            while((user_input = stdin.readLine()) != null){
                
                out.println(user_input);
                out.flush();

                response = in.readLine();
                if(response.equals("SD")){
                    System.out.println("The Server is no longer Active");
                    System.exit(0);
                }
                print_response(response);
                
                if(user_input.equalsIgnoreCase("QUIT"))
                    break;
                System.out.print("$>");
            }
        }catch (IOException e){
            System.out.println("The Server is no longer Active");
            System.exit(0);
        }finally{
            soc.close();
        }
    }
}