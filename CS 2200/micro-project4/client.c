#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <errno.h>
#include <limits.h>
#include <unistd.h>
// Mani Japra
// 902958199
// CS 2200 - Spring 2016

/* The port number you must create a TCP socket at */
const unsigned short PORT = 2200;

#define BUFFER_SIZE 256

struct sockaddr_in socketInfo;

/*
 * The main function where you will be performing the tasks described under the
 * client section of the project description PDF
 */
int main(int argc, char **argv)
{
	char *message;
	char *host;

	/* Making sure that the correct number of command line areguments are being passed through */
	if (argc < 3) {
		fprintf(stderr, "client usage: ./client <server IP address> <string to send>\n");
		exit(-1);
	}
	else
	{
		host = argv[1];
		message = argv[2];
	}
	
	socketInfo.sin_family = AF_INET;
	socketInfo.sin_port = htons(PORT);
	inet_aton(host, &(socketInfo.sin_addr));
	
	int cSocket = socket(AF_INET, SOCK_STREAM, 0);
	int connected = connect(cSocket, (struct sockaddr *) &socketInfo, sizeof(socketInfo));
	
	char recMessage[256];
	if (connected != -1)
	{
		send(cSocket, (char *)message, strlen(message), 0);
		printf("Message Out: %s\n", message);
		recv(cSocket, recMessage, BUFFER_SIZE, 0);
		printf("Message Recieved: %s\n", recMessage);
	}
	
	close(cSocket);
	
	return 0;
}
