import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class gatorLibrary {
	
	//regular expression pattern to match the input values
	public static String insertBlockPattern = "InsertBook\\((\\d+),\\s*\"([^\"]+)\",\\s*\"([^\"]+)\",\\s*\"([^\"]+)\"\\)";
	public static String printBookPattern = "PrintBook\\((\\d+)\\)";
	public static String printBooksPattern = "PrintBooks\\((\\d+),\\s*(\\d+)\\)"; 
	public static String borrowBookPattern = "BorrowBook\\((\\d+),\\s*(\\d+),\\s*(\\d+)\\)";
	public static String returnBookPattern = "ReturnBook\\((\\d+),\\s*(\\d+)\\)";
	public static String deleteBookPattern = "DeleteBook\\((\\d+)\\)";
	public static String findClosestBookPattern = "FindClosestBook\\((\\d+)\\)";
	
	public static void main(String[] args) {
		//if there are no arguments then returns without proceeding further
		if(args.length < 0) {
			System.out.println("Please provide the input file name");
			return;
		}
		
		//will read the file name from the command line argument
		String input_file_name = args[0];
		String[] inputFileName = input_file_name.split(".txt");
		Utility utility = new Utility();
		OutputStream access = null;
		try {
			access = new FileOutputStream(new File(inputFileName[0] + "_output_file.txt"));
			//based on the relative path and the file name Buffered reader will start reading the file
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input_file_name)));
	        String input;
	        RedBlackTree library = new RedBlackTree();
	        while ((input = reader.readLine()) != null) {
		        Pattern pattern;
		        Matcher matcher;
		        
	           if(input.startsWith("InsertBook")) {
	        	   //compiling the regex pattern
	        	   pattern = Pattern.compile(insertBlockPattern);
	        	   //matching the pattern against the input
	        	   matcher = pattern.matcher(input);
	        	   //if there is a match this if block will execute
	        	   if (matcher.find()) {
	        		   //Once match is found will start grouping the values from pattern matcher
			            int bookID = Integer.parseInt(matcher.group(1));
			            String bookName = matcher.group(2);
			            String authorName = matcher.group(3);
			            String availabilityStatus = matcher.group(4);
			            //this method responsible for inserting the book into library
			            library.insertBook(bookID, bookName, authorName, availabilityStatus);
			        } 
	           } else if(input.startsWith("PrintBooks")) {
	        	   //compiling the regex pattern
	        	   pattern = Pattern.compile(printBooksPattern);
	        	   //matching the pattern against the input
	        	   matcher = pattern.matcher(input);
	        	   //if there is a match this if block will execute
	        	   if(matcher.find()) {
	        		 //Once match is found will start grouping the values from pattern matcher
	        		   int start = Integer.parseInt(matcher.group(1));
	        		   int end = Integer.parseInt(matcher.group(2));
	        		   //this method is responsible for printing the books from range x to y
	        		   library.printBooks(start, end, utility, access);
	        		   utility.addNewLine(access);
	        	   }
	           } else if(input.startsWith("PrintBook")) {
	        	   //compiling the regex pattern
	        	   pattern = Pattern.compile(printBookPattern);
	        	   //matching the pattern against the input
	        	   matcher = pattern.matcher(input);
	        	   //if there is a match this if block will execute
	        	   if (matcher.find()) {
	        		 //Once match is found will start grouping the values from pattern matcher
	        		   int bookId = Integer.parseInt(matcher.group(1));
	        		   //this method will search the library and get the book if present
	        		   Book book = library.getBookFromLibrary(library.getRoot() ,bookId);
	        		   if(book == null) {
	        			   utility.write("Book " +bookId+" not found in the library", access);
	        		   } else {
	        			   //if book is present then will write the book data into file
	        			   utility.writeTheBookData(book, access);
	        		   }
	        		   //this is lines separator makes sire that there is space after each write operation 
	        		   utility.addNewLine(access);
	        	   }
	           } else if(input.startsWith("BorrowBook")) {
	        	   //compiling the regex pattern
	        	   pattern = Pattern.compile(borrowBookPattern);
	        	   //matching the pattern against the input
	        	   matcher = pattern.matcher(input);
	        	   //if there is a match this if block will execute
	        	   if(matcher.find()) {
	        		   //Once match is found will start grouping the values from pattern matcher
	        		   int patronId = Integer.parseInt(matcher.group(1));
	        		   int bookId = Integer.parseInt(matcher.group(2));
	        		   int patronPriority = Integer.parseInt(matcher.group(3));
	        		   //this method will check the book exists in library and if it is not allocated to anyone then will lend the book,
	        		   //if it is already borrowed the will add the Patron to the reservation list based on the priority if tow patrons
	        		   //have same priority then will break the tie based on the unique time stamp(first come first serve)
	        		   library.borrowBook(patronId, bookId, patronPriority, access, utility);
	        		   utility.addNewLine(access);
	        	   }
	           } else if(input.startsWith("ReturnBook")) {
	        	   //compiling the regex pattern
	        	   pattern = Pattern.compile(returnBookPattern);
	        	   //matching the pattern against the input
	        	   matcher = pattern.matcher(input);
	        	   //if there is a match this if block will execute
	        	   if(matcher.find()) {
	        		   //Once match is found will start grouping the values from pattern matcher
	        		   int patronId = Integer.parseInt(matcher.group(1));
	        		   int bookId = Integer.parseInt(matcher.group(2));
	        		   //this method makes that patron returns the book and allocated the book to the patron in reservation list
	        		   library.returnBook(patronId, bookId, access, utility);
	        		   utility.addNewLine(access);
	        	   }
	           } else if(input.startsWith("DeleteBook")) {
	        	   //compiling the regex pattern
	        	   pattern = Pattern.compile(deleteBookPattern);
	        	   //matching the pattern against the input
	        	   matcher = pattern.matcher(input);
	        	   //if there is a match this if block will execute
	        	   if(matcher.find()) {
	        		   //Once match is found will start grouping the values from pattern matcher
	        		   int bookId = Integer.parseInt(matcher.group(1));
	        		   //this method makes sure that book is removed from the library and remove the patrons from the list as book is no longer available
	        		   library.deleteBookFromLibrary(bookId, access, utility);
	        		   utility.addNewLine(access);
	        	   }
	           } else if(input.startsWith("FindClosestBook")) {
	        	   //compiling the regex pattern
	        	   pattern = Pattern.compile(findClosestBookPattern);
	        	   //matching the pattern against the input
	        	   matcher = pattern.matcher(input);
	        	   //if there is a match this if block will execute
	        	   if(matcher.find()) {
	        		   //Once match is found will start grouping the values from pattern matcher
	        		   int bookId = Integer.parseInt(matcher.group(1));
	        		   //this method finds the closest book in the library if exact book is found then will write the data else will get the closest book and 
	        		   //if there are two books closest to the book we are searching for then we will write both
		        	   library.findClosestBooks(bookId, utility, access);
		        	   utility.addNewLine(access);
	        	   }
	           } else if(input.equals("ColorFlipCount()")) {
	        	   //here will keep of track of the color change of books in the library
	        	   String res = "Color Flip Count: " + library.colorFlipCount;
	        	   access.write(res.getBytes());
	        	   utility.addNewLine(access);
	        	   utility.addNewLine(access);
	           } else if(input.equals("Quit()")) {
	        	   access.write("Program Terminated!!".getBytes());
	        	   //this line makes sure that program terminates
	        	   System.exit(0);
	           }
	        }
	        reader.close();
	        access.close();
		} catch (Exception e) {
			System.err.println("Expection occurred while reading the file "+ e);
		} finally {
			if(access != null) {
				try {
					access.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
