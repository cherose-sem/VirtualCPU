package dk.cphbusiness.virtualcpu;

import java.io.PrintStream;
import java.util.Scanner;

public class Machine {

    private Cpu cpu = new Cpu();
    private Memory memory = new Memory();
    private boolean halt = false;
    Program program;

    public void load(Program program) {
        this.program = program;
        int index = 0;
        for (int instr : program) {
            memory.set(index++, instr);
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n-> Press [ENTER] to run next instruction or type EXIT to exit");
        String input = scanner.nextLine();
        
        while (!input.equalsIgnoreCase("exit") && !halt) {
            tick();
            print(System.out);
            System.out.println("\n-> Press [ENTER] to run next instruction");
            input = scanner.nextLine();
        }

    }

    public void tick() {
        int instr = memory.get(cpu.getIp());
        switch (instr) {
            case 0b0000_0000:
                // 0000 0000  NOP
                cpu.incIp();
                // cpu.setIp(cpu.getIp() + 1);
                break;
            case 0b0000_0001:
                // 0000 0001 ADD A B
                cpu.setA(cpu.getA() + cpu.getB());
                cpu.incIp();
                break;
            case 0b0000_0010:
                // MUL
                cpu.setA(cpu.getA() * cpu.getB());
                cpu.incIp();
                break;
            case 0b0000_0011:
                // DIV
                cpu.setA(cpu.getA() / cpu.getB());
                cpu.incIp();
                break;
            case 0b0000_0100:
                // ZERO
                cpu.setFlag(cpu.getA() == 0);
                cpu.incIp();
                break;
            case 0b0000_0101:
                // NEG
                cpu.setFlag(cpu.getA() < 0);
                cpu.incIp();
                break;
            case 0b0000_0110:
                // POS
                cpu.setFlag(cpu.getA() > 0);
                cpu.incIp();
                break;
            case 0b0000_0111:
                // NZERO
                cpu.setFlag(cpu.getA() != 0);
                cpu.incIp();
                break;
            case 0b0000_1000:
                // EQ
                cpu.setFlag(cpu.getA() == cpu.getB());
                cpu.incIp();
                break;
            case 0b0000_1001:
                // LT
                cpu.setFlag(cpu.getA() < cpu.getB());
                cpu.incIp();
                break;
            case 0b0000_1010:
                // GT
                cpu.setFlag(cpu.getA() > cpu.getB());
                cpu.incIp();
                break;
            case 0b0000_1011:
                // NEQ
                cpu.setFlag(cpu.getA() != cpu.getB());
                cpu.incIp();
                break;
            case 0b0000_1100:
                // ALWAYS
                cpu.setFlag(true);
                cpu.incIp();
                break;
            case 0b0000_1101:
                // UNDEFINED
                System.out.println("UNDEFINED");
                break;
            case 0b0000_1110:
                // UNDEFINED
                System.out.println("UNDEFINED");
                break;
            case 0b0000_1111:
                // HALT
                halt = true;
                break;
            case 0b0001_0100:
                // MOV A B
                cpu.setB(cpu.getA());
                cpu.incIp();
                break;
            case 0b0001_0101:
                // MOV B A
                cpu.setA(cpu.getB());
                cpu.incIp();
                break;
            case 0b0001_0110:
                // INC
                cpu.setA(cpu.getA() + 1);
                cpu.incIp();
                break;
            case 0b0001_0111:
                // DEC
                cpu.setA(cpu.getA() - 1);
                cpu.incIp();
                break;
            default:
                break;
        }
//PUSH r
        if ((instr & 0b1111_1110) == 0b0001_0000) {

            cpu.decSp();
            int r = (instr & 0b0000_1000) >> 3;
            if (r == cpu.A) {
                memory.set(cpu.getSp(), cpu.getA());
            } else {
                memory.set(cpu.getSp(), cpu.getB());
            }
            cpu.incIp();
        }
        //POP r
        if ((instr & 0b1111_1110) == 0b0001_0010) {
            int r = (instr & 0b0000_0001);

            if (r == cpu.A) {
                cpu.setA(memory.get(cpu.getSp()));
            } else {
                cpu.setB(memory.get(cpu.getSp()));
            }
            cpu.incSp();
            cpu.incIp();
        }
        //RTN +o
        if ((instr & 0b1111_1000) == 0b0001_1000) {
            int o = (instr & 0b0000_0111);
            cpu.setIp(memory.get(cpu.getSp()));
            cpu.incSp();
            cpu.setSp(cpu.getSp() + o);
            cpu.incIp();
        }

        //MOV r o
        if ((instr & 0b1111_0000) == 0b0010_0000) {

            // 0010 1 011 MOV B (=1) +3  [SP +3] // Move register B to memory position of SP with offset 3
            // 00101011 finding instruction
            //    and
            // 11110000
            // --------
            // 00100000
            // 00101011 finding offset
            //    and
            // 00000111
            // --------
            // 00000011 = 3
            // 00101011 finding register
            //    and
            // 00001000
            // --------
            // 00001000 = 8
            //    >> 3
            // 00000001 = 1
            int o = instr & 0b0000_0111;
            int r = (instr & 0b0000_1000) >> 3;
            if (r == cpu.A) {
                memory.set(cpu.getSp() + o, cpu.getA());
            } else {
                memory.set(cpu.getSp() + o, cpu.getB());
            }
            cpu.setIp(cpu.getIp() + 1);
        }
        // MOV o r
        if ((instr & 0b1111_0000) == 0b0011_0000) {
            int r = (instr & 0b0000_0001);
            int o = (instr & 0b0000_1110) >> 1;

            if (r == cpu.A) {
                cpu.setA(memory.get(cpu.getSp() + o));
            } else {
                cpu.setB(memory.get(cpu.getSp() + o));
            }
            cpu.incIp();
        }
        //MOV v r
        if ((instr & 0b1100_0000) == 0b0100_0000) {
            int r = (instr & 0b0000_0001);
            int v = (instr & 0b0011_1110) >> 1;

            if (r == cpu.A) {
                cpu.setA(v);
            } else {
                cpu.setB(v);
            }
            cpu.incIp();
        }
        // JMP #a
        if ((instr & 0b1100_0000) == 0b1000_0000) {
            if (cpu.isFlag()) {
                cpu.setIp(instr & 0b00111111);
            } else {
                cpu.incIp();
            }
        }
        //CALL #a
        if ((instr & 0b1100_0000) == 0b1100_0000) {
            if (cpu.isFlag()) {
                cpu.decSp();
                memory.set(cpu.getSp(), cpu.getIp());
                cpu.setIp(instr & 0b00111111);
            } else {
                cpu.incIp();
            } 
        //CALL FACT
            
        }
       
        
    }
    
    private int fact(int n){
        if(n==0) return 1;
        return n*fact(n-1);
    }
    
    private int tfact(int n, int f){
        if(n==0) return f;
        return tfact(n-1, f*n);
    }

    public void print(PrintStream out) {
//        out.println();
        out.println();
        out.println(program.getString(cpu.getIp()));
        memory.print(out, cpu.getIp(), cpu.getSp());
        out.println("----------------------------------------------");
        cpu.print(out);
        out.println("----------------------------------------------");

    }
}

//load()
//print()
//tick() --> getCodeInstr(binary)  --> call for doInstr()
//print() --> getBinaryFromCodeInstr()

