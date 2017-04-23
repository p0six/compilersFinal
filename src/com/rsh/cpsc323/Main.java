package com.rsh.cpsc323;

/*
 * Name			Michael Romero, Austin Suarez, Sean Hillenbrand
 * Project No.	10 - Final Project
 * Due Date		END OF SEMESTER!
 * Professor	Ray Ahmadnia
 *
 * Purpose:		A bastardized compiler. Using either LR method or whatever we call the first method..
 */

import java.io.*;
import java.util.ArrayDeque;
import java.util.HashMap;

public class Main {

    private static String rhsIndex[] = {";",")","+","-","*","/",",","(",":","P","Q","R","S","0","1","2","3","4","5","6","7","8","9","PROGRAM","BEGIN","END.","INTEGER","PRINT","$"};
    private static String lhsIndex[] = {"A","B","C","D","G","H","I","J","K","L","M","N","E","O","T","U","F","V","W","X","Y","Z"};
    private static String[][] predictiveTable = { // Painfully Long Predictive Parsing Table
            //00 01  02    03    04    05    06   07    08 09       10       11       12       13    14    15    16    17    18    19    20    21    22     23                         24    25    26        27           28
            //;  )   +     -     *     /     ,    (     :  P        Q        R        S        0     1     2     3     4     5     6     7     8     9      PROGRAM                    BEGIN END.  INTEGER   PRINT        $
/* 00 A */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"",""      ,""      ,""      ,""      ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,"PROGRAM B ; D BEGIN J END.",""   ,""   ,""        ,""             ,""}, // 00 | A
/* 01 B */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"","Z C"    ,"Z C"    ,"Z C"    ,"Z C"    ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                          ,""   ,""   ,""        ,""             ,""}, // 01 | B
/* 02 C */  {"^","^","^"  ,"^"  ,"^"  ,"^"  ,"^" ,"^"  ,"","Z C"    ,"Z C"    ,"Z C"    ,"Z C"    ,"Y C" ,"Y C" ,"Y C" ,"Y C" ,"Y C" ,"Y C" ,"Y C" ,"Y C" ,"Y C" ,"Y C" ,""                          ,""   ,""   ,""        ,""             ,""}, // 02 | C
/* 03 D */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"",""      ,""      ,""      ,""      ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                          ,""   ,""   ,"I : G ;" ,""             ,""}, // 03 | D
/* 04 G */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"","B H"    ,"B H"    ,"B H"    ,"B H"    ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                          ,""   ,""   ,""        ,""             ,""}, // 04 | G
/* 05 H */  {"^","" ,""   ,""   ,""   ,""   ,", G",""   ,"",""      ,""      ,""      ,""      ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                          ,""   ,""   ,""        ,""             ,""}, // 05 | H
/* 06 I */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"",""      ,""      ,""      ,""      ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                          ,""   ,""   ,"INTEGER" ,""             ,""}, // 06 | I
/* 07 J */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"","L K"    ,"L K"    ,"L K"    ,"L K"    ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                          ,""   ,""   ,""        ,"L K"           ,""}, // 07 | J
/* 08 K */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"","J"     ,"J"     ,"J"     ,"J"     ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                          ,""   ,"^"  ,""        ,"J"            ,""}, // 08 | K
/* 09 L */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"","N"     ,"N"     ,"N"     ,"N"     ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                          ,""   ,""   ,""        ,"M"            ,""}, // 09 | L
/* 10 M */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"",""      ,""      ,""      ,""      ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                          ,""   ,""   ,""        ,"PRINT ( B ) ;",""}, // 10 | M
/* 11 N */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"","B = E ;","B = E ;","B = E ;","B = E ;",""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                          ,""   ,""   ,""        ,""             ,""}, // 11 | N
/* 12 E */  {"" ,"" ,"T O" ,"T O" ,""   ,""   ,""  ,"T O" ,"","T O"    ,"T O"    ,"T O"    ,"T O"    ,"T O" ,"T O" ,"T O" ,"T O" ,"T O" ,"T O" ,"T O" ,"T O" ,"T O" ,"T O" ,""                          ,""   ,""   ,""        ,""             ,""}, // 12 | E
/* 13 O */  {"^","^","+ T O","- T O",""   ,""   ,""  ,""   ,"",""      ,""      ,""      ,""      ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                          ,""   ,""   ,""        ,""             ,""}, // 13 | O
/* 14 T */  {"" ,"" ,"F U" ,"F U" ,""   ,""   ,""  ,"F U" ,"","F U"    ,"F U"    ,"F U"    ,"F U"    ,"F U" ,"F U" ,"F U" ,"F U" ,"F U" ,"F U" ,"F U" ,"F U" ,"F U" ,"F U" ,""                          ,""   ,""   ,""        ,""             ,""}, // 14 | T
/* 15 U */  {"^","^","^"  ,"^"  ,"* F U","/ F U",""  ,""   ,"",""      ,""      ,""      ,""      ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                          ,""   ,""   ,""        ,""             ,""}, // 15 | U
/* 16 F */  {"" ,"" ,"V"  ,"V"  ,""   ,""   ,""  ,"( E )","","B"     ,"B"     ,"B"     ,"B"     ,"V"  ,"V"  ,"V"  ,"V"  ,"V"  ,"V"  ,"V"  ,"V"  ,"V"  ,"V"  ,""                          ,""   ,""   ,""        ,""             ,""}, // 16 | F
/* 17 V */  {"" ,"" ,"W Y X","W Y X",""   ,""   ,""  ,""   ,"",""      ,""      ,""      ,""      ,"W Y X","W Y X","W Y X","W Y X","W Y X","W Y X","W Y X","W Y X","W Y X","W Y X",""                          ,""   ,""   ,""        ,""             ,""}, // 17 | V
/* 18 W */  {"" ,"" ,"+"  ,"-"  ,""   ,""   ,""  ,""   ,"",""      ,""      ,""      ,""      ,"^"  ,"^"  ,"^"  ,"^"  ,"^"  ,"^"  ,"^"  ,"^"  ,"^"  ,"^"  ,""                          ,""   ,""   ,""        ,""             ,""}, // 18 | W
/* 19 X */  {"^","^","^"  ,"^"  ,"^"  ,"^"  ,""  ,""   ,"",""      ,""      ,""      ,""      ,"Y X" ,"Y X" ,"Y X" ,"Y X" ,"Y X" ,"Y X" ,"Y X" ,"Y X" ,"Y X" ,"Y X" ,""                          ,""   ,""   ,""        ,""             ,""}, // 19 | X
/* 20 Y */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"",""      ,""      ,""      ,""      ,"0"  ,"1"  ,"2"  ,"3"  ,"4"  ,"5"  ,"6"  ,"7"  ,"8"  ,"9"  ,""                          ,""   ,""   ,""        ,""             ,""}, // 20 | Y
/* 21 Z */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"","P"     ,"Q"     ,"R"     ,"S"     ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                          ,""   ,""   ,""        ,""             ,""} // 21 | Z
    }; // predictiveTable[29][22]

    private static void partOne(BufferedReader br, BufferedWriter bw) throws IOException {
        String lineIn, specialChars = "():;,=*/+-";
        Character c; int slashCount = 0, spaceCount = 0;

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
                } else {
                    if (slashCount >= 2 && slashCount < 4) { // skip comments..
                        continue;
                    }
                    if (!Character.isWhitespace(c)) { // not a whitespace char.  duh.
                        if (spaceCount == 0 && specialChars.indexOf(c) != -1) { // ensures spaces in between special Chars
                            sb.append(' ');
                            sb.append(c);
                            sb.append(' ');
                            spaceCount++;
                        } else if (specialChars.indexOf(c) != -1) { // special char following a space.
                            sb.append(c);
                            sb.append(' '); // "P1 = - 3" vs "P1 = -3". this produces the former.
                            spaceCount = 1;
                        } else { // just another character.. nothing special
                            spaceCount = 0;
                            sb.append(c);
                        }
                    } else { // whitespace
                        if (spaceCount == 0 && sb.length() != 0) {
                            spaceCount++;
                            sb.append(' ');
                        }
                    }
                }
            }
            if (sb.length() != 0) { // we have a string. write to file. all whitespace and nonsense filtered out above
                //if (sb.toString().contains("PROGRAM")) {
                    //sb.append('\n');
                //} // rules say all blank lines must be removed, though it does not match sample output...
                sb.append('\n'); // what line string is complete without a carriage return
                bw.write(sb.toString());
                sb.delete(0, sb.length());
            }
        }
    }

    private static boolean partTwo(BufferedReader br) throws IOException {
        // This gives us an easy way to get some indexes....
        HashMap<String,Integer> rhsMap = new HashMap<>();
        for (int i = 0; i < rhsIndex.length; i++) {
            rhsMap.put(rhsIndex[i], i);
        }

        HashMap<String,Integer> lhsMap = new HashMap<>();
        for (int i = 0; i < lhsIndex.length; i++) {
            lhsMap.put(lhsIndex[i], i);
        }

        ArrayDeque<String> myStack = new ArrayDeque<>();
        String lineIn;
        System.out.println("===================================");
        System.out.println("| Beginning Part Two:");
        System.out.println("===================================");
        br.mark(1);
        boolean accepted = true;
        myStack.push("$");System.out.println("stack: " + myStack.toString());
        myStack.push("A");System.out.println("stack: " + myStack.toString());
        String tableValue, lineArray[], tableValueSplitter[], lhsHolder;
        Character charHolder;
        begin:
        while (br.ready() && accepted && !myStack.peek().equals("$")) {
            lineIn = br.readLine();
            lineArray = lineIn.split("\\s+");
            for (String readValue : lineArray) {
                lhsHolder = myStack.pop(); // A.... "pop: E " // B

                System.out.println("WORD: \"" + readValue + "\""); // S2017
                while (!lhsHolder.equals(readValue)) { // this probably needs to change too?
                    if(rhsMap.get(readValue) == null || readValue.equals("P") || readValue.equals("Q") || readValue.equals("R") || readValue.equals("S")) {
                        for (int j = 0; j < readValue.length(); j++) { // for each of the chars in the identifier... S 2 0 1 7
                            charHolder = readValue.charAt(j); // S 2 0 1 7 // 2
                            System.out.println("CHAR: " + charHolder);
                            while (!lhsHolder.equals(charHolder.toString()))  { // S 2 0 1 7

                                if (lhsHolder.equals("") || lhsMap.get(lhsHolder) == null || rhsMap.get(charHolder.toString()) == null) {
                                    accepted = false;
                                    break begin;
                                }

                                /*
                                System.out.println("lhsHolder = " + lhsHolder);
                                System.out.println("charHolder.toString() = " + charHolder.toString());
                                System.out.println("lhsMap.get(lhsHolder) = " + lhsMap.get(lhsHolder));
                                System.out.println("rhsMap.get(charHolder.toString()) = " + rhsMap.get(charHolder.toString()));
                                */

                                tableValue = predictiveTable[lhsMap.get(lhsHolder)][rhsMap.get(charHolder.toString())]; // "[E,(] = TQ" // will choke here // shoudln't be loooking up [B][S2017]

                                if (tableValue.equals("^")) { // handle lambda by ignoring and moving on..
                                    lhsHolder = myStack.pop(); // A.... "pop: E "
                                    continue;
                                }

                                tableValueSplitter = tableValue.split("\\s+"); // tableValueSplitter = {Z, C}
                                for (int k = tableValueSplitter.length - 1; k >= 0; k--) {
                                    myStack.push(tableValueSplitter[k]); // "push Q, T"
                                    System.out.println("stack: " + myStack.toString());
                                } // Pushes vars on stack correctly... in reverse order.
                                lhsHolder = myStack.pop(); // A.... "pop: E "
                                System.out.println("stack: " + myStack.toString());
                            }
                            lhsHolder = myStack.pop(); // A.... "pop: E "
                            System.out.println("stack: " + myStack.toString());
                        }
                        break;
                    } else {

                        if (lhsHolder.equals("") || lhsMap.get(lhsHolder) == null || rhsMap.get(readValue) == null) {
                            accepted = false;
                            break begin;
                        }

                        tableValue = predictiveTable[lhsMap.get(lhsHolder)][rhsMap.get(readValue)]; // "[E,(] = TQ" // will choke here // shoudln't be loooking up [B][S2017]

                        if (tableValue.equals("^")) { // handle lambda by ignoring and moving on..
                            lhsHolder = myStack.pop(); // A.... "pop: E "
                            continue;
                        }

                        tableValueSplitter = tableValue.split("\\s+");
                        for (int j = tableValueSplitter.length - 1; j >= 0; j--) {
                            myStack.push(tableValueSplitter[j]); // "push Q, T"
                            System.out.println("stack: " + myStack.toString());
                        }
                        lhsHolder = myStack.pop(); // A.... "pop: E "
                        System.out.println("stack: " + myStack.toString());
                    }
                } // end while lhsHolder != readValue || lhsHolder != "^"
                System.out.println("stack: " + myStack.toString());
            } // end for length of line - split all words
        } // end while - no more lines to read
        br.reset();
        return accepted;
    }

    private static void partThree(BufferedReader br, BufferedWriter bw) throws IOException {
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
        // Part One

        //System.out.println("predictiveTable.length = " + predictiveTable.length) ; // 29
        //System.out.println("predictiveTable[0].length = " + predictiveTable[0].length) ; // 29
        //System.exit(0);

        BufferedReader br = new BufferedReader(new FileReader(".//src//com//rsh//cpsc323//S2017.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter(".//src//com//rsh/cpsc323/finalv2.txt"));
        partOne(br, bw); bw.close(); br.close();

        // Display Results of Part One
        BufferedReader br2 = new BufferedReader(new FileReader(".//src//com//rsh//cpsc323//finalv2.txt"));
        br2.mark(1);
        System.out.println("==================================");
        System.out.println("| Results from Part One");
        System.out.println("==================================");
        while (br2.ready()) {
            System.out.println(br2.readLine()); // back to the back with a reset buffer...
        }
        br2.reset();

        // Part 2
        if (partTwo(br2)) {
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
        br2.reset();
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(".//src//com//rsh/cpsc323/finalv3.txt"));
        partThree(br2, bw2);
        br2.close(); bw2.close();
    }
}