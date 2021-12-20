

import java.net.Socket;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

public class ClientHandler implements Runnable{
    
    public boolean logged_in = false;
    public String user_name = null;
    
    private Socket client;
    private PrintWriter out;
    private BufferedReader in;
    
    private ArrayList<String> address_book;
    private RandomAccessFile random_access_file;

    private static int client_id = 0;
    private static String filename = "records.txt";  //record will be saved in this txt file
    private static String phone_number_format = "\\d{3}-\\d{3}-\\d{4}"; //phone numnber format

    public ClientHandler(Socket client) throws IOException{

        client_id++;
        this.client = client;
        
        this.in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
        this.out  = new PrintWriter(this.client.getOutputStream());
        
        this.address_book = new ArrayList<>();
        this.random_access_file = new RandomAccessFile(new File(filename), "rw");
        read_from_file(this.address_book, this.random_access_file);

        System.out.println("Client C0"+client_id+" connected successfully");  //specifies the client number 

    }

    //if the specific client decide to shutdown the connection 
    public void shutdown(){
        try{
            client.close();
            in.close();
            out.close();
            System.out.println("C0"+client_id+" quits the server!");
            System.exit(0);
        }catch(IOException e){
        }
    }

    @Override
    public void run(){
        String client_said;
        try{
            while((client_said = in.readLine()) != null){
                
                System.out.println("C0" + client_id + ": " + client_said);
                String[] input_command = client_said.trim().split("\\s+");
                String response = null;

                //all the user commands implementation 
                if (input_command[0].equalsIgnoreCase("ADD")) 
                    response = add(input_command);
                
                else if (input_command[0].equalsIgnoreCase("DELETE"))
                    response = delete(input_command);

                else if(input_command[0].equalsIgnoreCase("LIST")) 
                    response = list(input_command);

                else if(input_command[0].equalsIgnoreCase("LOOK")) 
                    response = look(input_command);

                else if(input_command[0].equalsIgnoreCase("WHO")) 
                    response = who(input_command);
                
                else if(input_command[0].equalsIgnoreCase("LOGOUT")) 
                    response = logout(input_command);

                else if(input_command[0].equalsIgnoreCase("LOGIN")) 
                    response = login(input_command);

                else if(input_command[0].equalsIgnoreCase("QUIT")){
                    if (input_command.length > 1)  
                        response = "301- message format error!";
                    else{
                        response = "200 - OK";
                        this.out.println(response);
                        this.out.flush();
                        break;
                    }
                }

                else if(input_command[0].equalsIgnoreCase("SHUTDOWN")){
                    if (input_command.length > 1)  
                        response = "301- message format error!";
                    else if(! this.logged_in)
                        response = "401- You are not Logged in!";
                    else{
                        response = "SD";
                        this.out.println(response);
                        this.out.flush();
                        Server.shutdown();
                        break;
                    }
                }

                else
                    response = "401- Invalid Command!";
                
                this.out.println(response);
                this.out.flush();
                print_response(response);
            }

        }catch(IOException  e){

            System.err.println("Oops! An Error occurred.\n" + e.getStackTrace());
       
        }finally{
            try{
                client.close();
                in.close();
                out.close();
                System.out.println("C0"+client_id+" quits the server!");
            }catch(IOException e){
                System.err.println("An error occurred while closing resources.\n" + e.getStackTrace());
            }
        }
    }

