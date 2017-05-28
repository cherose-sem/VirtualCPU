package dk.cphbusiness.virtualcpu;

import java.util.Iterator;
import java.util.List;

public class Program implements Iterable<Integer> {

    private String[] lines;

    public Program(String... lines) {
        this.lines = lines;
    }

    Program(List<String> instr) {
        this.lines = new String[instr.size()];
        for (int i = 0; i < instr.size(); i++) {
            this.lines[i] = instr.get(i);
        }
    }

    public int get(int index) {
        String line = lines[index];
        if (line.charAt(0) == '0' || line.charAt(0) == '1') {
            return Integer.parseInt(line, 2);
        } else {
            return getBinaryInstr(line);
        }
    }
    
    public String getString(int index){
      return lines[index];
  }

    @Override
    public Iterator<Integer> iterator() {
        return new ProgramIterator();
    }

    private int getBinaryInstr(String line) {
        String[] parts = line.split(" ");
        int byteInstr = -1;
        switch (parts[0]) {
            case "NOP":
                byteInstr = 0b0000_0000;
                break;
            case "ADD":
                byteInstr = 0b0000_0001;
                break;
            case "MUL":
                byteInstr = 0b0000_0010;
                break;
            case "DIV":
                byteInstr = 0b0000_0011;
                break;
            case "ZERO":
                byteInstr = 0b0000_0100;
                break;
            case "NEG":
                byteInstr = 0b0000_0101;
                break;
            case "POS":
                byteInstr = 0b0000_0110;
                break;
            case "NZERO":
                byteInstr = 0b0000_0111;
                break;
            case "EQ":
                byteInstr = 0b0000_1000;
                break;
            case "LT":
                byteInstr = 0b0000_1001;
                break;
            case "GT":
                byteInstr = 0b0000_1010;
                break;
            case "NEQ":
                byteInstr = 0b0000_1011;
                break;
            case "ALWAYS":
                byteInstr = 0b0000_1100;
                break;
            case "HALT":
                byteInstr = 0b0000_1111;
                break;
            case "PUSH": // = 0001 000r
                if (parts[1].equals("A")) {
                    byteInstr = 0b0001_0000;
                }
                if (parts[1].equals("B")) {
                    byteInstr = 0b0001_0001;
                }
                break;
            case "POP": // = 0001 001r
                if (parts[1].equals("A")) {
                    byteInstr = 0b0001_0010;
                }
                if (parts[1].equals("B")) {
                    byteInstr = 0b0001_0011;
                }
                break;
            case "MOV": // MOV A B - 0001 0100 , MOV B A - 0001 0101
//                    //still getting confused in here :/

                if (parts[1].equals("A") || parts[1].equals("B")) {
                    int r = parts[1].equals("B") ? 1 : 0;
                    int o = Integer.parseInt(parts[2]);
                    return 0b0010_0000 | (r << 3) | o;
                } else if (parts[2].equals("A") || parts[2].equals("B")) {
                    int r = parts[2].equals("B") ? 1 : 0;
                    if (parts[1].startsWith("+")) {
                        return 0b0011_0000 | Integer.parseInt(parts[1]) << 1 | r;
                    } else {
                        return 0b01000000 | convertValue2Complement(parts[1]) << 1 | r;
                    }

                }
                break;
            case "INC":
                byteInstr = 0b0001_0110;
                break;
            case "DEC":
                byteInstr = 0b0001_0111;
                break;
            case "RTN":
                if (parts.length > 1) {
                    byteInstr = 0b0001_1000 | Integer.parseInt(parts[1]);
                } else {
                    byteInstr = 0b0001_1000 | Integer.parseInt("+0");
                }
                break;
            case "JMP":
                byteInstr = 0b1000_0000 | Integer.parseInt(parts[1]);
                break;
            case "CALL":
                byteInstr = 0b1100_0000 | Integer.parseInt(parts[1]);
                break;
        }
        return byteInstr;
    }

    private int convertValue2Complement(String number){
      int num = Integer.parseInt(number);
      if (num<0) return 32-Math.abs(num);
      return num;
  }
    
    private class ProgramIterator implements Iterator<Integer> {

        private int current = 0;

        @Override
        public boolean hasNext() {
            return current < lines.length;
        }

        @Override
        public Integer next() {
            return get(current++);
        }

    }

}
