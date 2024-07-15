// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Mult.asm

// Multiplies 0 and 1 and stores the result in 2.
// (0, 1, 2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
// The algorithm is based on repetitive addition.

// A - register address
// M - RAM[A]
// D - temp 16-bit
// add ram[0], ram[1] times


// @i
// M=0
// @R2
// M=0
//
// (LOOP)
//   @i
//   D=M
//   @R1
//   D=D-M
//   @END
//   D;JGT
//   @i
//   D=M
//  @R2
//  M=D+M
//   @i
//   M=M+1
//   @LOOP
//   0;JMP

// (END)
//   @END
//   0;JMP


// @R2


// let r3 = 0
// LOOP:
//   if r2 = 0; goto STOP
//   r3=r3+r1
//   r2=r2-1
//   goto loop
// STOP:
//   infinite loop


@R2
M=0

(LOOP)
  @R1
  D=M
  @END
  D;JEQ

  @R0
  D=M
  @R2
  M=D+M

  @R1
  M=M-1
  @LOOP
  0;JMP

(END)
  @END
  0;JMP
