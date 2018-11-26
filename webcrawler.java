// File name: webcrawler.java
// Author: George Perez
// Class: CSCI 392 Fall 2018
// Description: This program takes in a page filename from the command line and accesses it through
//              the winthrop domain. Then, it scans the webpage for any anchor tags, indicating an accessable link.
//              Finally, it outputs all the links found, whether or not they're bad.
//              Currently will probably break if something like this is just text:<p> a href="blah"> this was all just part of a paragraph, no tags were used </p>
//              Could most likely be fixed by extending the state table to accept all forms of < (< and &lt;), therefore properly recognizing the start of a tag

import java.net.*;
import java.io.*;

public class webcrawler
{
 public static void main(String[] args)
 {
   int[][] stateTable = new int[][] // Table used for character matching
   {//0  1  2  3  4  5  6  7
/*a*/{1, 0, 0, 0, 0, 0, 0, 0},
/* */{0, 2, 2, 0, 0, 0, 6, 7}, // <- This line takes care of the following possibilities:
/*h*/{0, 0, 3, 0, 0, 0, 0, 0}, // a href ="filename"
/*r*/{0, 0, 0, 4, 0, 0, 0, 0}, // a href = "filename"
/*e*/{0, 0, 0, 0, 5, 0, 0, 0}, // a href= "filename"
/*f*/{0, 0, 0, 0, 0, 6, 0, 0}, // a  href = "filename"  and all variations
/*=*/{0, 0, 0, 0, 0, 0, 7, 0}  // It basically allows for more spaces between the parts of the tag
}; // Row: What we just saw
   // Column: What state we're in

   System.out.println("Starting Web Tester\n");

   if (args.length == 0)
   {
     System.out.println("No file specified (please provide in the command line); e.g.: smithj/csci100/default.htm | Exiting...\n\n");
     System.exit(1);
   }

   try
   {
     // Connect to the server
     Socket sock = new Socket ("faculty.winthrop.edu",80);

     // Get the reading and writing streams
     InputStream sin = sock.getInputStream();
     BufferedReader fromServer = new BufferedReader(new InputStreamReader(sin));
     OutputStream sout = sock.getOutputStream();
     PrintWriter toServer = new PrintWriter (new OutputStreamWriter(sout));

     // Build the requested message
     String outmsg = "GET https://faculty.winthrop.edu/";
     outmsg += args[0];
     outmsg += " HTTP/1.0\r\n\r\n";

     // Send request to server
     toServer.print (outmsg);
     toServer.flush();

     // Read responses
     String inputline = fromServer.readLine();
     String pageString = ""; // To combine all lines into a string for parsing

     while (inputline != null) // Until the end of the stream
     {
       inputline = fromServer.readLine(); // Read the line in
       pageString += inputline; // Add it to the big string
     }

     //System.out.println(pageString); // Uncomment to view the string

     int currentState = 0;
     int charCount = 0; // What character we're at in the string
     int character; // The character equivalent in the table
     String urlName = ""; // The URL to print out

     System.out.println("All links within the page:");

     while(charCount != pageString.length()) // Iterating through the entire webpage
     {
       switch(pageString.charAt(charCount))
       {
         case 'a': character = 0; break;
         case ' ': character = 1; break;
         case 'h': character = 2; break;
         case 'r': character = 3; break;
         case 'e': character = 4; break;
         case 'f': character = 5; break;
         case '=': character = 6; break;
         default:  currentState = 0; charCount++; continue;
       } // (default) If the character doesn't match any of these, restart at the start state, iterate through the string, and restart the loop
       currentState = stateTable[character][currentState]; // Find new state from table using new indices

       if(currentState == 7) // If at a proper =
       {

         if(pageString.charAt(charCount + 1) == '&') // If the next set of characters are &quot;
         {
           charCount += 7; // Skip over and start at the next character
         }
         else if(pageString.charAt(charCount + 1) == ' ') // If the next character is a space, continue iterating
         {
           charCount++;
           continue;
         }
         else if (pageString.charAt(charCount + 1) == '"') // If the next character is "
         {
           charCount += 2;
         }
         else // If the character is the beginning of the filename, start iterating through
         {
           charCount++;
         }

         while((pageString.charAt(charCount) != '&') && (pageString.charAt(charCount) != '"') && (pageString.charAt(charCount) != '>'))
          { // While not at the end of the filename (since filenames begin and end with some variation of the quotation mark, or >)
            urlName += pageString.charAt(charCount); // Add the current character to the URL
            charCount++; // Increment through the filename
          }
          System.out.println(urlName.trim()); // Trims in case there's space before the filename
          urlName = ""; // Reset the URL for a new entry
          continue;
       }
       charCount++; // Iterating through the big string
     }
   }
   catch (Exception e)
   {
      System.out.println(e.getMessage());
   }

   System.out.println("\nDone with Web Tester");
 }
}
