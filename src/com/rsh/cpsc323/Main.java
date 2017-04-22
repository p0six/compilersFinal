package com.rsh.cpsc323;


import java.io.*;
import java.util.ArrayDeque;
import java.util.Scanner;

public class Main {

    private static void partOne(BufferedReader br, BufferedWriter bw) throws IOException {
        String lineIn, specialChars = "():;,)=*/+-";
        Character c;int slashCount = 0, spaceCount = 0;

        System.out.println("===================================");
        System.out.println("| Part One Input:");
        System.out.println("===================================");
        StringBuilder sb = new StringBuilder();
        while (br.ready()) {
            lineIn = br.readLine();
            System.out.println(lineIn);

            for (int i = 0; i < lineIn.length(); i++) { // step 1: trim all whitespace...
                c = lineIn.charAt(i);
                if (c == '/') { // let's handle comments..
                    slashCount++;
                    if (slashCount == 4) { // comment began.. comment ended..
                        slashCount = 0;
                    }
                } else if (slashCount >= 2 && slashCount < 4) { // ignores comments.. kinda ugly this way tbh...
                    continue;
                } else { // we're not in a comment..
                    if (!Character.isWhitespace(c)) {
                        if (spaceCount == 0 && specialChars.indexOf(c) != -1) {
                            sb.append(" ");
                            sb.append(c);
                            sb.append(" ");
                            spaceCount++;
                        } else {
                            spaceCount = 0;
                            sb.append(c);
                        }
                    } else {
                        if (sb.length() != 0 && spaceCount == 0) {
                            spaceCount++;
                            sb.append(" ");
                        }
                    }
                }
            }
            if (sb.length() != 0) {
                //if (sb.toString().contains("PROGRAM")) {
                    //lineOut += '\n';
                    //sb.append('\n');
                //} // rules say all blank lines must be removed, though it does not match sample output...
                sb.append("\n");
                System.out.println(sb.toString());
                bw.write(sb.toString());
                sb.delete(0, sb.length());
            }
        }
    }

    private static boolean partTwo(BufferedReader br, BufferedWriter bw) throws IOException {
        String lineIn, lineOut;
        System.out.println("===================================");
        System.out.println("| Part Two Input:");
        System.out.println("===================================");
        while (br.ready()) {
            lineIn = br.readLine();
            System.out.println(lineIn);
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
        // Part One
        BufferedReader br = new BufferedReader(new FileReader(".//src//com//rsh//cpsc323//S2017.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter(".//src//com//rsh/cpsc323/finalv2.txt"));
        partOne(br, bw); bw.close(); br.close();


        // Part Two
        BufferedReader br2 = new BufferedReader(new FileReader(".//src//com//rsh//cpsc323//finalv2.txt"));
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(".//src//com//rsh/cpsc323/finalv3.txt"));
        if (partTwo(br2, bw2)) {
            System.out.println("==================================");
            System.out.println("| Accepted");
            System.out.println("==================================");
        } else {
            System.out.println("==================================");
            System.out.println("| Rejected");
            System.out.println("==================================");
            return;
        } br2.close(); bw2.close();

        // At this point... we need to translate the input into a final language...
        BufferedReader br3 = new BufferedReader(new FileReader(".//src//com//rsh//cpsc323//finalv3.txt"));
        System.out.println("made it here...");
    }
}