    private void print_response(String response){
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
            }
            output += response.charAt(i);
        }
        System.out.println(output);
    }

    private static boolean hasAlpha(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
           //if character is digit then return false 
           if ((Character.isDigit(str.charAt(i)))) {
              return false;
           }
        }
        return true;
    }

    private static void read_from_file(ArrayList<String> address_Array, RandomAccessFile random_acc_file) throws IOException {
        String line = null;
        while((line = random_acc_file.readLine()) != null) {
            address_Array.add(line);
        }
    }

    private static void update_record_file(ArrayList<String> address_Array, RandomAccessFile random_acc_file) throws IOException {
        random_acc_file.setLength(0);
        for(int i = 0; i < address_Array.size(); i++) {
            random_acc_file.writeBytes(address_Array.get(i) + "\r\n");
        }
    }

    private String add(String[] input_command){
        String response = null;
        if (input_command.length != 4) { // only 4 strings allowed including ADD
            response = "301- message format error!"; // if client do not enter command in right order                                 
        } else {
            if (input_command[1].length() > 8 || input_command[2].length() > 8)  //for first and last name 
                response = "301- message format error!";                          
            else if (!hasAlpha(input_command[1]) || !hasAlpha(input_command[2])) 
                response = "301- message format error!";                         
            
            else if (input_command[3].length() != 12 || !input_command[3].matches(phone_number_format)) //for phone number format
                response = "301- message format error!";                         
            
            else if(address_book.size() >= 20) //if more than 20 record in the array/file
                response = "Error!- records limit exceeded!";           
            
            else if(! this.logged_in)
                response = "401- You are not Logged in!";

            else{
                try{
                    String recordID = String.valueOf(1000 + address_book.size() + 1);
                    String addressData = recordID + "|" + input_command[1] + "|" + input_command[2] + "|" + input_command[3];
                    random_access_file.writeBytes(addressData + "\r\n");
                    address_book.add(addressData);
                    response = "200 ok <n>  The new record id is " + recordID;  
                }catch (Exception e){
                    response = "501- IO error, unable to write to the file!";
                }
            }
        }
        return response;
    }

    private String delete(String[] input_command){
        String response = null;
        if (input_command.length != 2) //no more than 2 strings 
            response ="301- message format error!";  
        else if(! this.logged_in)
            response = "401- You are not Logged in!";               
        else {
            String newID = null;
            String newAdrss = null;
            //pos = record id removed from array and file
            int pos = (Integer.parseInt(input_command[1]) % 100) - 1;
            if (pos >= address_book.size() || pos < 0) { 
                response ="Error! - Record Could not find!";   
              
            } else { 
                
                for (int i = address_book.size() - 1; i > pos; i--) {
                    newID = String.valueOf(1000 + i);
                    newAdrss = newID + address_book.get(i).substring(4);
                    address_book.set(i, newAdrss);
                }
                try{
                    //remove the record, call the appropriate function and return with '200-ok' message 
                    address_book.remove(pos);
                    update_record_file(address_book, random_access_file);
                    response ="200 ok!";  
                }catch (Exception e){
                    response = "501- IO error, unable to write to the file!";
                }
                
            }
        }
        return response;
    }

    private String list(String[] input_command){
        String response = null;
        if (input_command.length > 1) { //only one string command allowed 
            response = "301- message format error!";    
        } else {
            response = "200 ok!<n>" + "|" + "The list of Records found in the book:" + "|" + "<n>";  
            for (int i = 0; i < address_book.size(); i++) 
                response += "|" + address_book.get(i) + "|" + "<n>";
        }
        return response;
    }

    private String look(String[] input_command){
        String response = null;
        ArrayList<String> result = new ArrayList<>();
        if(input_command.length != 3)
            response = "301- message format error!";
        else{
            if (hasAlpha(input_command[1]))
                response = "302- Arg 2 must be a number!";
            else{
                int search_key = Integer.parseInt(input_command[1]);
                for (int i = 0; i < address_book.size(); i++){
                    String line = address_book.get(i);
                    String[] splits = line.split("\\|");
                    if(splits[search_key].equals(input_command[2]))
                        result.add(line);
                }
                if(result.size() > 0){
                    response = "200 ok!<n>  "+result.size()+" results found.<n>";
                    for(int i = 0; i < result.size(); i++)
                        response+= "|" + result.get(i) + "<n>";
                }else
                    response = "404 Your search did not match any records.";
            }
        }
        return response;
    }

    private String who(String[] input_command){
        String response = null;
        if (input_command.length > 1)  //only one string command allowed 
            response = "301- message format error!";  
        else{
            response = "200 ok! <n>|The list of Active users are: <n>";
            for(int i = 0; i < Server.clients.size(); i++){
                ClientHandler curr = Server.clients.get(i);
                if(curr.logged_in)
                    response += "|" + curr.user_name + "|"+curr.client.getRemoteSocketAddress().toString()+"<n>";
            }
        }
        return response;  
    }

    private String logout(String[] input_command){
        String response = null;
        if (input_command.length > 1)  //only one string command allowed 
            response = "301- message format error!";  
        else if(! this.logged_in)
            response = "401 You are not Logged in!";
        else{
            this.logged_in = false;
            this.user_name = null;
            response = "200 ok!";
        } 
        return response;
    }

    private String login(String[] input_command){
        String response = null;
        if (input_command.length != 3)  //only one string command allowed 
            response = "301- message format error!";
        else {
            String pass  = Server.user_data.get(input_command[1]);
            if(pass == null)
                response = "401- Invalid Username";
            else if(! pass.equals(input_command[2]))
                response = "402- Invalid Password";
            else{
                this.logged_in = true;
                this.user_name = input_command[1];
                response = "200 ok";
            }
        }
        return response;  
    }
}
