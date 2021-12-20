
											The commands implemented: 

        To Login: Login Username Password
        To ADD record:   ADD Firstname  Lastname  Phone-number
        To DELETE - DELETE Record-ID
        To search records: LOOK Num Key
        To view LIST: LIST
        To view Users: WHO
        To Shutdown: SHUTDOWN
        To Logout: LOGOUT
        To QUIT: QUIT\n


							Instruction to Compile: (please input commands in right order as instructed below)

Server:
1. cd Private/
2. cd Ahmed_T_p2
3. javac Client.java
4. javac ClientHandler.java 
5. javac Server.java
6. java Server
Once Succesfully input the commands following message will appear in server window:
Opening the Server, Please wait_

Client: 
1. java Client localhost

If the instruction followed correctly. the Server will prompt the following message: 
"Successfully connected with the client"
Then Client input any of the given command to test it.

Special Note:
Using the follwoing command is not required to run the code (However i do add the make file in the folder):
1. make clean
2. make

 


							Known problems or bugs 

We do not experience any issues/bug while runnig this program. All bugs are fixed. As stated earlier please do not use the 'make clean', 'make' command. 


							The Output at the Clent Side
C01: QUIT
C01 quits the server!
Client C02 connected successfully
Client C03 connected successfully
Client C04 connected successfully
C04: Login root root01
200 ok
C04: login john john01
200 ok
C04: Login david david01
200 ok
C04: Add Tanim Ahmed 234-454-3454
200 ok
  The new record id is 1002
C04: LIST
200 ok!
   The list of Records found in the book:
   1001   Tanim   Ahmed   313-434-6787
   1002   Tanim   Ahmed   234-454-3454
C04: WHO
200 ok! 
   The list of Active users are:
   root   /127.0.0.1:59568
   root   /127.0.0.1:59609
   john   /127.0.0.1:59655
   david   /127.0.0.1:59659
C04: Logout
200 ok!
C04: QUIT
C04 quits the server!
C03: QUIT
C03 quits the server!
C02: QUIT
C02 quits the server!


