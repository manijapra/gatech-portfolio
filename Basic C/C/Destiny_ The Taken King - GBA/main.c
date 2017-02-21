#include <stdlib.h>
#include <stdio.h>

#include "mylib.h"
#include "text.h"
#include "titlescreen.h"
#include "gamescreen.h"
#include "ship.h"

enum {SPLASH, GAME, WIN, LOSE};

typedef struct
{
	int row;
	int oldrow;
	int col;
	int oldcol;
	int rd;
	int cd;
	int width;
	int height;
	const u16* show;
	const u16* a1;
	const u16* a2;
	const u16* a3;
	const u16* a4;
} SHIP;

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

void splash();
void win();
void lose();
int game();

int main()
{
	videoBuffer = BUFFER0;
	REG_DISPCTL = MODE4 | BG2_ENABLE;
	int state = SPLASH;
	
	while(1)
	{
		switch(state)
		{
		case SPLASH:
			splash();
			state = GAME;
			break;
		case GAME:
			state = game();
			break;
		case WIN:
			win();
			state = SPLASH;
			break;
		case LOSE:
			lose();
			state = SPLASH;
			break;
		default:
			break;
		}
	}
}

void splash()
{
	setPalette(titlescreen_palette, TITLESCREEN_PALETTE_SIZE);
	
	PageFlip();
	
	drawImage4(0,0, TITLESCREEN_WIDTH, TITLESCREEN_HEIGHT, titlescreen);
	
	char title[40] = "Welcome to Destiny: The Taken King!"; 
	char subTitle[40] = "Created By: Mani Japra";
	char pressAToPlay[40] = "Press 'A' to Play!";
	
	drawString4(130,15,title,250);
	drawString4(140,55,subTitle,250);
 	drawString4(150,70,pressAToPlay,250);
	waitForVblank();
			
	PageFlip();
	
	while(!KEY_DOWN_NOW(BUTTON_A));
}

