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
import java.util.HashMap;

public class Main {

    private static String[][] predictiveTable = { // Painfully Long Predictive Parsing Table
            //00 01  02    03    04    05    06   07    08 09       10       11       12       13    14    15    16    17    18    19    20    21    22     23                         24    25    26        27           28
            //;  )   +     -     *     /     ,    (     :  P        Q        R        S        0     1     2     3     4     5     6     7     8     9      PROGRAM                    BEGIN END.  INTEGER   PRINT        $
/* 00 A */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"",""      ,""      ,""      ,""      ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,"PROGRAM B: D BEGIN J END.",""   ,""   ,""       ,""          ,""}, // 00 | A
/* 01 B */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"","ZC"    ,"ZC"    ,"ZC"    ,"ZC"    ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                         ,""   ,""   ,""       ,""          ,""}, // 01 | B
/* 02 C */  {"^","^","^"  ,"^"  ,"^"  ,"^"  ,"^" ,"^"  ,"","ZC"    ,"ZC"    ,"ZC"    ,"ZC"    ,"YC" ,"YC" ,"YC" ,"YC" ,"YC" ,"YC" ,"YC" ,"YC" ,"YC" ,"YC" ,""                         ,""   ,""   ,""       ,""          ,""}, // 02 | C
/* 03 D */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"",""      ,""      ,""      ,""      ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                         ,""   ,""   ,"I : G;" ,""          ,""}, // 03 | D
/* 04 G */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"","BH"    ,"BH"    ,"BH"    ,"BH"    ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                         ,""   ,""   ,""       ,""          ,""}, // 04 | G
/* 05 H */  {"^","" ,""   ,""   ,""   ,""   ,",G",""   ,"",""      ,""      ,""      ,""      ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                         ,""   ,""   ,""       ,""          ,""}, // 05 | H
/* 06 I */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"",""      ,""      ,""      ,""      ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                         ,""   ,""   ,"INTEGER",""          ,""}, // 06 | I
/* 07 J */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"","LK"    ,"LK"    ,"LK"    ,"LK"    ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                         ,""   ,""   ,""       ,"LK"        ,""}, // 07 | J
/* 08 K */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"","J"     ,"J"     ,"J"     ,"J"     ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                         ,""   ,"^"  ,""       ,"J"         ,""}, // 08 | K
/* 09 L */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"","N"     ,"N"     ,"N"     ,"N"     ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                         ,""   ,""   ,""       ,"M"         ,""}, // 09 | L
/* 10 M */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"",""      ,""      ,""      ,""      ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                         ,""   ,""   ,""       ,"PRINT (B);",""}, // 10 | M
/* 11 N */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"","B = E;","B = E;","B = E;","B = E;",""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                         ,""   ,""   ,""       ,""          ,""}, // 11 | N
/* 12 E */  {"" ,"" ,"TO" ,"TO" ,""   ,""   ,""  ,"TO" ,"","TO"    ,"TO"    ,"TO"    ,"TO"    ,"TO" ,"TO" ,"TO" ,"TO" ,"TO" ,"TO" ,"TO" ,"TO" ,"TO" ,"TO" ,""                         ,""   ,""   ,""       ,""          ,""}, // 12 | E
/* 13 O */  {"^","^","+TO","-TO",""   ,""   ,""  ,""   ,"",""      ,""      ,""      ,""      ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                         ,""   ,""   ,""       ,""          ,""}, // 13 | O
/* 14 T */  {"" ,"" ,"FU" ,"FU" ,""   ,""   ,""  ,"FU" ,"","FU"    ,"FU"    ,"FU"    ,"FU"    ,"FU" ,"FU" ,"FU" ,"FU" ,"FU" ,"FU" ,"FU" ,"FU" ,"FU" ,"FU" ,""                         ,""   ,""   ,""       ,""          ,""}, // 14 | T
/* 15 U */  {"^","^","^"  ,"^"  ,"*FU","/FU",""  ,""   ,"",""      ,""      ,""      ,""      ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                         ,""   ,""   ,""       ,""          ,""}, // 15 | U
/* 16 F */  {"" ,"" ,"V"  ,"V"  ,""   ,""   ,""  ,"(E)","","B"     ,"B"     ,"B"     ,"B"     ,"V"  ,"V"  ,"V"  ,"V"  ,"V"  ,"V"  ,"V"  ,"V"  ,"V"  ,"V"  ,""                         ,""   ,""   ,""       ,""          ,""}, // 16 | F
/* 17 V */  {"" ,"" ,"WYX","WYX",""   ,""   ,""  ,""   ,"",""      ,""      ,""      ,""      ,"WYX","WYX","WYX","WYX","WYX","WYX","WYX","WYX","WYX","WYX",""                         ,""   ,""   ,""       ,""          ,""}, // 17 | V
/* 18 W */  {"" ,"" ,"+"  ,"-"  ,""   ,""   ,""  ,""   ,"",""      ,""      ,""      ,""      ,"^"  ,"^"  ,"^"  ,"^"  ,"^"  ,"^"  ,"^"  ,"^"  ,"^"  ,"^"  ,""                         ,""   ,""   ,""       ,""          ,""}, // 18 | W
/* 19 X */  {"^","^","^"  ,"^"  ,"^"  ,"^"  ,""  ,""   ,"",""      ,""      ,""      ,""      ,"YX" ,"YX" ,"YX" ,"YX" ,"YX" ,"YX" ,"YX" ,"YX" ,"YX" ,"YX" ,""                         ,""   ,""   ,""       ,""          ,""}, // 19 | X
/* 20 Y */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"",""      ,""      ,""      ,""      ,"0"  ,"1"  ,"2"  ,"3"  ,"4"  ,"5"  ,"6"  ,"7"  ,"8"  ,"9"  ,""                         ,""   ,""   ,""       ,""          ,""}, // 20 | Y
/* 21 Z */  {"" ,"" ,""   ,""   ,""   ,""   ,""  ,""   ,"","P"     ,"Q"     ,"R"     ,"S"     ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""   ,""                         ,""   ,""   ,""       ,""          ,""} // 21 | Z
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
        HashMap<String,Integer> myMap = new HashMap<>();
        for (int i = 0; i < stringIndex.length; i++) {
            myMap.put(stringIndex[i], i);
        }

        String lineIn;
        System.out.println("===================================");
        System.out.println("| Part Two Input:");
        System.out.println("===================================");
        br.mark(1);
        while (br.ready()) {
            lineIn = br.readLine();
            System.out.println(lineIn);
        }
        br.reset();
        return true;
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

    private static String stringIndex[] = {";",")","+","-","*","/",",","(",":","P","Q","R","S","0","1","2","3","4","5","6","7","8","9","PROGRAM","BEGIN","END.","INTEGER","PRINT","$"};

    public static void main(String[] args) throws IOException {
        // Part One
        BufferedReader br = new BufferedReader(new FileReader(".//src//com//rsh//cpsc323//S2017.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter(".//src//com//rsh/cpsc323/finalv2.txt"));
        partOne(br, bw); bw.close(); br.close();

        // Part Two
        BufferedReader br2 = new BufferedReader(new FileReader(".//src//com//rsh//cpsc323//finalv2.txt"));
        br2.mark(1);
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
