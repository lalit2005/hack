// Hack CPU Assembler
// Written in C, by Lalit

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_INSTRUCTIONS 32768
#define MAX_SYMBOLS 500
#define MAX_LINE_LENGTH 100
#define MAX_SYMBOL_LENGTH 100

typedef struct {
  char symbol[MAX_SYMBOL_LENGTH];
  int address;
} Symbol;

Symbol symbolTable[MAX_SYMBOLS];

int instructionCount = 0;
int symbolCount = 0;
int currentAddress = 16;
char *instructions[MAX_INSTRUCTIONS];

char *compMap[28][2] = {
    {"0", "0101010"}, {"1", "0111111"}, {"-1", "0111010"},
    {"D", "0001100"}, {"A", "0110000"}, {"M", "1110000"},
    {"!D", "0001101"}, {"!A", "0110001"}, {"!M", "1110001"},
    {"-D", "0001111"}, {"-A", "0110011"}, {"-M", "1110011"},
    {"D+1", "0011111"}, {"A+1", "0110111"}, {"M+1", "1110111"},
    {"D-1", "0001110"}, {"A-1", "0110010"}, {"M-1", "1110010"},
    {"D+A", "0000010"}, {"D+M", "1000010"}, {"D-A", "0010011"},
    {"D-M", "1010011"}, {"A-D", "0000111"}, {"M-D", "1000111"},
    {"D&A", "0000000"}, {"D&M", "1000000"}, {"D|A", "0010101"},
    {"D|M", "1010101"}
};

char *destMap[8][2] = {
    {"", "000"}, {"M", "001"}, {"D", "010"},
    {"MD", "011"}, {"A", "100"}, {"AM", "101"},
    {"AD", "110"}, {"AMD", "111"}
};

char *jumpMap[8][2] = {
    {"", "000"}, {"JGT", "001"}, {"JEQ", "010"},
    {"JGE", "011"}, {"JLT", "100"}, {"JNE", "101"},
    {"JLE", "110"}, {"JMP", "111"}
};

char *trim(char *str) {
  char *end;
  while (isspace((unsigned int)* str)) {
    str++;
  }
  end = str + strlen(str) - 1;
  while (end > str && isspace((unsigned char)* end)) {
    end--;
  }
  end[1] = '\0';
  return str;
}

void addSymbol(char *symbol, int address) {
  strcpy(symbolTable[symbolCount].symbol, symbol);
  symbolTable[symbolCount].address = address;
  symbolCount++;
}

int getAddress(char *symbol) {
  if (isdigit(symbol[0])) {
    return atoi(symbol);
  }
  for (int i = 0; i < symbolCount; i++) {
    if (strcmp(symbolTable[i].symbol, symbol) == 0) {
      return symbolTable[i].address;
    }
  }
  if (symbol[0] != 'R' || atoi(symbol + 1) > 15) {
    addSymbol(symbol, currentAddress);
    return currentAddress++;
  }
  fprintf(stderr, "Error: Unidentified symbol %s\n", symbol);
  exit(1);
};

char *getComp(char *comp) {
  for (int i = 0;i<28;i++) {
    if (strcmp(compMap[i][0], comp) == 0 ) {
      return compMap[i][1];
    }
  }
  return "0000000";
}

char *getDest(char *dest) {
  for (int i = 0;i<28;i++) {
    if (strcmp(destMap[i][0], dest) == 0 ) {
      return destMap[i][1];
    }
  }
  return NULL;
}

char *getJump(char *jmp) {
  for (int i = 0;i<28;i++) {
    if (strcmp(jumpMap[i][0], jmp) == 0) {
      return jumpMap[i][1];
    }
  }
  return NULL;
}

void processLabels(char *instructions[], int *instructionCount) {
  int currentLineNumber = 0;
  int i = 0;
  while (i < *instructionCount) {
    char *instruction = instructions[i];
    if (instruction[0] == '(') {
      char *label = strtok(instruction + 1, ")");
      addSymbol(label, currentLineNumber);
      
      free(instructions[i]);
      for (int j = i; j < *instructionCount - 1; j++) {
        instructions[j] = instructions[j + 1];
      }
      (*instructionCount)--;
    } else {
      currentLineNumber++;
      i++;
    }
  }
}

char *translateAInx(char *inx) {
  static char binary[17];
  int value;
  if (isdigit(inx[1])) {
    value = atoi(inx + 1);
  } else {
    value = getAddress(inx + 1);
  }
  snprintf(binary, sizeof(binary), "0%015d", 0);
  for (int i = 1; i < 16; i++) {
    binary[i] = (value & (1 << (15 - i))) ? '1' : '0';
  }
  return binary;
}

char *translateCInx(char *inx) {
  static char binary[17];
  char *dest = "";
  char *comp = "";
  char *jmp = "";
  char *eqPos = strchr(inx, '=');
  char *scPos = strchr(inx, ';');

  if (eqPos != NULL) {
    *eqPos = '\0';
    dest = inx;
    comp = eqPos + 1;
  } else {
    comp = inx;
  }

  if (scPos != NULL) {
    *scPos = '\0';
    jmp = scPos + 1;
    if (eqPos == NULL) {
      comp = inx;
    }
  }
  
  char *compCode = getComp(comp);
  char *destCode = getDest(dest);
  char *jmpCode = getJump(jmp);

  snprintf(binary, sizeof(binary), "111%s%s%s", (compCode), (destCode), (jmpCode));

  return binary;
};

void initializeSymbolTable() {
  addSymbol("SP", 0);
  addSymbol("LCL", 1);
  addSymbol("ARG", 2);
  addSymbol("THIS", 3);
  addSymbol("THAT", 4);
  for (int i = 0; i <= 15; i++) {
    char name[4];
    snprintf(name, sizeof(name), "R%d", i);
    addSymbol(name, i);
  }
  addSymbol("SCREEN", 16384);
  addSymbol("KBD", 24576);
}

int main(int argc, char* argv[]) { 
  if (argc != 2) {
    fprintf(stderr, "Usage: %s input.asm output.hack\n", argv[0]);
    return 1;
  }

  FILE* inputFile = fopen(argv[1], "r");
  if (!inputFile) {
    perror("Error opening input file");
    return 1;
  }

  char line[MAX_LINE_LENGTH];
  while (fgets(line, MAX_LINE_LENGTH, inputFile)) {
    char *trimmed = trim(line);
    if (trimmed[0] == '\0' || trimmed[0] == '/' || trimmed[0] == '(') continue;
    instructions[instructionCount++] = strdup(trimmed);
  }

  fclose(inputFile);

  initializeSymbolTable();
  processLabels(instructions, &instructionCount);

  for (int i = 0; i < instructionCount; i++) {
    char *instruction = instructions[i];
    if (instruction[0] == '@') {
      printf("%s\n", translateAInx(instruction));
    } else {
      printf("%s\n", translateCInx(instruction));
    }
  }


  for (int i = 0; i < instructionCount; i++) {
    free(instructions[i]);
  }

  return 0;
}

