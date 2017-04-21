package com.rsh.cpsc323;


import java.io.*;
import java.util.ArrayDeque;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        String lineIn = new String(), lineOut = new String(), specialChars = "():;,)=*/+-";
        Character c;
        ArrayDeque<Character> ad = new ArrayDeque<Character>();

        BufferedReader br = new BufferedReader(new FileReader(".//src//com//rsh//cpsc323//S2017.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter(".//src//com//rsh/cpsc323/finalv2.txt"));
        int slashCount = 0, spaceCount = 0;
        while (br.ready()) {
            lineIn = br.readLine(); lineOut = "";

            for (int i = 0; i < lineIn.length(); i++) { // step 1: trim all whitespace...
               c = lineIn.charAt(i);
               if (c == '/') { // let's handle comments..
                  slashCount++;
                  if (slashCount == 4) { // comment began.. comment ended..
                      slashCount = 0;
                  }
               } else if (slashCount >= 2 && slashCount < 4) { // ignores comments.. kinda ugly this way tbh...
               } else { // we're not in a comment..
                   if (!Character.isWhitespace(c)) {
                       if (spaceCount == 0 && specialChars.indexOf(c) != -1) {
                          lineOut += ' ';
                          lineOut += c;
                          lineOut += ' ';
                          spaceCount++;
                       } else {
                           spaceCount = 0;
                           lineOut += c;
                       }
                   } else {
                       if (!lineOut.isEmpty() && spaceCount == 0) {
                           spaceCount++;
                           lineOut += ' ';
                       }
                   }
               }
            }
            if (!lineOut.isEmpty()) {
                if (lineOut.contains("PROGRAM")) { // sample output has an extra line after the line with PROGRAM
                    lineOut += '\n';
                }
                bw.write(lineOut + '\n');
            }
        }
        bw.close(); br.close();
    }
}