int game()
{
	PageFlip();
	setPalette(ship_palette, SHIP_PALETTE_SIZE);
	int healthRem = 100;
	int kingHealth = 100;
	int anim = 0;

	SHIP s[1];
	s->row = 100;
	s->col = 10;
	s->oldrow = 10;
	s->oldcol = 10;
	s->rd = 5;
	s->cd = 5;
	s->width = F1_WIDTH;
	s->height = F1_HEIGHT;
	s->a1 = f1;
	s->a2 = f2;
	s->a3 = f3;
	s->a4 = f4;
	s->show = s->a1;
	
	SHIP king[1];
	king->row = 10;
	king->col = 180;
	king->oldrow = 10;
	king->oldcol = 10;
	king->rd = 5;
	king->cd = 5;
	king->width = TKING_WIDTH;
	king->height = TKING_HEIGHT;
	king->a1 = tking;
	king->show = king->a1;
	
	BULLET bul[1];
	bul->row = (s->row);
	bul->col = (s->col) + 31;
	bul->cd = 3;
	bul->rd = 0;
	bul->height = 2;
	bul->width = 2;
	bul->hit = 0;
	bul->shot = 0;
	
	BULLET oryx[1];
	oryx->row = (king->row) + 60;
	oryx->col = (king->col) + 30;
	oryx->cd = 0;
	oryx->rd = 3;
	oryx->height = 2;
	oryx->width = 2;
	oryx->hit = 0;
	oryx->shot = 0;
	
	
	char gameString[40] = "Your Health: ";
	fillScreen4(0);
	char kingString[40] = "Oryx's Health: ";
	
	drawImage4(s->row, s->col, s->width, s->height, s->show);
	drawImage4(king->row, king->col, king->width, king->height, king->show);
	
	sprintf(gameString, "Your Health: %d", healthRem);
	drawString4(140,0,gameString,250);
	sprintf(kingString, "Oryx's Health: %d", kingHealth);
	drawString4(150,0,kingString,250);
	
	waitForVblank();
	PageFlip();
	while(!KEY_DOWN_NOW(BUTTON_SELECT))
	{
		fillScreen4(0);
		if (anim == 0)
		{
			s->show = s->a1;
			anim++;
		}
		else if (anim == 1)
		{
			s->show = s->a2;
			anim++;
		}
		else if (anim == 2)
		{
			s->show = s->a3;
			anim++;
		}
		else if (anim == 3)
		{
			s->show = s->a4;
			anim = 0;
		}
		
		drawRect4(king->oldrow, king->oldcol, king->width, king->height, 0);
		king->oldrow = king->row;
		king->oldcol = king->col;
		king->row = king->row + king->rd;
		king->col =  king->col + king->cd;
		checkBoundary(king->row < 0, &(king->row), 0, &(king->rd), -king->rd);
		checkBoundary(king->row > ((80)-(king->height)+1), &(king->row), ((80)-(king->height)+1), &(king->rd), -king->rd);
		
		checkBoundary(king->col < 0, &(king->col), 0, &(king->cd), -king->cd);
		checkBoundary(king->col > ((240)-(king->width)+1), &(king->col), ((240)-(king->width)+1), &(king->cd), -king->cd);
		
		/* Ship Movement Begin */
		
		if(KEY_DOWN_NOW(BUTTON_DOWN))
		{
			drawRect4(s->oldrow, s->oldcol, s->width, s->height, 0);
			s->oldrow = s->row;
			s->oldcol = s->col;
			
			s->row = s->row + s->rd;
			checkBoundary(s->row < 80, &(s->row), 80, &(s->rd), s->rd);
			checkBoundary(s->row > ((160)-(s->height)+1), &(s->row), ((160)-(s->height)+1), &(s->rd), s->rd);
			
			checkBoundary(s->col < 0, &(s->col), 0, &(s->cd), s->cd);
			checkBoundary(s->col > ((240)-(s->width)+1), &(s->col), ((240)-(s->width)+1), &(s->cd), s->cd);

			
		}
		else if(KEY_DOWN_NOW(BUTTON_UP))
		{
			drawRect4(s->oldrow, s->oldcol, s->width, s->height, 0);
			s->oldrow = s->row;
			s->oldcol = s->col;
			
			s->row = s->row - s->rd;
			checkBoundary(s->row < 80, &(s->row), 80, &(s->rd), s->rd);
			checkBoundary(s->row > ((160)-(s->height)+1), &(s->row), ((160)-(s->height)+1), &(s->rd), s->rd);
			
			checkBoundary(s->col < 0, &(s->col), 0, &(s->cd), s->cd);
			checkBoundary(s->col > ((240)-(s->width)+1), &(s->col), ((240)-(s->width)+1), &(s->cd), s->cd);
	
		}
		else if(KEY_DOWN_NOW(BUTTON_RIGHT))
		{
			drawRect4(s->oldrow, s->oldcol, s->width, s->height, 0);
			s->oldrow = s->row;
			s->oldcol = s->col;
			
			s->col = s->col + s->cd;
			checkBoundary(s->row < 80, &(s->row), 80, &(s->rd), s->rd);
			checkBoundary(s->row > ((160)-(s->height)+1), &(s->row), ((160)-(s->height)+1), &(s->rd), s->rd);
			
			checkBoundary(s->col < 0, &(s->col), 0, &(s->cd), s->cd);
			checkBoundary(s->col > ((240)-(s->width)+1), &(s->col), ((240)-(s->width)+1), &(s->cd), s->cd);
			
		}
		else if(KEY_DOWN_NOW(BUTTON_LEFT))
		{
			drawRect4(s->oldrow, s->oldcol, s->width, s->height, 0);
			s->oldrow = s->row;
			s->oldcol = s->col;
			
			s->col = s->col - s->cd;
			checkBoundary(s->row < 80, &(s->row), 80, &(s->rd), s->rd);
			checkBoundary(s->row > ((160)-(s->height)+1), &(s->row), ((160)-(s->height)+1), &(s->rd), s->rd);
			
			checkBoundary(s->col < 0, &(s->col), 0, &(s->cd), s->cd);
			checkBoundary(s->col > ((240)-(s->width)+1), &(s->col), ((240)-(s->width)+1), &(s->cd), s->cd);
			
		}
		
		/* Ship Movement End */
		
		if(KEY_DOWN_NOW(BUTTON_A))
		{
			if(bul->shot == 0)
			{
			  	bul->row = (s->row);
				bul->col = (s->col) + 31;
				bul->cd = 0;
				bul->rd = -3;
				bul->height = 2;
				bul->width = 2;
				bul->hit = 0;
				bul->shot = 1;
			}
		}
		
		int dc = (king->col - (s->col + 10));
		if (dc < 0) dc = dc * -1;
		if (dc < 10)
		{
			oryx->row = (king->row) + 60;
			oryx->col = (king->col) + 30;
			oryx->cd = 0;
			oryx->rd = 3;
			oryx->height = 2;
			oryx->width = 2;
			oryx->hit = 0;
			oryx->shot = 1;
		}
		
		
		if (bul->shot != 0)
		{
			drawRect4((bul->row), (bul->col), (bul->width), (bul->height), 0);
			bul->row = bul->row + bul->rd;
			bul->col = bul->col + bul->cd;
			if (bul->row > 160 || bul->row < 0 || bul->col > 240 || bul->col < 0) bul->shot = 0;
			int deltaR = (bul->row - (king->row + 30));
			if (deltaR < 0) deltaR = deltaR * -1;
			int deltaC = (bul->col - (king->col + 30));
			if (deltaC < 0) deltaC = deltaC * -1;
			if (deltaR < 10 && deltaC < 10)
			{
				kingHealth = kingHealth - 25;
				if (kingHealth == 0) return WIN;
				bul->shot = 0;
			}
		}
		
		if (oryx->shot != 0)
		{
			drawRect4((oryx->row), (oryx->col), (oryx->width), (oryx->height), 0);
			oryx->row = oryx->row + oryx->rd;
			oryx->col = oryx->col + oryx->cd;
			if (oryx->row > 160 || oryx->row < 0 || oryx->col > 240 || oryx->col < 0) oryx->shot = 0;
			int dr = (oryx->row - (s->row + 31));
			if (dr < 0) dr = dr * -1;
			int dC = (oryx->col - (s->col + 31));
			if (dC < 0) dC = dC * -1;
			if (dr < 5 && dC < 5)
			{
				healthRem = healthRem - 25;
				if (healthRem == 0) return LOSE;
				oryx->shot = 0;
			}
		}
		drawImage4(king->row, king->col, king->width, king->height, king->show);
		drawImage4(s->row, s->col, s->width, s->height, s->show);
		sprintf(gameString, "Your Health: %d", healthRem);
		drawString4(140,0,gameString,250);
		sprintf(kingString, "Oryx's Health: %d", kingHealth);
		drawString4(150,0,kingString,250);
		if (bul->shot != 0)
		{
			drawRect4((bul->row), (bul->col), bul->width, bul->height, 250);
		}
		if (oryx->shot != 0)
		{
			drawRect4((oryx->row), (oryx->col), oryx->width, oryx->height, 100);
		}
		waitForVblank();
		PageFlip();
	}
	
	return SPLASH;
}

