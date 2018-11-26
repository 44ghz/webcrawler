# webcrawler
A webcrawler that takes a file from faculty.winthrop.edu and prints out the links in the page.

/// Purpose ///

This was created for my Java course, and is meant to take in a file from the faculty.winthrop.edu domain. When it loads the page, it will look for and output any external links or download links. 

/// How to use ///

The program takes in the filename by the command line, and the domain can be changed by altering the socket and the GET function within main. An example of a filename would be /smithj/csci101/default.htm . 

/// How it works ///

This program works by reading in the entirety of the html on a given page, and parses that string. It looks for anchor tags and compares them to a state table, built with a finite state machine. If an achor tag is found, it looks for the filename within, and the name is printed.
