// Hack VM translator
// written by Lalit (@lalit2005)
// usage: bun index.js tests/SimpleAdd/SimpleAdd.vm

import fs from "node:fs";
import { argv } from "node:process";

if (!argv[1]) {
  throw new Error("No asm file given");
}

const filePath = argv[2];
const fileContents = fs.readFileSync(filePath);
const vmInstructions = fileContents
  .toString()
  .split("\n")
  .map((a) => {
    if (!a.startsWith("//") && a.includes("//")) {
      return a.substring(0, a.indexOf("//") - 1).trim();
    }
    return a.trim();
  })
  .filter((a) => {
    return !a.startsWith("//") && a; // '& a' ensures that "" are not considered as valid instructions
  });

const memorySegments = [
  "local",
  "argument",
  "this",
  "that",
  "constant",
  "static",
  "temp",
  "pointer",
];

function translate(vmInstructions) {
  let finalAsmCode = [];
  let instruction;
  vmInstructions.forEach((inx) => {
    if (inx.startsWith("push")) {
      instruction = parsePush(inx);
      console.log(instruction);
      finalAsmCode.push(parsePush(inx));
    } else if (inx.startsWith("pop")) {
      instruction = parsePop(inx);
      console.log(instruction);
      finalAsmCode.push(instruction);
    } else if (
      ["add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"].includes(inx)
    ) {
      instruction = parseArithmetic(inx);
      console.log(instruction);
      finalAsmCode.push(instruction);
    } else if (inx.startsWith("label")) {
      instruction = parseLabel(inx);
      console.log(instruction);
      finalAsmCode.push(instruction);
      parseLabel(inx);
    } else if (inx.startsWith("goto")) {
      instruction = parseGoto(inx);
      console.log(instruction);
      finalAsmCode.push(instruction);
    } else if (inx.startsWith("if-goto")) {
      instruction = parseIfGoto(inx);
      console.log(instruction);
      finalAsmCode.push(instruction);
    } else if (inx.startsWith("function")) {
    } else if (inx.startsWith("call")) {
    }
  });

  fs.writeFileSync(
    "result.asm",
    `// Assembly code for ${filePath}\n\n` + finalAsmCode.join("\n"),
  );
}

function parseIfGoto(inx) {
  if (inx.split(" ").length != 2) {
    throw new Error("Invalid if-goto statement" + " " + inx);
  }
  let [_, labelName] = inx.split(" ");
  let asmCode = `
// ${inx}
@SP
M=M-1
A=M
D=M
@${labelName}
D;JGT
`;
  return asmCode;
}

function parseGoto(inx) {
  if (inx.split(" ").length != 2) {
    throw new Error("Invalid goto statement" + " " + inx);
  }
  let [_, labelName] = inx.split(" ");
  let asmCode = `
// ${inx}
@${labelName}
0;JMP
`;
  return asmCode;
}

function parseLabel(inx) {
  if (inx.split(" ").length != 2) {
    throw new Error("Invalid label command" + " " + inx);
  }

  let [_, labelName] = inx.split(" ");

  let asmCode = `
// ${inx}
(${labelName})
`;
  return asmCode.trim();
}

function parsePop(inx) {
  let segments = inx.split(" ");
  let [_, segment, value] = segments;
  if (segments.length != 3) {
    console.log(segments);
    throw new Error("Invalid pop statement" + " " + inx);
  }
  if (!memorySegments.includes(segment)) {
    throw new Error("Invalid memory segment" + " " + inx);
  }
  let asmCode;
  switch (segment) {
    // example: pop temp 6
    case "temp":
      asmCode = `
@SP
A=M-1
D=M
@${5 + +value}
M=D
@SP
M=M-1
`;
      break;
    // example: pop pointer 0/1
    case "pointer":
      asmCode = `
@SP
A=M-1
D=M
@${+value == 0 ? "THIS" : "THAT"}
M=D
@SP
M=M-1
`;
      break;

    case "local":
    case "argument":
    case "this":
    case "that":
      let labels = {
        argument: "ARG",
        local: "LCL",
        this: "THIS",
        that: "THAT",
      };
      asmCode = `
@SP
A=M-1
D=M
@R13
M=D
@${labels[segment]}
D=M
@${value}
D=D+A
@R14
M=D
@R13
D=M
@R14
A=M
M=D
@SP
M=M-1
`;
      break;
    // example: pop static 5
    // assuming that the file name is in the form hello.vm
    case "static":
      let file = filePath.split("/")[filePath.split("/").length - 1];
      asmCode = `
@SP
A=M-1
D=M
@${file[0].toUpperCase() + file.slice(1)}.${value}
M=D
@SP
M=M-1
`;
      break;
    default:
      asmCode = "---------INVALID POP-----------";
      break;
  }
  return `// ${inx}
${asmCode.trim()}
`.trim();
}