void win()
{
	setPalette(gamescreen_palette, GAMESCREEN_PALETTE_SIZE);
	
	drawImage4(0,0, GAMESCREEN_WIDTH, GAMESCREEN_HEIGHT, gamescreen);
	
	char winText[40] = "Congratulations Guardian!";
	char winText2[40] = "You have saved the Traveler";
	char winText3[40] = "from the Darkness.";

	drawString4(70,50,winText,250);
	drawString4(100,40,winText2,250);
 	drawString4(110,70,winText3,250);

	waitForVblank();
	PageFlip();
	
	while(!KEY_DOWN_NOW(BUTTON_SELECT));
}

void lose()
{
	setPalette(gamescreen_palette, GAMESCREEN_PALETTE_SIZE);
	
	drawImage4(0,0, GAMESCREEN_WIDTH, GAMESCREEN_HEIGHT, gamescreen);
	
	char winText[40] = "You've lost Guardian...";
	char winText2[40] = "You failed to save the Traveler";
	char winText3[40] = "from the Darkness.";

	drawString4(70,50,winText,250);
	drawString4(100,20,winText2,250);
 	drawString4(110,65,winText3,250);

	waitForVblank();
	PageFlip();
	
	while(!KEY_DOWN_NOW(BUTTON_SELECT));
}
