
======================== Group Members ========================

                          Mani Japra
                      mjapra3@gatech.edu

                           Alex Kim
                      akim316@gatech.edu

===============================================================

                           CS 3251
                          Section A

                          11/23/2016

===============================================================

               Sockets Programming Assignment 2

===============================================================


Files included:
---------------------------------------------------------------

> fta_server.py :
   The front-end of the file transfer application that runs the server
> fta_client.py :
   The front-end of the file transfer application that runs the client
> crp.py :
   The implementation of the the CRP API
> crp_packet.py :
   A Packet class that wraps around the CRP API to provide an easy way to send/receive/get/set
   packets of data -> Holds all of our flags for each packet


Detailed Instructions on running the File Transfer Application:
---------------------------------------------------------------

> Server:

   1. To run the server, enter the following command into your docker application:
      "python ./fta_server.py -p <PORT NUMBER>"

      <PORT NUMBER> : the port number at which the FTA-server’s UDP socket should bind

      * Note: You can also initialize the server with a different window size (Default: 1)
              by using the following command:
              "python ./fta_server.py -p <PORT NUMBER> -w <WINDOW SIZE>"

              <WINDOW SIZE> : the maximum receiver’s window-size at the FTA-Server (in segments)

   2. (NOT IMPLEMENTED) Use the command "window <W>" to change the window-size of the server
      "FTA-server: window <W>"

      <W> : the maximum receiver’s window-size at the FTA-Server (in segments)

   3. (NOT IMPLEMENTED) Use the command "terminate" to shut down the server gracefully
      "FTA-server: terminate"

> Client:

   1. To run the client, enter the following command into your docker application:
      "python ./fta_client.py -p <PORT NUMBER> -a <IP ADDRESS>"

      <PORT NUMBER> : the UDP port number of FTA-server
      <IP ADDRESS>  : the IP address of FTA-server

      * Note: You can also initialize the client with a different window size (Default: 1)
              by using the following command:
              "python ./fta_client.py -p <PORT NUMBER> -a <IP ADDRESS> -w <WINDOW SIZE>"

              <WINDOW SIZE> : the maximum receiver’s window-size at the FTA-Server (in segments)

   2. To connect to the server, simply enter the command "connect"
      "FTA-client: connect"

   3. To get a file from the server, use the command "get <F>"
      "FTA-client: get <F>"

      <F> : The FTA-client downloads file <F> from the server
            (if <F> exists in the same directory with the FTA-server program)

   4. To upload a file to the server, use the command "post <F>"
      "FTA-client: post <F>"

      <F> : The FTA-client uploads file <F> to the server
            (if <F> exists in the same directory with the FTA-client program)

   5. To change the window size, use the command "window <WINDOW SIZE>"
      "FTA-client: window <WINDOW SIZE>"

      <WINDOW SIZE> : the maximum receiver’s window-size at the FTA-Client (in segments)

   6. To disconnect from the server gracefully, use the command "disconnect"
      "FTA-client: disconnect"


Any known bugs, limitations of design, limitations of program:
---------------------------------------------------------------

Our Protocol can only connect, disconnect, and send a message from the client to the server. Unfortunately,
we do not have a working file transfer application set up - BUT we do have a successful 3-way handshake for
starting a connection, and closing a connection.

We had trouble working with threads in our FTA-server so our server, once run through the command line, will always
be listening on the socket you choose. The only way to stop the server is to hit "Ctrl + C" on your keyboard to end
the process.

For the FTA-Client, we have successful 'connect' and 'disconnect' commands but the 'get' only sends a string to the
server that is split up and encapsulated by a packet header.
