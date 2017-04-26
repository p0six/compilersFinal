package com.rsh.cpsc323;

/*
 * Name			Michael Romero, Austin Suarez, Sean Hillenbrand
 * CPSC 323     Compilers and Languages
 * Project No.	10 - Final Project
 * Due Date		END OF SEMESTER!
 * Professor	Ray Ahmadnia
 *
 * Purpose:		A predictive parsing table driven compiler.
 */

import java.io.*;
import java.util.ArrayDeque;
import java.util.HashMap;

public class Main {

    private static String rhsIndex[] = {";",")","+","-","*","/",",","(","P","Q","R","S","0","1","2","3","4","5","6","7","8","9","PROGRAM","END.","INTEGER","PRINT",":","$","BEGIN"};
    private static String lhsIndex[] = {"A","B","C","D","G","H","I","J","K","L","M","N","E","O","T","U","F","V","W","X","Y","Z"};
    private static String[][] predictiveTable = { // Unused columns: ":", "BEGIN", "$", moved to the end of table... kept just in case
            //00    01      02      03      04      05      06    07      08        09        10        11        12      13      14      15      16      17      18      19      20      21       22                           23    24        25              26     27      28
            //;     )       +       -       *       /       ,     (       P         Q         R         S         0       1       2       3       4       5       6       7       8       9        PROGRAM                      END.  INTEGER   PRINT           :      $       BEGIN
/* 00 A */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"PROGRAM B ; D BEGIN J END.","5523","5524"   ,"5525"         ,"5526","5527","5528"}, // 00 | A
/* 01 B */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"Z C"    ,"Z C"    ,"Z C"    ,"Z C"    ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528"}, // 01 | B
/* 02 C */  {"^"   ,"^"   ,"^"    ,"^"    ,"^"    ,"^"    ,"^"   ,"^"    ,"Z C"    ,"Z C"    ,"Z C"    ,"Z C"    ,"Y C"  ,"Y C"  ,"Y C"  ,"Y C"  ,"Y C"  ,"Y C"  ,"Y C"  ,"Y C"  ,"Y C"  ,"Y C"  ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528"}, // 02 | C
/* 03 D */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","I : G ;","5525"         ,"5526","5527","5528"}, // 03 | D
/* 04 G */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"B H"    ,"B H"    ,"B H"    ,"B H"    ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528"}, // 04 | G
/* 05 H */  {"^"  ,"5501","5502"  ,"5503" ,"5504" ,"5505" ,", G" ,"5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528"}, // 05 | H
/* 06 I */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","INTEGER","5525"         ,"5526","5527","5528"}, // 06 | I
/* 07 J */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"L K"    ,"L K"    ,"L K"    ,"L K"    ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"L K"          ,"5526","5527","5528"}, // 07 | J
/* 08 K */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"J"      ,"J"      ,"J"      ,"J"      ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"^"   ,"5524"   ,"J"            ,"5526","5527","5528"}, // 08 | K
/* 09 L */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"N"      ,"N"      ,"N"      ,"N"      ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"M"            ,"5526","5527","5528"}, // 09 | L
/* 10 M */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"PRINT ( B ) ;","5526","5527","5528"}, // 10 | M
/* 11 N */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"B = E ;","B = E ;","B = E ;","B = E ;","5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528"}, // 11 | N
/* 12 E */  {"5500","5501","T O"  ,"T O"  ,"5504" ,"5505" ,"5506","T O"  ,"T O"    ,"T O"    ,"T O"    ,"T O"    ,"T O"  ,"T O"  ,"T O"  ,"T O"  ,"T O"  ,"T O"  ,"T O"  ,"T O"  ,"T O"  ,"T O"  ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528"}, // 12 | E
/* 13 O */  {"^"   ,"^"   ,"+ T O","- T O","5504" ,"5505" ,"5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528"}, // 13 | O
/* 14 T */  {"5500","5501","F U"  ,"F U"  ,"5504" ,"5505" ,"5506","F U"  ,"F U"    ,"F U"    ,"F U"    ,"F U"    ,"F U"  ,"F U"  ,"F U"  ,"F U"  ,"F U"  ,"F U"  ,"F U"  ,"F U"  ,"F U"  ,"F U"  ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528"}, // 14 | T
/* 15 U */  {"^"   ,"^"   ,"^"    ,"^"    ,"* F U","/ F U","5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528"}, // 15 | U
/* 16 F */  {"5500","5501","V"    ,"V"    ,"5504" ,"5505" ,"5506","( E )","B"      ,"B"      ,"B"      ,"B"      ,"V"    ,"V"    ,"V"    ,"V"    ,"V"    ,"V"    ,"V"    ,"V"    ,"V"    ,"V"    ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528"}, // 16 | F
/* 17 V */  {"5500","5501","W Y X","W Y X","5504" ,"5505" ,"5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"W Y X","W Y X","W Y X","W Y X","W Y X","W Y X","W Y X","W Y X","W Y X","W Y X","5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528"}, // 17 | V
/* 18 W */  {"5500","5501","+"    ,"-"    ,"5504" ,"5505" ,"5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"^"    ,"^"    ,"^"    ,"^"    ,"^"    ,"^"    ,"^"    ,"^"    ,"^"    ,"^"    ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528"}, // 18 | W
/* 19 X */  {"^"   ,"^"   ,"^"    ,"^"    ,"^"    ,"^"    ,"5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"Y X"  ,"Y X"  ,"Y X"  ,"Y X"  ,"Y X"  ,"Y X"  ,"Y X"  ,"Y X"  ,"Y X"  ,"Y X"  ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528"}, // 19 | X
/* 20 Y */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"0"    ,"1"    ,"2"    ,"3"    ,"4"    ,"5"    ,"6"    ,"7"    ,"8"    ,"9"    ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528"}, // 20 | Y
/* 21 Z */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"P"      ,"Q"      ,"R"      ,"S"      ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528"}  // 21 | Z
    };

    private static void partOne(BufferedReader br, BufferedWriter bw) throws IOException {
        String lineIn, specialChars = "():;,=*/+-$";
        Character c, d; int slashCount = 0, spaceCount = 0;

        System.out.println("===================================");
        System.out.println("| Part One Input:");
        System.out.println("===================================");
        StringBuilder sb = new StringBuilder();
        while (br.ready()) {
            lineIn = br.readLine();
            System.out.println(lineIn);

            for (int i = 0; i < lineIn.length(); i++) { // remove comments, trim whitespace
                c = lineIn.charAt(i);
                if (c == '/') { // let's handle comments..
                    slashCount++;
                    if (slashCount == 4) { // comment began.. comment ended..
                        slashCount = 0;
                    }
                } else {
                    if (!(slashCount >= 2 && slashCount < 4)) { // ignores comments
                        if (!Character.isWhitespace(c)) { // not a whitespace char.
                            if (spaceCount == 0 && specialChars.indexOf(c) != -1) { // ensures spaces in between special Chars
                                sb.append(' ');
                                sb.append(c);
                                sb.append(' ');
                                spaceCount++;
                            } else if (specialChars.indexOf(c) != -1) { // handling negative and positive assignments
                                sb.append(c);
                                if ((i + 1) < lineIn.length()) {
                                    d = lineIn.charAt(i+1);
                                    if ((c.equals('-') || c.equals('+')) && Character.isDigit(d)) {
                                        System.out.println("WE IN HERE");
                                        sb.append(d);
                                        i++;
                                    }
                                }
                                sb.append(' ');
                                spaceCount++;
                            } else { // just another character.. nothing special
                                spaceCount = 0;
                                sb.append(c);
                            }
                        } else { // whitespace
                            if (spaceCount == 0 && sb.length() != 0) {
                                sb.append(' ');
                                spaceCount++;
                            }
                        }
                    }
                }
            }
            if (sb.length() != 0) { // we have a string. write to file. all whitespace and nonsense filtered out above
                sb.append('\n'); // what line string is complete without a carriage return
                bw.write(sb.toString());
                sb.delete(0, sb.length());
            }
        }
    }

    private static String doWork(String tableValue, ArrayDeque<String> myStack) {
        String lhsHolder;
        String tableValueSplitter[];
        if (tableValue.equals("^")) { // handle lambda by ignoring and moving on..
            lhsHolder = myStack.pop();
            System.out.println("stack: " + myStack.toString()); // A.... "pop: E "
        } else if (tableValue.contains("55")) { // "null" condition in table..
            lhsHolder = tableValue;
        } else {
            tableValueSplitter = tableValue.split("\\s+");
            for (int j = tableValueSplitter.length - 1; j >= 0; j--) {
                myStack.push(tableValueSplitter[j]); System.out.println("stack: " + myStack.toString()); // "push Q, T"
            }
            lhsHolder = myStack.pop(); System.out.println("stack: " + myStack.toString()); // A.... "pop: E "
        }
        return lhsHolder;
    }

    private static boolean partTwo(BufferedReader br) throws IOException {
        // Initialization Stuff..
        br.mark(1);
        String lineIn, tableValue, lhsHolder, lineArray[];
        Character charHolder;
        ArrayDeque<String> myStack = new ArrayDeque<>();

        HashMap<String,Integer> rhsMap = new HashMap<>(); // This gives us an easy way to get some indexes....
        for (int i = 0; i < rhsIndex.length; i++) {
            rhsMap.put(rhsIndex[i], i);
        }
        HashMap<String,Integer> lhsMap = new HashMap<>();
        for (int i = 0; i < lhsIndex.length; i++) {
            lhsMap.put(lhsIndex[i], i);
        }
        // Initialized...

        System.out.println("===================================");
        System.out.println("| Beginning Part Two:");
        System.out.println("===================================");
        myStack.push("$"); System.out.println("stack: " + myStack.toString());
        myStack.push("A"); System.out.println("stack: " + myStack.toString());
        //while (br.ready() && !myStack.peek().equals("$")) {
        while (br.ready()) {
            lineIn = br.readLine();
            lineArray = lineIn.split("\\s+");
            for (String readValue : lineArray) {
                lhsHolder = myStack.pop(); System.out.println("stack: " + myStack.toString()); // "pop: E "
                System.out.println("WORD: \"" + readValue + "\"");
                while (!lhsHolder.equals(readValue)) {
                    if(rhsMap.get(readValue) == null || readValue.equals("P") || readValue.equals("Q") || readValue.equals("R") || readValue.equals("S")) {
                        for (int j = 0; j < readValue.length(); j++) { // for each char in the <identifier>
                            charHolder = readValue.charAt(j); // S // 2 // 0 // 1 // 7
                            System.out.println("CHAR: " + charHolder);
                            while (!lhsHolder.equals(charHolder.toString()))  {
                                if (lhsMap.get(lhsHolder) == null || rhsMap.get(charHolder.toString()) == null || lhsHolder.contains("50")) {
                                    if (lhsHolder.contains("55")) {
                                        System.out.println("Error with: " + rhsIndex[Integer.parseInt(lhsHolder) % 5500] + ", char: " + charHolder + ", stackHolding: \"" + myStack.peek() + "\"");
                                    } else {
                                        System.out.println("Error with: " + lhsHolder + " at readValue: " + readValue + ", char: " + charHolder + ", stackHolding: \"" + myStack.peek() + "\"");
                                    }
                                    return false;
                                } else {
                                    tableValue = predictiveTable[lhsMap.get(lhsHolder)][rhsMap.get(charHolder.toString())]; // "[E,(] = TQ"
                                    lhsHolder = doWork(tableValue, myStack);
                                }
                            }
                            lhsHolder = myStack.pop(); System.out.println("stack: " + myStack.toString()); // "pop: E "
                        }
                        break; // takes us out of parsing characters to parsing words
                    } else {
                        if (lhsMap.get(lhsHolder) == null || rhsMap.get(readValue) == null || lhsHolder.contains("55")) {
                            if (lhsHolder.contains("55")) {
                                System.out.println("wError with: " + rhsIndex[Integer.parseInt(lhsHolder) % 5500] + " @ readValue: " + readValue + ", stackHolding: \"" + myStack.peek() + "\"");
                            } else {
                                System.out.println("xError with: " + lhsHolder + " @ readValue: " + readValue + ", stackHolding: \"" + myStack.peek() + "\"");
                            }
                            return false;
                        } else {
                            tableValue = predictiveTable[lhsMap.get(lhsHolder)][rhsMap.get(readValue)]; // "[E,(] = TQ"
                            lhsHolder = doWork(tableValue, myStack);
                        }
                    } // conditional to check for identifier types
                } // end while lhsHolder != readValue
            } // end for length of line - split all words
        } // end while - no more lines to read
        br.reset();
        return true;
    }

    private static void partThree(BufferedReader br, BufferedWriter bw) throws IOException { // Austin? :P
        System.out.println("======================");
        System.out.println("| Beginning Phase 3");
        System.out.println("======================");
        String lineIn;
        while (br.ready()) {
            lineIn = br.readLine();
            System.out.println(lineIn); // back to the back with a reset buffer...
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(".//src//com//rsh//cpsc323//S2017.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter(".//src//com//rsh/cpsc323/finalv2.txt"));
        // Part One
        partOne(br, bw); bw.close(); br.close();
        // Display Results of Part One
        br = new BufferedReader(new FileReader(".//src//com//rsh//cpsc323//finalv2.txt"));
        br.mark(1);
        System.out.println("==================================");
        System.out.println("| Results from Part One");
        System.out.println("==================================");
        while (br.ready()) {
            System.out.println(br.readLine()); // back to the back with a reset buffer...
        }
        br.reset();
        //System.exit(0);

        // Part 2
        if (partTwo(br)) {
            System.out.println("==================================");
            System.out.println("| Accepted");
            System.out.println("==================================");
        } else {
            System.out.println("==================================");
            System.out.println("| Rejected");
            System.out.println("==================================");
            return;
        }

        // At this point.. we need to convert our validated input into Source Code
        bw = new BufferedWriter(new FileWriter(".//src//com//rsh/cpsc323/finalv3.txt"));
        partThree(br, bw); // Begin Part 3
        br.close(); bw.close();
    }
}