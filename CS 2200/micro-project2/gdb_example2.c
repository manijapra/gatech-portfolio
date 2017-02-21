#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define BUFFERSIZE 45

void setint(int* ip, int i) {
    *ip = i;
} 

void write_message(char *message) {
    char buffer[BUFFERSIZE];

    memset(buffer, '\0', BUFFERSIZE);
    strcpy(buffer, message);
    printf("%s\n", buffer);
}

int main() {
    char message[] = "Look, this seems like an innocent message!";
    int a;
    static int c;
    int b;
    a++;
    setint(&a, 10);
    printf("%d\n", a);
    setint(&b, 20);
    c = a + b;
    printf("%d\n", c);

    write_message(message);

    return 0;
}
