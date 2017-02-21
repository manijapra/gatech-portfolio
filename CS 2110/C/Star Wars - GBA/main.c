//Name: Mani Japra

typedef unsigned short u16;

#include "StarWarsTitleScreen.h"
#include "shipup.h"
#include "shipdown.h"
#include "shipright.h"
#include "shipleft.h"
#include "tiefighter.h"
#include "winscreen.h"
#include "fullhealth.h"
#include "thirdhealth.h"
#include "lowhealth.h"
#include <stdlib.h>

#define REG_DISPCNT *(unsigned short *) 0x4000000
#define MODE3 3
#define BG2_ENABLE (1<<10)
#define RGB(r, g, b) ((r) | (g)<<5 | (b)<<10)

#define RED RGB(31, 0 , 0)
#define GREEN RGB(0, 31, 0)
#define BLUE RGB(0, 0, 31)
#define WHITE RGB(31, 31, 31)
#define BLACK 0
#define YELLOW RGB(31, 31, 0)
#define CYAN RGB(0, 31, 31)
#define MAGENTA RGB(31, 0, 31)

#define BUTTON_A (1<<0)
#define BUTTON_B (1<<1)
#define BUTTON_SELECT (1<<2)
#define BUTTON_START (1<<3)
#define BUTTON_RIGHT (1<<4)
#define BUTTON_LEFT (1<<5)
#define BUTTON_UP (1<<6)
#define BUTTON_DOWN (1<<7)
#define BUTTON_R (1<<8)
#define BUTTON_L (1<<9)

#define KEY_DOWN_NOW(key) (~(BUTTONS) & key)

#define BUTTONS *(volatile unsigned int *) 0x4000130
#define size 10

extern u16 *videoBuffer;

typedef struct
{
	int row;
	int oldrow;
	int col;
	int oldcol;
	int rd;
	int cd;
	const u16* image;
	int height;
	int width;
} EMPIRESHIP;

typedef struct
{
	int row;
	int oldrow;
	int col;
	int oldcol;
	int rd;
	int cd;
	const u16* image;
	int height;
	int width;
	int dir;
} FALCON;

typedef struct
{
	int row;
	int col;
	int rd;
	int cd;
	int height;
	int width;
	int hit;
	int shot;
} BULLET;

//Prototypes:
void setPixel(int r, int c, u16 color);
void drawRect(int r, int c, int width, int height, u16 color);
void drawHollowRect(int r, int c, int width, int height, u16 color);
void drawImage3(int r, int c, int width, int height, const u16* image);
void waitForVblank();

void checkBoundary(int cond, int *var1, int set1, int *var2, int set2);

