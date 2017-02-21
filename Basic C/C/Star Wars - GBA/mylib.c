// Name: Mani Japra

typedef unsigned short u16;

u16 *videoBuffer = (u16 *) 0x6000000;

#define SCANLINECOUNTER *(volatile unsigned short *) 0x4000006

//Prototypes:
void setPixel(int r, int c, u16 color);
void drawRect(int r, int c, int width, int height, u16 color);
void drawHollowRect(int r, int c, int width, int height, u16 color);
void drawImage3(int r, int c, int width, int height, const u16* image);
void waitForVblank();

void setPixel(int r, int c, u16 color) 
{
	videoBuffer[r * 240 + c] = color;
}

void drawRect(int r, int c, int width, int height, u16 color) 
{
	for(int curR = 0; curR < height; curR++) {
		for(int curC = 0; curC < width; curC++) {
			setPixel(curR + r, curC + c, color);
		}
	}
}

void drawHollowRect(int r, int c, int width, int height, u16 color) 
{
	for(int curR = 0; curR < height; curR++) {
		for(int curC = 0; curC < width; curC++) {
			if ((curR == 0) || (curC == 0) || (curR == height) || (curC == width)) {
				setPixel(curR + r, curC + c, color);
			}
		}
	}
}

void drawImage3(int r, int c, int width, int height, const u16* image) 
{
	int offset = 0;
	for (int curH = r; curH < (height + r); curH++) {
		for (int curW = c; curW < (width + c); curW++) {
			videoBuffer[curH * 240 + curW] = image[offset];
			offset++;
		}
	}
}

void waitForVblank()
{
	while(SCANLINECOUNTER > 160);
	while(SCANLINECOUNTER < 160);
}
