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
import java.util.Vector;

public class Main {

    private static String rhsIndex[] = {";",")","+","-","*","/",",","(","P","Q","R","S","0","1","2","3","4","5","6","7","8","9","PROGRAM","END.","INTEGER","PRINT",":","$","BEGIN", "="};
    private static String lhsIndex[] = {"A","B","C","D","G","H","I","J","K","L","M","N","E","O","T","U","F","V","W","X","Y","Z"};
    private static String[][] predictiveTable = { // Unused columns: ":", "BEGIN", "$", moved to the end of table... kept just in case
            //00    01      02      03      04      05      06    07      08        09        10        11        12      13      14      15      16      17      18      19      20      21       22                           23    24        25              26     27      28	   29
            //;     )       +       -       *       /       ,     (       P         Q         R         S         0       1       2       3       4       5       6       7       8       9        PROGRAM                      END.  INTEGER   PRINT           :      $       BEGIN   =
/* 00 A */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"PROGRAM B ; D BEGIN J END.","5523","5524"   ,"5525"         ,"5526","5527","5528", "5529"}, // 00 | A
/* 01 B */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"Z C"    ,"Z C"    ,"Z C"    ,"Z C"    ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528", "5529"}, // 01 | B
/* 02 C */  {"^"   ,"^"   ,"^"    ,"^"    ,"^"    ,"^"    ,"^"   ,"^"    ,"Z C"    ,"Z C"    ,"Z C"    ,"Z C"    ,"Y C"  ,"Y C"  ,"Y C"  ,"Y C"  ,"Y C"  ,"Y C"  ,"Y C"  ,"Y C"  ,"Y C"  ,"Y C"  ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528", "^"}, // 02 | C
/* 03 D */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","I : G ;","5525"         ,"5526","5527","5528", "5529"}, // 03 | D
/* 04 G */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"B H"    ,"B H"    ,"B H"    ,"B H"    ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528", "5529"}, // 04 | G
/* 05 H */  {"^"  ,"5501","5502"  ,"5503" ,"5504" ,"5505" ,", G" ,"5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528", "5529"}, // 05 | H
/* 06 I */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","INTEGER","5525"         ,"5526","5527","5528", "5529"}, // 06 | I
/* 07 J */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"L K"    ,"L K"    ,"L K"    ,"L K"    ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"L K"          ,"5526","5527","5528", "5529"}, // 07 | J
/* 08 K */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"J"      ,"J"      ,"J"      ,"J"      ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"^"   ,"5524"   ,"J"            ,"5526","5527","5528", "5529"}, // 08 | K
/* 09 L */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"N"      ,"N"      ,"N"      ,"N"      ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"M"            ,"5526","5527","5528", "5529"}, // 09 | L
/* 10 M */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"PRINT ( B ) ;","5526","5527","5528", "5529"}, // 10 | M
/* 11 N */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"B = E ;","B = E ;","B = E ;","B = E ;","5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528", "5529"}, // 11 | N
/* 12 E */  {"5500","5501","T O"  ,"T O"  ,"5504" ,"5505" ,"5506","T O"  ,"T O"    ,"T O"    ,"T O"    ,"T O"    ,"T O"  ,"T O"  ,"T O"  ,"T O"  ,"T O"  ,"T O"  ,"T O"  ,"T O"  ,"T O"  ,"T O"  ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528", "5529"}, // 12 | E
/* 13 O */  {"^"   ,"^"   ,"+ T O","- T O","5504" ,"5505" ,"5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528", "5529"}, // 13 | O
/* 14 T */  {"5500","5501","F U"  ,"F U"  ,"5504" ,"5505" ,"5506","F U"  ,"F U"    ,"F U"    ,"F U"    ,"F U"    ,"F U"  ,"F U"  ,"F U"  ,"F U"  ,"F U"  ,"F U"  ,"F U"  ,"F U"  ,"F U"  ,"F U"  ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528", "5529"}, // 14 | T
/* 15 U */  {"^"   ,"^"   ,"^"    ,"^"    ,"* F U","/ F U","5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528", "5529"}, // 15 | U
/* 16 F */  {"5500","5501","V"    ,"V"    ,"5504" ,"5505" ,"5506","( E )","B"      ,"B"      ,"B"      ,"B"      ,"V"    ,"V"    ,"V"    ,"V"    ,"V"    ,"V"    ,"V"    ,"V"    ,"V"    ,"V"    ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528", "5529"}, // 16 | F
/* 17 V */  {"5500","5501","W Y X","W Y X","5504" ,"5505" ,"5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"W Y X","W Y X","W Y X","W Y X","W Y X","W Y X","W Y X","W Y X","W Y X","W Y X","5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528", "5529"}, // 17 | V
/* 18 W */  {"5500","5501","+"    ,"-"    ,"5504" ,"5505" ,"5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"^"    ,"^"    ,"^"    ,"^"    ,"^"    ,"^"    ,"^"    ,"^"    ,"^"    ,"^"    ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528", "5529"}, // 18 | W
/* 19 X */  {"^"   ,"^"   ,"^"    ,"^"    ,"^"    ,"^"    ,"5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"Y X"  ,"Y X"  ,"Y X"  ,"Y X"  ,"Y X"  ,"Y X"  ,"Y X"  ,"Y X"  ,"Y X"  ,"Y X"  ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528", "5529"}, // 19 | X
/* 20 Y */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"5508"   ,"5509"   ,"5510"   ,"5511"   ,"0"    ,"1"    ,"2"    ,"3"    ,"4"    ,"5"    ,"6"    ,"7"    ,"8"    ,"9"    ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528", "5529"}, // 20 | Y
/* 21 Z */  {"5500","5501","5502" ,"5503" ,"5504" ,"5505" ,"5506","5507" ,"P"      ,"Q"      ,"R"      ,"S"      ,"5512" ,"5513" ,"5514" ,"5515" ,"5516" ,"5517" ,"5518" ,"5519" ,"5520" ,"5521" ,"5522"                      ,"5523","5524"   ,"5525"         ,"5526","5527","5528", "5529"}  // 21 | Z
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

    private static void doWork(String tableValue, ArrayDeque<String> myStack) {
        String tableValueSplitter[];
        tableValueSplitter = tableValue.split("\\s+");
        for (int j = tableValueSplitter.length - 1; j >= 0; j--) {
            myStack.push(tableValueSplitter[j]);
        }
    }

    private static boolean partTwo(BufferedReader br) throws IOException {
        // Initialization Stuff..
        br.mark(1);
        String lineIn, stackHolder, readValue = "", lineArray[];
        ArrayDeque<String> myStack = new ArrayDeque<>();

        HashMap<String,Integer> rhsMap = new HashMap<>();
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

        myStack.push("$");
        myStack.push("A");
        System.out.println("stack: " + myStack.toString());

        int lineCounter = 1;
        int state = 0;
        Vector<String> variableList = new Vector<>();

        boolean read;
        while (br.ready()) {
            lineIn = br.readLine(); //reads next line
            lineArray = lineIn.split("\\s+"); //splits line into strings
            for (int i = 0; i < lineArray.length; i++) {
                readValue = lineArray[i];
                System.out.println("Word: " + readValue);

                // Checks for ";" at the end of each line except in the case of BEGIN or END. since those do not have ";" at end.
                if(i == lineArray.length - 1 && !readValue.equals(";") && !readValue.equals("BEGIN") && !readValue.equals("END.")) {
                    System.out.println("ERROR ( line " + lineCounter + " ): ; is missing");
                    return false;
                }

                read = false;
                while(!read){
                    stackHolder = myStack.pop();
                    System.out.println("stack: " + myStack.toString());
                    if(stackHolder.contains("55")){	// Checks for illegal expression
                        if(readValue.equals("(")){ // Checks for PRINT
                            System.out.println("ERROR ( line " + lineCounter + " ): PRINT is expected");
                        } else {
                            System.out.println("ERROR ( line " + lineCounter + " ): illegal expression");
                        }
                        return false;
                    } else if(stackHolder.equals("^")) { // Checks for lambda and moves to next word
                        System.out.println("Lambda encountered.. skipping");
                    } else if(stackHolder.equals(readValue)){ // Checks for an input match
                        if(stackHolder.equals(";")){ // Checks if input is a ;
                            state++; // Increments the state
                        }
                        break;
                    } else if(rhsMap.get(stackHolder) != null){ //  Checks if stackHolder is a member of rhsMap
                        System.out.println("ERROR ( line " + lineCounter + " ): " + stackHolder + " is expected or missing");
                        return false;
                    } else {
                        if(stackHolder.equals("A") && !readValue.equals("PROGRAM")){ // Checks for missing program at beginning of program
                            System.out.println("ERROR ( line " + lineCounter + " ): PROGRAM is expected");
                            return false;
                        } else if(stackHolder.equals("D") && !readValue.equals("INTEGER")){ // Checks for missing INTEGER at beginning of line
                            System.out.println("ERROR ( line " + lineCounter + " ): INTEGER is expected");
                            return false;
                        } else if(stackHolder.equals("H") && !readValue.equals(",") && !readValue.equals(";")){ // Checks for missing comma in integer section
                            System.out.println("ERROR ( line " + lineCounter + " ): , is missing");
                            return false;
                        } else if(rhsMap.get(readValue) == null || readValue.equals("P") || readValue.equals("Q") || readValue.equals("R") || readValue.equals("S")) {
                            int readIter = 0;
                            while(readIter < readValue.length()){
                                if(stackHolder.contains("55") || rhsMap.get("" + readValue.charAt(readIter)) == null){ // Checks if not a valid character in table
                                    System.out.println("ERROR ( line " + lineCounter + " ): Invalid identifier");
                                    return false;
                                } else if(stackHolder.equals("" + readValue.charAt(readIter))) { // Checks for input match
                                    readIter++;    // Increments the read iterator
                                    if (readIter == readValue.length()) {
                                        if (state <= 1 && Character.isLetter(readValue.charAt(0))) {
                                            System.out.println("adding to variableList : readValue = " + readValue);
                                            variableList.addElement(readValue);
                                        } else {
                                            if (Character.isLetter(readValue.charAt(0)) && !variableList.contains(readValue)) {
                                                System.out.println("ERROR ( line " + lineCounter + " ): Unknown identifier");
                                                return false;
                                            }
                                        }
                                    }
                                } else if (stackHolder.equals("^")) {
                                } else {
                                    System.out.println("stackHolder = " + stackHolder);
                                    System.out.println("readIter = " + readIter);
                                    System.out.println("myStack = " + myStack);
                                    doWork(predictiveTable[lhsMap.get(stackHolder)][rhsMap.get("" + readValue.charAt(readIter))], myStack);
                                }
                                stackHolder = myStack.pop();
                                System.out.println("stack: " + myStack.toString());
                                read = true; // Allows reader to read next word
                            }
                        } else if ((stackHolder.equals("J") && !variableList.contains(readValue)) && !rhsMap.containsKey(readValue)) {
                            System.out.println("ERROR ( line " + lineCounter + " ): Unknown identifier");
                            return false;
                        } else{
                            doWork(predictiveTable[lhsMap.get(stackHolder)][rhsMap.get(readValue)], myStack);
                        }
                    }
                }
            } // end for length of line - split all words
            lineCounter++;
        } // end while - no more lines to read

        if(state >= 3 && !readValue.equals("END.")){ //checks for END. at end of file
            System.out.println("ERROR ( line " + lineCounter + " ): END. is expected");
            return false;
        } else {
            br.reset();
            return true;
        }
    }

    private static void partThree(BufferedReader br) throws IOException { // Austin? :P
        System.out.println("======================");
        System.out.println("| Translation Phase 3");
        System.out.println("======================");
        System.out.println("see program.cpp");
        Boolean begin = false;
        String lineIn;
        FileWriter writer = new FileWriter(".//src//com//rsh//cpsc323//program.cpp");

        while (br.ready())
        {
            // using new if's instead of else if's in case input is a single line
            lineIn = br.readLine();
            if(lineIn.contains("PROGRAM")) {
                writer.write("#include <iostream>\n");
                writer.write("using namespace std;\n");
                writer.write("int main()\n");
                writer.write("{\n");
            }
            if(lineIn.contains("INTEGER :")) {
                writer.write("\tint");
                writer.write(lineIn.substring(9) + "\n");
            }
            if(lineIn.contains("BEGIN")) {
                begin = true;
            }
            if(lineIn.contains("END")) {
                begin = false;
                writer.write("\treturn 0;\n");
                writer.write("}\n");
            }
            if(begin && !lineIn.contains("BEGIN")) {
                if(lineIn.contains("PRINT")) {
                    writer.write("\tcout<<");
                    lineIn = lineIn.replace('(', ' ');
                    lineIn = lineIn.replace(')', ' ');
                    for(int index = 6; index < lineIn.length()-3; index++) {
                        if(lineIn.charAt(index) != ' ') {
                            writer.write(lineIn.charAt(index));
                        }
                    }
                    writer.write("<<endl;\n");
                } else {
                    if(!lineIn.contains("BEGIN")) {
                        writer.write('\t'+lineIn+"\n");
                    }
                }
            }
        }
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(".//src//com//rsh//cpsc323//S2017.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter(".//src//com//rsh//cpsc323//finalv2.txt"));
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
        partThree(br); // Begin Part 3
        br.close(); bw.close();
    }
}