// example: push argument 10
function parsePush(inx) {
  let segments = inx.split(" ");
  let [_, segment, value] = segments;
  if (segments.length != 3) {
    throw new Error("Invalid push statement" + " " + inx);
  }
  if (!memorySegments.includes(segment)) {
    throw new Error("Invalid memory segment" + " " + inx);
  }
  let asmCode;
  switch (segment) {
    // example: push constant 5
    case "constant":
      asmCode = `
@${value}
D=A
@SP
A=M
M=D
@SP
M=M+1
`;
      break;
    // example: push temp 6
    case "temp":
      asmCode = `
@${5 + +value}
D=M
@SP
A=M
M=D
@SP
M=M+1
`;
      break;
    // example: push pointer 0/1
    case "pointer":
      asmCode = `
@${+value == 0 ? "THIS" : "THAT"}
D=M
@SP
A=M
M=D
@SP
M=M+1
`;
      break;

    case "local":
    case "argument":
    case "this":
    case "that":
      let labels = {
        argument: "ARG",
        local: "LCL",
        this: "THIS",
        that: "THAT",
      };
      asmCode = `
@${labels[segment]}
D=M
@${value}
D=D+A
A=D
D=M
@SP
A=M
M=D
@SP
M=M+1
`;
      break;
    // example: push static 5
    // assuming that the file name is in the form hello.vm
    case "static":
      let file = filePath.split("/")[filePath.split("/").length - 1];
      asmCode = `
@${file[0].toUpperCase() + file.slice(1)}.${value}
D=M
@SP
A=M
M=D
@SP
M=M+1
`;
      break;
    default:
      asmCode = "---------INVALID PUSH -----------";
      break;
  }
  console.log(asmCode);
  return `// ${inx}
${asmCode.trim()}
`.trim();
}

function parseArithmetic(inx) {
  let asmCode;
  let id = randomInt();
  switch (inx) {
    case "add":
      asmCode = `
@SP
A=M-1
D=M
A=A-1
D=D+M
M=D
@SP
M=M-1
`;
      break;
    case "sub":
      asmCode = `
@SP
A=M-1
D=M
A=A-1
D=M-D
M=D
@SP
M=M-1
`;
      break;
    case "neg":
      asmCode = `
@SP
A=M-1
M=-M
`;
      break;
    case "eq":
      asmCode = `
@SP
A=M-1
D=M
A=A-1
D=M-D

@EQ_TRUE_${id}
D;JEQ

@SP
A=M-1
A=A-1
M=0
@EQ_END_${id}
0;JMP

(EQ_TRUE_${id})
@SP
A=M-1
A=A-1
M=-1

(EQ_END_${id})
@SP
M=M-1
`;
      break;
    case "gt":
      asmCode = `
@SP
A=M-1
D=M
A=A-1
D=M-D

@GT_TRUE_${id}
D;JGT

@SP
A=M-1
A=A-1
M=0
@GT_END_${id}
0;JMP

(GT_TRUE_${id})
@SP
A=M-1
A=A-1
M=-1

(GT_END_${id})
@SP
M=M-1
`;
      break;
    case "lt":
      asmCode = `
@SP
A=M-1
D=M
A=A-1
D=M-D

@LT_TRUE_${id}
D;JLT

@SP
A=M-1
A=A-1
M=0
@LT_END_${id}
0;JMP

(LT_TRUE_${id})
@SP
A=M-1
A=A-1
M=-1

(LT_END_${id})
@SP
M=M-1
`;
      break;
    case "and":
      asmCode = `
@SP
A=M-1
D=M
A=A-1
D=D&M
M=D
@SP
M=M-1
`;
      break;
    case "or":
      asmCode = `
@SP
A=M-1
D=M
A=A-1
D=D|M
M=D
@SP
M=M-1
`;
      break;
    case "not":
      asmCode = `
@SP
A=M-1
M=!M
`;
      break;
    default:
      break;
  }

  return `// ${inx}
${asmCode.trim()}
`.trim();
}

function randomInt() {
  return Math.floor(Math.random() * 10e5);
}

translate(vmInstructions);
console.log(vmInstructions);
