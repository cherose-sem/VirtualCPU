package dk.cphbusiness.virtualcpu;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to the awesome CPU program");
//        List<String> instr = readInstr();
        List<String> instr = getFacTailCmds();
        Program program = new Program(instr);
        Machine machine = new Machine();
        machine.load(program);
        machine.run();
    }

    public static List<String> readInstr() {
        List<String> lines = new ArrayList<>();
        File file = new File("instructions.txt");
        Scanner scan;
        try {
            scan = new Scanner(file);
            while (scan.hasNext()) {
                lines.add(scan.nextLine());
            }
        } catch (FileNotFoundException ex) {
            System.out.println("FILE NOT FOUND");
        }

        return lines;
    }
    
    public static List<String> getFacTailCmds(){
        
        List<String> lines = new ArrayList<>();
        File file = new File("factTail.txt");
        Scanner scan;
        try {
            scan = new Scanner(file);
            while (scan.hasNext()) {
                lines.add(scan.nextLine());
            }
        } catch (FileNotFoundException ex) {
            System.out.println("FILE NOT FOUND");
        }
        return lines;
        
    }

}