int main(void) 
{
	REG_DISPCNT = MODE3 | BG2_ENABLE;
	int titleScreen = 1;

	EMPIRESHIP ship[1];
	ship->row = 70+rand()%20;
	ship->col = 110+rand()%20;
	ship->rd = 3;
	ship->cd = 4;
	ship->image = tiefighter_data;
	ship->height = TIEFIGHTER_HEIGHT;
	ship->width = TIEFIGHTER_WIDTH;
	
	FALCON fal[1];
	fal->row = 50+rand()%20;
	fal->col = 10+rand()%20;
	fal->oldrow = fal->row;
	fal->oldcol = fal->col;
	fal->rd = 2;
	fal->cd = 2;
	fal->image = shipright_data;
	fal->height = SHIPRIGHT_HEIGHT;
	fal->width = SHIPRIGHT_WIDTH;
	fal->dir = 0;
	
	BULLET bul[1];
	bul->shot = 0;
	bul->row = (fal->row);
	bul->col = (fal->col);
	bul->cd = 3;
	bul->rd = 0;
	bul->height = 2;
	bul->width = 2;
	bul->hit = 0;
	bul->shot = 0;

	int caughtTieFighter = 0;
	


	drawImage3(0, 0, STARWARSTITLESCREEN_WIDTH, STARWARSTITLESCREEN_HEIGHT, StarWarsTitleScreen_data);
	while(1) //<-- Game Loop
	{
		if (caughtTieFighter == 3)
		{
			titleScreen = 2;
			drawImage3(0, 0, WINSCREEN_WIDTH, WINSCREEN_HEIGHT, winscreen_data);
		}
		if (KEY_DOWN_NOW(BUTTON_A))
		{
			if (titleScreen == 1) 
			{
				drawRect(0, 0, 240, 160, BLACK);
				drawImage3((ship->row), (ship->col), (ship->width), (ship->height), (ship->image));
				drawImage3((fal->row), (fal->col), (fal->width), (fal->height), (fal->image));
				titleScreen = 0;
			}
			else if (titleScreen == 0)
			{
				if(bul->shot == 0)
				{
					if(fal->dir == 0)
					{
					  	bul->row = (fal->row) + 20;
						bul->col = (fal->col) + 40;
						bul->cd = 3;
						bul->rd = 0;
						bul->height = 2;
						bul->width = 2;
						bul->hit = 0;
						bul->shot = 1;
					}
					else if(fal->dir == 1)
					{
						bul->row = (fal->row) + 40;
						bul->col = (fal->col) + 20;
						bul->cd = 0;
						bul->rd = 3;
						bul->height = 2;
						bul->width = 2;
						bul->hit = 0;
						bul->shot = 1;
					}
					else if(fal->dir == 2)
					{
						bul->row = (fal->row) + 20;
						bul->col = (fal->col);
						bul->cd = -3;
						bul->rd = 0;
						bul->height = 2;
						bul->width = 2;
						bul->hit = 0;
						bul->shot = 1;
					}
					else if(fal->dir == 3)
					{
						bul->row = (fal->row);
						bul->col = (fal->col) + 20;
						bul->cd = 0;
						bul->rd = -3;
						bul->height = 2;
						bul->width = 2;
						bul->hit = 0;
						bul->shot = 1;
					}
				}
			}
		}
		else if (KEY_DOWN_NOW(BUTTON_SELECT))
		{
			drawImage3(0, 0, STARWARSTITLESCREEN_WIDTH, STARWARSTITLESCREEN_HEIGHT, StarWarsTitleScreen_data);
			titleScreen = 1;
			caughtTieFighter = 0;
		}
		else if(KEY_DOWN_NOW(BUTTON_DOWN))
		{
			waitForVblank();
			drawRect((fal->row), (fal->col), (fal->width), (fal->height), BLACK);
			fal->width = SHIPDOWN_WIDTH;
			fal->height = SHIPDOWN_HEIGHT;
			fal->image = shipdown_data;
			
			fal->row = fal->row + fal->rd;
			fal->dir = 1;
			checkBoundary(fal->row < 0, &(fal->row), 0, &(fal->rd), fal->rd);
			checkBoundary(fal->row > ((160)-(fal->height)+1), &(fal->row), ((160)-(fal->height)+1), &(fal->rd), fal->rd);
			
			checkBoundary(fal->col < 0, &(fal->col), 0, &(fal->cd), fal->cd);
			checkBoundary(fal->col > ((120)-(fal->width)+1), &(fal->col), ((120)-(fal->width)+1), &(fal->cd), fal->cd);

			drawImage3((fal->row), (fal->col), (fal->width), (fal->height), (fal->image));
		}
		else if(KEY_DOWN_NOW(BUTTON_UP))
		{
			waitForVblank();
			drawRect((fal->row), (fal->col), (fal->width), (fal->height), BLACK);
			fal->width = SHIPUP_WIDTH;
			fal->height = SHIPUP_HEIGHT;
			fal->image = shipup_data;

			fal->row = fal->row - fal->rd;
			fal->dir = 3;
			checkBoundary(fal->row < 0, &(fal->row), 0, &(fal->rd), fal->rd);
			checkBoundary(fal->row > ((160)-(fal->height)+1), &(fal->row), ((160)-(fal->height)+1), &(fal->rd), fal->rd);
			
			checkBoundary(fal->col < 0, &(fal->col), 0, &(fal->cd), fal->cd);
			checkBoundary(fal->col > ((120)-(fal->width)+1), &(fal->col), ((120)-(fal->width)+1), &(fal->cd), fal->cd);
			drawImage3((fal->row), (fal->col), (fal->width), (fal->height), (fal->image));
		}
		else if(KEY_DOWN_NOW(BUTTON_RIGHT))
		{
			waitForVblank();
			drawRect((fal->row), (fal->col), (fal->width), (fal->height), BLACK);
			fal->width = SHIPRIGHT_WIDTH;
			fal->height = SHIPRIGHT_HEIGHT;
			fal->image = shipright_data;

			fal->col = fal->col + fal->cd;
			fal->dir = 0;
			checkBoundary(fal->row < 0, &(fal->row), 0, &(fal->rd), fal->rd);
			checkBoundary(fal->row > ((160)-(fal->height)+1), &(fal->row), ((160)-(fal->height)+1), &(fal->rd), fal->rd);
			
			checkBoundary(fal->col < 0, &(fal->col), 0, &(fal->cd), fal->cd);
			checkBoundary(fal->col > ((120)-(fal->width)+1), &(fal->col), ((120)-(fal->width)+1), &(fal->cd), fal->cd);
			drawImage3((fal->row), (fal->col), (fal->width), (fal->height), (fal->image));
		}
		else if(KEY_DOWN_NOW(BUTTON_LEFT))
		{
			waitForVblank();
			drawRect((fal->row), (fal->col), (fal->width), (fal->height), BLACK);
			fal->width = SHIPLEFT_WIDTH;
			fal->height = SHIPLEFT_HEIGHT;
			fal->image = shipleft_data;

			fal->col = fal->col - fal->cd;
			fal->dir = 2;
			checkBoundary(fal->row < 0, &(fal->row), 0, &(fal->rd), fal->rd);
			checkBoundary(fal->row > ((160)-(fal->height)+1), &(fal->row), ((160)-(fal->height)+1), &(fal->rd), fal->rd);
			
			checkBoundary(fal->col < 0, &(fal->col), 0, &(fal->cd), fal->cd);
			checkBoundary(fal->col > ((120)-(fal->width)+1), &(fal->col), ((120)-(fal->width)+1), &(fal->cd), fal->cd);
			drawImage3((fal->row), (fal->col), (fal->width), (fal->height), (fal->image));
		}
		if (titleScreen == 0)
		{
			ship->oldrow = ship->row;
			ship->oldcol = ship->col;
			ship->row = ship->row + ship->rd;
			ship->col =  ship->col + ship->cd;
			checkBoundary(ship->row < 0, &(ship->row), 0, &(ship->rd), -ship->rd);
			checkBoundary(ship->row > ((160)-(ship->height)+1), &(ship->row), ((160)-(ship->height)+1), &(ship->rd), -ship->rd);
			
			checkBoundary(ship->col < 120, &(ship->col), 120, &(ship->cd), -ship->cd);
			checkBoundary(ship->col > ((240)-(ship->width)+1), &(ship->col), ((240)-(ship->width)+1), &(ship->cd), -ship->cd);
			if (bul->shot != 0)
			{
				waitForVblank();
				drawRect((bul->row), (bul->col), (bul->width), (bul->height), BLACK);
				bul->row = bul->row + bul->rd;
				bul->col = bul->col + bul->cd;
				if (bul->row > 160 || bul->row < 0 || bul->col > 240 || bul->col < 0) bul->shot = 0;
				int deltaR = (bul->row - (ship->row + 10));
				if (deltaR < 0) deltaR = deltaR * -1;
				int deltaC = (bul->col - (ship->col + 10));
				if (deltaC < 0) deltaC = deltaC * -1;
				if (deltaR < 10 && deltaC < 10)
				{
					caughtTieFighter++;
					bul->shot = 0;
				}
			}
			
		}
		waitForVblank();
		if (titleScreen == 0)
		{	
			drawRect((ship->oldrow), (ship->oldcol), (ship->width), (ship->height), BLACK);
			//drawRect((fal->oldrow), (fal->oldcol), (fal->width), (fal->height), BLACK);
			drawImage3((ship->row), (ship->col), (ship->width), (ship->height), (ship->image));
			drawImage3((fal->row), (fal->col), (fal->width), (fal->height), (fal->image));
			if (bul->shot != 0)
			{
				drawRect((bul->row), (bul->col), bul->width, bul->height, GREEN);
			}
			if (caughtTieFighter == 0) drawImage3(0, 240 - FULLHEALTH_WIDTH, FULLHEALTH_WIDTH, FULLHEALTH_HEIGHT, fullhealth_data);
			else if (caughtTieFighter == 1) drawImage3(0, 240 - FULLHEALTH_WIDTH, THIRDHEALTH_WIDTH, THIRDHEALTH_HEIGHT, thirdhealth_data);
			else if (caughtTieFighter == 2) drawImage3(0, 240 - FULLHEALTH_WIDTH, LOWHEALTH_WIDTH, LOWHEALTH_HEIGHT, lowhealth_data);
		}
	}
}

void checkBoundary(int cond, int *var1, int set1, int *var2, int set2)
{
	if (cond)
	{
		*var1 = set1;
		*var2 = set2;
	}
}





