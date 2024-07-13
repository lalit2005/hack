// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed,
// the screen should be cleared.

(LOOP)
  @8192
  D=A
  @i
  M=D
  @KBD
  D=M
  @START
  D;JNE
  @WHITE
  D;JEQ

(START)
  @SCREEN
  D=A
  @i
  D=D+M
  A=D
  M=-1
  @i
  D=M
  @LOOP
  D;JEQ
  @i
  M=M-1
  @START
  0;JMP

(WHITE)
  @SCREEN
  D=A
  @i
  D=D+M
  A=D
  M=0
  @i
  D=M
  @LOOP
  D;JEQ
  @i
  M=M-1
  @WHITE
  0;JMP
