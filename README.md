# SAP-1 Simulator
A simulator For Albert Paul Malvino's SAP-1 Computer made in Java Swing.

After building this computer on a breadboard following Ben Eater's tutorials ([my build running](https://github.com/milen-patel/BreadboardCPU)), I decided to build this simulator. Visually appealing? No. Functional? Yes. 
![](demo.gif)

To test it out yourself, clone the project and compile it. A Java Runtime Environment is required to run this simulator (at least JRE 8). Additionally, a executable JAR is available in the distribution folder. To run it from the command line:  
```
java -jar SAP-1.jar
```

## Features
- **Clock Toggle**: Almost all of the components in SAP-1 are linked together via a system clock. This simulator has a button that can toggle the clock from low to high, or vice versa (as a result, each full clock cycle requires two presses of this button for a rising edge and falling edge).  
- **Event Log**: I built this tool so that potential users could load programs and trace their execution down to the edges of the clock. The event log provides brief descriptions of everything that happens in the computer to aid in learning.  
- **Autoplay**: Having the clock toggle button can be useful for tracing through individual instructions; however, it becomes repetitive to click the button to run an entire program. Thus, an autoplay toggle button has been provided that auto-toggles the clock. Click the button once to start auto-playing programs and click it again to stop and return to manual clock mode. Below this button is a slider where you can control the speed at which the program is executed.
- **Reset**: Clears the A register, B register, Out register, Program Counter, Instruction Register,	Memory Address Register, Flags Register, and sets the step-cycle counter to 0.  
- **Clear All Memory**: By default, the memory is loaded with random values (as is the case with Ben Eater's physical build). The user has the option to reset all memory values (to 0) in one click using this button.  
- **RAM Bit Changes**: The memory content visualizer on the left of the simulator shows each bit of the computer's memory. Users can click on each bit to toggle between a 1 or 0 in the selected position.  
- **Show Operation Codes**: Displays the operation codes in the Event Log, useful when writing programs.  
- **Load Counting Program**: Loads a counting program in memory that infinitely increments the A register.  
- **Analyze Program**: Translates the current memory content into a more readable form in the SAP Instruction Set, displays the readable version in the Event Log.  
- **7-Segment Display**: There is a display composed of four 7-segment displays that shows the current value stored in the output register. There is a button to toggle the display treating the input as unsigned or using two's complement.
- **Memory Address Highlighting**: The memory address register is always pointing to some address in memory. The memory visualization, by default, will highlight the address held in the MAR on the widget. There is a button to disable this feature, but I included it because it is helpful to always see what part of RAM is being written to or read from.
- **Assembler**: More on this in the next section.

## Assembler  
Writing programs using the manual bit changes on the memory view is slow enough, so I decided to create an assembler that converts readable assemble language to machine code which can be sent into the SAP memory.  

To use the assembler in the most basic form, write out instructions with binary arguments with one instruction per line. Example programs are provided in the following section. However, the assembler has both variables and labels to help make programs more readable.  

**Variables**: The assembler allows variables to be created and then referenced in LDA, ADD, SUB, and STA instructions. Variables can be declared anywhere in the program (including after being referenced). To declare a variable x with the value of 32, use `LET x = 0001 0000`. A variable name follows the   `let` keyword; variable names can only contain letters. The value given to a variable, following the equals sign, must be provided as an 8-bit value. Variables will be assigned a location in memory based on the order they are declared in the input program; variables will be allocated space at the last available memory address; the variable x declared above would be allocated at memory address 15 if it was the first variable to be declared in a program. Variables are not required to be used; for example, the command `LDA 0010` is valid and doesn't rely on any variables.

**Labels**: Labels can be declared on a new line using a '.' followed by the name of the label, such as `.main`. Labels can then be referenced as arguments to branch commands (JMP, JC, JZ). Example usage of this functionality can be seen in the examples below. 

**Comments**: As seen in the demo programs below, the assembler allows comments (similar to higher-level languages). Comments can be declared on their own line or on the same line as a command; the assembler ignores all comments before parsing the program. To start a comment, use a '#'; all proceeding text will be discarded until the next line is read.

Blow is the grammar that the assembler follows (not strictly on this due to syntax exceptions):

```
Program             -> Line Program?
Line                -> (Comment | Instruction | Label | Declaration) Comment? '\n'
Comment             -> '#' .*?
Instruction         -> Whitespace (InstructionType Argument | BranchInstruction | ArgLessInstruction)
Label               -> Whitespace '.' String
Declaration         -> "LET" WhiteSpace String "=" WhiteSpace BinaryString{2} 
InstructionType     -> ("LDA" | "ADD" | "SUB" | "STA" | "LDI") WhiteSpace
ArgLessInstruction  -> ("HLT" | "OUT" | "NOP") WhiteSpace
BranchInstruction   -> ("JMP" | "JC" | "JZ") WhiteSpace BranchArg
Argument            -> BinaryString | String // String should be a valid variabble
BranchArg           -> BinaryString | String // String should be a valid label
BinaryString        -> (('0' | '1') WhiteSpace?){4}
String              -> ([a-z][A-Z])?
WhiteSpace          -> (\t | ' ')*
```

## Example Programs
A trivial program is given below. A NOP (no operation) followed by a halt; the computer will essentially do nothing in the 8 clock cycles (5 for NOP + 3 for HLT) it takes to execute. This is useful to trace through and see the handling of instructions.  
```
[0] NOP
[1] HLT
```

Here is the counting program included in the simulator. Notice that the program will run indefinitely since there is no HLT command provided. This will load the constant 1 into the A register; then it will continually increment the contents of the A register by 1, display it in the output register, and store the same value in memory.    

```
# Assembler Input:
LET x = 0000 0001   # Define Variable
.loop               # Use of labels
    LDI 0001
    ADD x
    STA 1010
    OUT
    JMP loop

```
```
Instruction Set Version:
[0] LDI 1
[1] ADD 14
[2] OUT
[3] STA 10
[4] JMP 1
[14] 0b00000001
```

This is another basic program that demonstrates the LDI, ADD, SUB, OUT, and HLT instructions. This will load the constant 2 into the A register, add 1 to it (result = 3). Then it will subtract 2 (result = 1), store that value in the output register, and then halt.  
```
#Assembler Input:
LET X = 0000 0001  
LET Y = 0000 0010

.main               # Labels can be defined anywhere, this one isn't nececsary
    LDI 0010
    ADD X
    SUB Y

.end                # Another label, again not necesary but helps readability
    OUT
    HLT

```
```
 Instruction Set Version:
[0] LDI 2
[1] ADD 15
[2] SUB 14
[3] OUT
[4] HLT
[14] 2
[15] 1
```

A program is included to show conditional branching. We load the value stored in memory at address 15 into A, and then subtract that same value from A. The value stored at address 15 in memory is useless since any number minus itself equals 0. After we subtract in step 1, we expect the zero flag to be set. Instruction 2 says to branch to instruction 10 if the zero flag is set, and since 10 is a halt, the program will finish execution. We know this to be the case, so we would expect this to be the path that the computer takes. However, if the JZ is not executed, the program is set to display a 1 in the output register before halting, for demonstration purposes. If you changed the argument of the SUB command on the second line to yield a non-zero result, this is the path that the computer would follow.  
```
# Assembler Input:
LET x  = 0001 0000

.setup
    LDA x
    SUB x
    JZ caseZero
    JMP caseElse

.caseZero
    HLT

.caseElse
    LDI 0001
    OUT
    JMP caseZero
```

```
Instruction Set Version:
[0] LDA 15
[1] SUB 15
[2] JZ 10
[3] LDI 1
[4] OUT 
[5] JMP 10
[10] HLT
[15] 32
```

The computer also has the ability to conditionally branch if the last ALU operation overflowed. Below is a basic example that causes the ALU to overflow, followed by a JC command. If the JC executes successfully (and assuming the carry flag is set), the program halts. However, if the carry flag is not set, then the program will enter an easily detectable infinite loop.
```
# Assembler Input:
LET varMAX = 1111 1111
LET varONE = 0000 0001

.setup
    LDA varMax
    ADD varOne
    JC caseOverflow
    JMP setup

.caseOverflow
    HLT
```
```
Instruction Set Version:
[0] LDA 15
[1] ADD 14
[2] JC 4
[3] JMP 0
[4] HLT
[14] 0b00000001
[15] 0b11111111
```


### Operation Codes
The sample programs included above are brief and are intended to show off some of the instructions; however, there are more instructions not covered in the examples that allow more complex programs to be written. A full table of operation codes and their binary values are listed below. Note that each operation code is 4 bits, but we don't have 16 instructions; so, it is possible (and not too difficult) to add more instructions to our architecture if we wanted.   
| OPCode      |  Binary Value |
| ----------- | -----------   |
| NOP      	  | 0000          |
| LDA      	  | 0001          |
| ADD      	  | 0010          |
| SUB      	  | 0011          |
| STA      	  | 0100          |
| LDI      	  | 0101          |
| JMP      	  | 0110          |
| JC      	  | 0111          |
| JZ      	  | 1000          |
| OUT      	  | 1110          |
| HLT      	  | 1111          |



## Known Bugs
I have tested this program extensively, and have only come across the error listed below; however, I'm sure there are others out there to be discovered.  
- Reset Memory and Load Counting Program buttons sometimes take two clicks to fully execute. After extensive debugging, I am not sure why this happens but I believe it has something to do with Java Swing since the internal memory values are being updated correctly, just occassionally displayed infrequently.
