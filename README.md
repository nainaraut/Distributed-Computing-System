# Distributed-Computing-System
A Java based distributed system to reduce the processing workload of a server by distributing its task in data packets to clients(mobile devices)

Summary

This project concentrates on simplifying the distributed computing system by introducing Java Based portable server and processing data on smartphone devices. These devices can connect or disconnect any time they want. Therefore, fixed cluster is not needed. Following calculations show the power of using smartphones to do distributed processing.


Objectives

To simplify distributed computing system and process data on nearby smartphones.
Write a Java based portable server which distributes file chunks, schedules and manages jobs.
Write a program / application on smartphone to process the data. 
Establish communication between smartphone and server to exchange files and results
Design and implement an architecture in the server with multiple level threads that will enable simultaneous communication of many jobs on different smartphones.


Steps involved on the PC or Server Side

Step 1 : Loading the file and Splitting it
Create a new thread for executing the new class ‘SplitFileThread’ in order to get the input from the user, split the input file and store the chunks on the server.

Read the file that need to be processed for the execution from the server (PC).
A new thread is created for each file selected from the user so that every file can be processed simultaneously.
The file is read from the user using a JFileChooser dialog box. This dialog box helps us choose any file from the user PC. 
We get the selected file using the getSelectedFile method of the JFileChooser class.

Split the input file line by line and store it on the PC
Using FileReader class read the file in the default encoding and wrap it using BufferedReader.
For each line of the file read using BufferedReader, we increment the count variable.This variable decides the chunk number for the split file.
For count variable equal to 20, the chunk number is incremented by one, and new file is created for the next 20 lines.
For saving the split file on the desktop we decided a directory ‘’.If the directory doesn't exist, then we create the same using mkDir method of the File class.
Every line is written to the split file by using the FileWriter class and the name of the file is given as text plus the chunk number.

Step 2 : Initiating connection with mobile using default port
Start a new thread for executing class ‘MainThread’ in order to get the IP address of the server, storing the IP address on the cloud and to accept a connection from a new mobile on the default port.
We get the IP address of the server using the NetworkInterface class and the getInetAddresses() method.
Store the default port number and the IP address to the Parse.

Step 3: Storing IP address and port number on parse cloud
Accessing Parse cloud is done using REST API in python.
Connection to Parse cloud is done using HTTPS request and response messages.
New object on Parse is created by sending a POST request to the class URL along with the contents of object.
The POST functions body is a JSON object consisting of three fields: “ServerName”, “IP” and “PORT”.
If the POST function successfully updates the parameters passed to it, then it returns the object id of the row on which the update was performed. 
For example in our application we used POST function as 

connection.request('POST','/1/classes/ServerIP', json.dumps 
({
"ServerName":sys.argv[1],
"IP": sys.argv[2],
       "PORT": sys.argv[3]
}), 
{
"X-Parse-Application-Id":"<YOUR PARSE APP ID HERE>",
"X-Parse-REST-API-Key": "<YOUR PARSE REST API KEY HERE>",
       "Content-Type": "application/json"
})
sys.argv[1], sys.argv[2] and sys.argv[3]  are command line arguments passed to      python from java. 
This will allow clients to access IP address and port number by retrieving the Parse Objects and communicate on specified server IP address and port number. 
Object ID is unique identifier for each saved object that have been generated when we created the parse object. by using this ObjectId we can easily retrieve the ParseObject. 

Step 4: Create Socket Channel and wait for new clients
Create a Socket Channel connection so that new clients can connect to the server.
We can create a socket channel by using open methods. Socket channel cannot be created for a pre-existing socket.
A new socket is not pre connected. A client cannot start the connection unless we have a socket channel connection initiated. We need to connect to the socket using a port number. Trying to connect to 
an unconnected socket channel will give us NotYetConnectedException.
Socket Channel allow us to create a non blocking connection with the client by using the configureBlocking(false) method of the socket channel.

Calculate the new client port number which will be unique for each client. Send this port number to the connected client using the write() method of the ServerSocketConnection class.

Step 5 : Sending File chunks to mobiles
Create new thread for executing class ‘ClientThread’ in which we send the chunks to client using a socket connection and new assigned port number to the client.
Connect to the mobile using socket.
Send the chunks to the connected mobiles simultaneously.
The results from mobiles will be received in input buffer of server, sent by device by writing the result to its output buffer.
Till the time the mobiles are connected, we can continue sending the chunks to them simultaneously and get the result each time a file chunk is processed back to the server through the inputStreamReader of the socket.


Steps involved on the Mobile side or Client side

Step 1: Get the IP Address and Port from Parse
To maintain flexibility of using the Server Java Program on any computer, we have included this step.
As our server may not have static IP, the current IP address of the server needs to be communicated with client. Server stores this information on Parse.
When new mobile is ready to connect to the server, it first gets the IP address of the server and the port number from the Parse.

Step 2: Initiate connection to server
Once client gets the information about IP address a port number of the server, it creates a socket connection and communicates with the server to inform it that it is ready for processing.
Server then sends back new port number that this client must use for any further communication with the server.
Client stores this port number locally and starts a new android service to do the processing.

Step 3: Get chunk and process data
The Android service that processes a data first reads the newly assigned port number and starts a new socket connection on this port. 
Server then sends the file chunk to the client. Client receives this chunk on socket and stores the chunk locally to do further processing.
Client then processes the data. In our example client counts the number of characters in the chunk. 

Step 5: Sending back results
After processing client sends the result back to the server (PC) through the same socket connection.
After getting results, both server and clients close the socket and end the process.
Step 6: Loop again
Client may continue executing Step-2 to Step-5 again and again to process more chunks. 




