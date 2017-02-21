Mani Japra
mjapra3@gatech.edu

CS 3251
Section A
09/30/2016
Sockets Programming Assignment 1

- To run the server:
    1. Change directories to where you have unpacked the files.
    2. Run "javac *.java" through Docker.
    3. Running the server:
        > UDP Server:
            * W/O debugging: "java sensor_server_udp -p <port number> -f <CSV file>"
            * W/ debugging: "java sensor_server_udp -d -p <port number> -f <CSV file>"
        > TCP Server:
            * W/O debugging: "java sensor_server_tcp -p <port number> -f <CSV file>"
            * W/ debugging: "java sensor_server_tcp -d -p <port number> -f <CSV file>"

- To run the client:
    1. Change directories to where you have unpacked the files.
    2. Run "javac *.java" through Docker.
    3. Running the server:
        > UDP Client:
            * W/O debugging: "java sensor_udp -s <server address> -p <port number> -u "<username>" -c "<password>" -r <value>"
            * W/ debugging: "java sensor_udp -d -s <server address> -p <port number> -u "<username>" -c "<password>" -r <value>"
        > TCP Client:
            * W/O debugging: "java sensor_tcp -s <server address> -p <port number> -u "<username>" -c "<password>" -r <value>"
            * W/ debugging: "java sensor_tcp -d -s <server address> -p <port number> -u "<username>" -c "<password>" -r <value>"

Description of Protocol:

When a client first connects with the server, a packet is sent to initiate authentication. This initial packet of information only contains
one string: "-a_req". When the server parses this String, the server generates a random 64 character string which will represent the
challenge value for the MD5 hash. Upon creating the random 64 character string, the server sends this string back to the client with no
additional padding. The client then receives this challenge value and calculates the hash based off of the MD5 encryption standard by
combining the username, password, and challenge value together into one string. Once the client generates this hash value, it converts it to
a string and sends it to the server by padding it in the following format "-h;<username>;<generated hash value>". The server then parses
and decouples the string based on the ";" character and uses the username provided by the client to lookup the password associated with it.
If the username does not exist, the authentication fails and the server sends back "-auth false". If the username does exist, the server
uses the username, password, and the hash value it generated before to create a hash based on the same MD5 encryption standard. The server
then compares the hash value it generated with the hash value it received from the client and if they match, the server responds to the
client with a "-auth true". If the hash values do not match, then the password the client provided was incorrect and a "-auth false" is
returned. If the authentication is successful, the server then awaits for the client to send the sensor data. Once the server receives
the packet of data that includes the sensor value, it parses the information and adds the new value to the sensor object it created when it
first started up. This completes the protocol description used in this project. To see implementation, please take a look at the java files
provided.

Any known bugs and/or limitations of your design or program:
I personally have not checked and verified the server handling multiple requests at the same time in the TCP implementation and it may
not work at all. This is the only limitation of which I'm aware of.
