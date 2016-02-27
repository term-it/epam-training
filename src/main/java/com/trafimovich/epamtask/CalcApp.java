package com.trafimovich.epamtask;

import java.nio.file.*;
import java.util.List;

/**
 *
 * @author Term-iT
 */
public class CalcApp {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        List<String> tokens;
        Calculator epamCalculator;
        if (args.length != 0) {
            try {
                tokens = Files.readAllLines(Paths.get(args[0]));
            }
            catch (Exception exc) {
                System.out.println(exc);
                return ;
            }
            switch (tokens.remove(0)) {
                case "1":
                    epamCalculator = new SimpleCalc();
                    break ;
                case "2":
                    epamCalculator = new SimpleMemCalc();
                    break ;
                case "3":
                    epamCalculator = new EngineerCalc();
                    break ;
                case "4":
                    epamCalculator = new EngineerMemCalc();
                    break ;
                default:
                    System.out.println("Wrong type!");
                    return ;
            }
            System.out.println("Result = " +
                            epamCalculator.calculate(tokens.listIterator()));
        } else
            System.out.println("Usage: java epamtask <file_name>");
    }
}
