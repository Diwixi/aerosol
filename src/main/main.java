package main;

import java.util.Scanner;

public class main {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter format (h4 or h5): ");
        String format = scanner.nextLine();
        if (format.equals("h4")) {
            H4Reader.read(scanner);
        } else if (format.equals("h5")) {
            H5Reader.read(scanner);
        }
    }
}
