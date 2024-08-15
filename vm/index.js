// Hack VM translator
// written by Lalit (@lalit2005)

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
  .map((a) => a.trim())
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
  vmInstructions.forEach((inx) => {
    if (inx.startsWith("push")) {
      console.log(parsePush(inx));
    } else if (inx.startsWith("pop")) {
      console.log(parsePop(inx));
    } else if (
      ["add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"].includes(inx)
    ) {
      console.log(parseArithmetic());
    }
  });
}

function parsePush(inx) {
  let segments = inx.split(" ");
  let [_, segment, value] = segments;
  if (segments.length != 3) {
    throw new Error("Invalid pop statement");
  }
  if (!memorySegments.includes(segment)) {
    throw new Error("Invalid memory segment");
  }
  let asmCode;
  switch (segment) {
    // example: push temp 6
    case "temp":
      asmCode = `
@LCL
D=M
@5
D=D+A
@R13
M=D
@SP
M=M-1
D=M
@R13
A=M
M=D
`;
      break;
    // example: push pointer 0/1
    case "pointer":
      asmCode = `
@SP
A=M
D=M
@${value == 0 ? 3 : 4}
M=D
@SP
M=M-1
`;
      break;

    case ("local", "argument", "this", "that"):
      let labels = {
        argument: "ARG",
        local: "LCL",
        this: "THIS",
        that: "THAT",
      };
      asmCode = `
@SP
A=M
D=M
@${labels[value]}
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
    // example: push static 5
    // assuming that the file name is in the form hello.vm
    case "static":
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
    throw new Error("Invalid push statement");
  }
  if (!memorySegments.includes(segment)) {
    throw new Error("Invalid memory segment");
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
@${value == 0 ? 3 : 4}
D=M
@SP
A=M
M=D
@SP
M=M+1
`;
      break;

    case ("local", "argument", "this", "that"):
      let labels = {
        argument: "ARG",
        local: "LCL",
        this: "THIS",
        that: "THAT",
      };
      asmCode = `
@${labels[value]}
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
    // example: push static 5
    // assuming that the file name is in the form hello.vm
    case "static":
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
      break;
  }
  return `// ${inx}
${asmCode.trim()}
`.trim();
}

function parseArithmetic(inx) {
  let asmCode;
  switch (inx) {
    case "add":
      asmCode = `
@SP
A=M-1
D=M
A=A-1
D=D+M
M=D
D=A
@SP
M=D
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
D=A
@SP
M=D
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

@EQ_TRUE
D;JEQ

@SP
A=M-1
M=0
@EQ_END

(EQ_TRUE)
@SP
A=M-1
M=-1

(EQ_END)
`;
      break;
    case "gt":
      asmCode = `
@SP
A=M-1
D=M
A=A-1
D=M-D

@GT_TRUE
D;JGT

@SP
A=M-1
M=0
@GT_END

(GT_TRUE)
@SP
A=M-1
M=-1

(GT_END)
`;
      break;
    case "lt":
      asmCode = `
@SP
A=M-1
D=M
A=A-1
D=M-D

@LT_TRUE
D;JLT

@SP
A=M-1
M=0
@LT_END

(LT_TRUE)
@SP
A=M-1
M=-1

(LT_END)
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
D=A
@SP
M=D+1
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
D=A
@SP
M=D+1
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
}

translate(vmInstructions);
