// Name: Mani Japra (902958199)

#include "mylib.h"

unsigned short *videoBuffer = (u16 *)0x6000000;

void setPixel(int r, int c, u16 color) 
{
	videoBuffer[r * 240 + c] = color;
}

void setPixel4(int row, int col, u8 index)
{
	int pixel = row*240+col;
	int whichShort = pixel/2;
	if(col&1)
	{
		// Odd column must insert index into left side
		videoBuffer[whichShort] = (videoBuffer[whichShort] & 0x00FF) | (index<<8);
	}
	else
	{
		// Even column
		videoBuffer[whichShort] = (videoBuffer[whichShort] & 0xFF00) | index;
	}

}

void drawRect(int r, int c, int width, int height, u16 color) 
{
	for(int curR = 0; curR < height; curR++) {
		for(int curC = 0; curC < width; curC++) {
			setPixel(curR + r, curC + c, color);
		}
	}
}

void drawRect4(int row, int col, int height, int width, u8 index)
{
	int r,c;
	for(r=0; r<height; r++)
	{
		for(c=0; c<width; c++)
		{
			setPixel4(row+r, col+c, index);
		}
	}
}

void fillScreen4(u8 index)
{	
	volatile u16 color = (index<<8) | index;
	DMA[3].src = &color;
	DMA[3].dst = videoBuffer;
	DMA[3].cnt = DMA_ON | DMA_SOURCE_FIXED | 19200;	
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

void drawImage4(int r, int c, int width, int height, const u16* image)
{
	int row;
	for (row = 0; row < height; row++)
	{
		DMA[3].src = &image[(row*width)/2];
		DMA[3].dst = &videoBuffer[(OFFSET(row+r, c)/2)];
		DMA[3].cnt = DMA_ON | (width/2);
	}
}

void fillImage4(const u16 *img)
{
	DMA[3].src = img;
	DMA[3].dst = videoBuffer;
	DMA[3].cnt = 19200 | DMA_ON;
}

void PageFlip()
{
	if (REG_DISPCTL & BUFFER1FLAG)
	{
		//display buffer 1, videoBuffer was Buffer 0
		REG_DISPCTL = REG_DISPCTL & (~BUFFER1FLAG);
		videoBuffer = BUFFER1;
	}
	else
	{
		REG_DISPCTL = REG_DISPCTL | (BUFFER1FLAG);
		videoBuffer = BUFFER0;
	}
}

void setPalette(const u16 *palette, int size)
{
	DMA[3].src = palette;
	DMA[3].dst = PALETTE;
	DMA[3].cnt = DMA_ON | size;
}

void waitForVblank()
{
	while(SCANLINECOUNTER > 160);
	while(SCANLINECOUNTER < 160);
}

void checkBoundary(int cond, int *var1, int set1, int *var2, int set2)
{
	if (cond)
	{
		*var1 = set1;
		*var2 = set2;
	}
}
