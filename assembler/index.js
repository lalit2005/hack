// Hack CPU Assembler

import fs from "node:fs";
import { argv } from "node:process";

if (!argv[1]) {
  throw new Error("No asm file given");
}
const filePath = argv[2];
const fileContents = fs.readFileSync(filePath);
const instructions = fileContents
  .toString()
  .split("\n")
  .map((a) => a.trim())
  .filter((a) => {
    return !a.startsWith("//") && a; // '& a' ensures that "" are not considered as valid instructions
  });

// Symbol table
let symbolTable = {
  R0: 0,
  R1: 1,
  R2: 2,
  R3: 3,
  R4: 4,
  R5: 5,
  R6: 6,
  R7: 7,
  R8: 8,
  R9: 9,
  R10: 10,
  R11: 11,
  R12: 12,
  R13: 13,
  R14: 14,
  R15: 15,
  SP: 0,
  LCL: 1,
  ARG: 2,
  THIS: 3,
  THAT: 4,
  SCREEN: 16384,
  KBD: 24576,
};

const destinationTable = {
  null: "000",
  M: "001",
  D: "010",
  MD: "011",
  A: "100",
  AM: "101",
  AD: "110",
  AMD: "111",
};

const computationTable = {
  0: "101010",
  1: "111111",
  "-1": "111010",
  D: "001100",
  A: "110000",
  M: "110000",
  "!D": "001101",
  "!A": "110001",
  "!M": "110001",
  "-D": "001111",
  "-A": "110011",
  "-M": "110011",
  "D+1": "011111",
  "A+1": "110111",
  "M+1": "110111",
  "D-1": "001110",
  "A-1": "110010",
  "M-1": "110010",
  "D+A": "000010",
  "D+M": "000010",
  "D-A": "010001",
  "D-M": "010011",
  "A-D": "000111",
  "M-D": "000111",
  "D&A": "000000",
  "D&M": "000000",
  "D|A": "010101",
  "D|M": "010101",
};

const jmpTable = {
  null: "000",
  JGT: "001",
  JEQ: "010",
  JGE: "011",
  JLT: "100",
  JNE: "101",
  JLE: "110",
  JMP: "111",
};

let parseAsm = (instructions) => {
  let ir = [];
  registerSymbols(instructions);
  instructions
    .filter((a) => !a.startsWith("("))
    .forEach((instruction, i) => {
      if (instruction.startsWith("@")) {
        ir[i] = {
          instructionType: "A",
          value: parseAInx(instruction.trim()),
        };
      } else {
        ir[i] = {
          instructionType: "C",
          value: parseCInx(instruction.trim()),
        };
      }
    });

  let finalResult = "";
  for (let i = 0; i < ir.length; i++) {
    const inx = ir[i];
    console.log(inx.value);
    finalResult += inx.value + "\n";
  }
  fs.writeFileSync("result.txt", finalResult);
};

let parseAInx = (instruction) => {
  if (Number.isInteger(+instruction.substring(1))) {
    return "0" + decimalToBinary(instruction.substring(1));
  }
  return "0" + decimalToBinary(symbolTable[instruction.substring(1)]);
};

let parseCInx = (instruction) => {
  let init = "111";
  let a = "0";
  let computationCode, destCode, jmpCode;
  jmpCode = "000";
  if (instruction.includes("=")) {
    [destCode, computationCode] = instruction.split("=");
    computationCode = computationCode.split(";")[0];
    destCode = destinationTable[destCode];
  }
  if (instruction.includes(";")) {
    jmpCode = jmpTable[instruction.split(";")[1]];
    if (!instruction.includes("=")) {
      computationCode = instruction.split(";")[0];
    }
    destCode = destinationTable[destCode || null];
  }
  if (computationCode.includes("M")) {
    a = "1";
  }
  computationCode = computationTable[String(computationCode)];
  //console.log({
  //  instruction,
  //  computationCode,
  //  destCode,
  //  jmpCode,
  //});
  return init + a + computationCode + destCode + jmpCode;
};

let registerSymbols = (instructions) => {
  let labels = [];
  instructions.forEach((inx, index) => {
    if (inx.startsWith("(") && inx.endsWith(")")) {
      let label = inx.substring(1, inx.length - 1);
      symbolTable[label] = index - labels.length;
      labels.push(label);
    }
  });
  // If the instruction starts with @, it means it's an A instruction.
  // The above block adds all the labels to symbolTable
  // The below block adds variables to the symbolTable
  let lastUsedRegisterAddress = 15;
  instructions.forEach((inx) => {
    if (
      inx.startsWith("@") &&
      !Number.isInteger(+inx.substring(1)) &&
      !Object.keys(symbolTable).includes(inx.substring(1))
    ) {
      symbolTable[inx.substring(1)] = lastUsedRegisterAddress + 1;
      lastUsedRegisterAddress += 1;
    }
  });
  //console.log({
  //  symbolTable,
  //});
};

let decimalToBinary = (number) => {
  return (+number).toString(2).padStart(15, "0");
};

parseAsm(instructions);
