extern const unsigned char fontdata_6x8[12288];

/* Prototypes */
void drawChar(int row, int col, char ch, u16 color);
void drawString(int row, int col, char *str, u16 color);
void drawChar4(int row, int col, char ch, u8 index);
void drawString4(int row, int col, char *str, u8 index);
