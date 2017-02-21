// bmptoc.c
// Name: Mani Japra

#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <ctype.h>

#define ARRAYLENGTH ((0x36) + (240) * (160) * (4))

// This is the array into which you will load the raw data from the file
// You don't have to use this array if you don't want to, but you will be responsible
// for any errors caused by erroneously using the stack or malloc if you do it a
// different way, incorrectly!

char data_arr[0x36 + 240 * 160 * 4];

int main(int argc, char *argv[]) {

	// 1. Make sure the user passed in the correct number of arguments
	if (argc != 2) return -1;


	// 2. Open the file; if it doesn't exist, tell the user and then exit
	char *fname = argv[1];
	
	FILE *f = fopen(fname, "r");

	if (f == NULL) {
		printf("Unfortunately, you have decided to not use your brain.\n");
		printf("The file does not exist.\n");
		return -1;
	}


	// 3. Read the file into the buffer then close it when you are done
	fread(data_arr, 1, ARRAYLENGTH, f);
	fclose(f);


	// 4. Get the width and height of the image
	unsigned int w = *(unsigned int*) (data_arr + 0x12);
	unsigned int h = *(unsigned int*) (data_arr + 0x16);


	// 5. Create header file, and write header contents; close it
	char newFileName[100],
		nameUpperCase[100],
		nameLowerCase[100];

	int periodLoc = strlen(fname) - 4;

	strcpy(newFileName, fname);
	strcpy(nameUpperCase, fname);
	strcpy(nameLowerCase, fname);

	newFileName[periodLoc] = '\0';
	nameUpperCase[periodLoc] = '\0';
	nameLowerCase[periodLoc] = '\0';

	//Manipulate newFileName to Add .h
	sprintf(newFileName, "%s.h", newFileName);

	//Make nameUpperCase --> Upper Case
	int k = 0;
	
	while (nameUpperCase[k]) {
		nameUpperCase[k] = toupper(nameUpperCase[k]);
		k++;
	}
	

	//Make & Write to 'Something.h'
	FILE *newFile = fopen(newFileName, "w");

	fprintf(newFile, "#define %s_WIDTH %d\n", nameUpperCase, w);
	fprintf(newFile, "#define %s_HEIGHT %d\n", nameUpperCase, h);
	fprintf(newFile, "const unsigned short %s_data[%d];\n", nameLowerCase, w*h);

	fclose(newFile);

	// 6. Create C file, and write pixel data; close it
	char cFileName[100];
	strcpy(cFileName, fname);
	
	cFileName[periodLoc] = '\0';
	sprintf(cFileName, "%s.c", cFileName);

	FILE *cF = fopen(cFileName, "w");

	fprintf(cF, "const unsigned short %s_data[%d] = { \n", nameLowerCase, w*h);

	unsigned int *p = (unsigned int*) (data_arr + 0x36);

	for (int curH = h - 1; curH >= 0; curH--) {
		for (unsigned int curW = 0; curW < w; curW++) {
			unsigned int px = p[w*curH+curW];
			
			unsigned char rVal = (px >> 16) & 0xFF;
			rVal = rVal >> 3;
			unsigned char gVal = (px >> 8) & 0xFF;
			gVal = gVal >> 3;
			unsigned char bVal = (px) & 0xFF;
			bVal = bVal >> 3;
			
			unsigned int finalPx = (bVal << 10) | (gVal << 5) | (rVal);
			fprintf(cF, "0x%x, \n", finalPx);
		}
	}

	fprintf(cF, "\n};");
	fclose(cF);

	return 0;
}

