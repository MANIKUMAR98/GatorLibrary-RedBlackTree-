import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GatorLibrary {
	
	//regular expression pattern to match the input values
	public static String insertBlockPattern = "InsertBook\\((\\d+), \"([^\"]+)\", \"([^\"]+)\", \"([^\"]+)\"\\)";
	public static String printBookPattern = "PrintBook\\((\\d+)\\)";
	public static String printBooksPattern = "PrintBooks\\((\\d+), (\\d+)\\)";
	public static String borrowBookPattern = "BorrowBook\\((\\d+), (\\d+), (\\d+)\\)";
	public static String returnBookPattern = "ReturnBook\\((\\d+), (\\d+)\\)";
	public static String deleteBookPattern = "DeleteBook\\((\\d+)\\)";
	public static String findClosestBookPattern = "FindClosestBook\\((\\d+)\\)";
	
	public static void main(String[] args) {
		if(args.length < 0) {
			System.out.println("Please provide the input file name");
			return;
		}
		String input_file_name = args[0];
		String[] inputFileName = input_file_name.split(".txt");
		
		Utility utility = new Utility();
		try {
			DataOutputStream access = new DataOutputStream(new FileOutputStream(inputFileName[0] + "_output_file.txt"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input_file_name), StandardCharsets.UTF_8));
	        String input;
	        RedBlackTree library = new RedBlackTree();
	        while ((input = reader.readLine()) != null) {
		        Pattern pattern;
		        Matcher matcher;
	           if(input.startsWith("InsertBook")) {
	        	   pattern = Pattern.compile(insertBlockPattern);
	        	   matcher = pattern.matcher(input);
	        	   if (matcher.find()) {
			            int bookID = Integer.parseInt(matcher.group(1));
			            String bookName = matcher.group(2);
			            String authorName = matcher.group(3);
			            String availabilityStatus = matcher.group(4);
			            library.insertBook(bookID, bookName, authorName, availabilityStatus);
			        } 
	           } else if(input.startsWith("PrintBooks")) {
	        	   pattern = Pattern.compile(printBooksPattern);
	        	   matcher = pattern.matcher(input);
	        	   if(matcher.find()) {
	        		   int start = Integer.parseInt(matcher.group(1));
	        		   int end = Integer.parseInt(matcher.group(2));
	        		   library.printBooks(start, end, utility, access);
	        		   access.write("---------------------------------------".getBytes());
	        		   access.writeUTF(System.lineSeparator());
	        	   }
	           } else if(input.startsWith("PrintBook")) {
	        	   pattern = Pattern.compile(printBookPattern);
	        	   matcher = pattern.matcher(input);
	        	   if (matcher.find()) {
	        		   int bookId = Integer.parseInt(matcher.group(1));
	        		   Book book = library.getBookFromLibrary(bookId);
	        		   if(book.getAuthorName() == null) {
	        			   utility.write("No book exists.", access);
	        		   } else {
	        			   utility.writeTheBookData(book, access);
	        		   }
	        		   access.write("---------------------------------------".getBytes());
	        		   access.writeUTF(System.lineSeparator());
	        	   }
	           } else if(input.startsWith("BorrowBook")) {
	        	   pattern = Pattern.compile(borrowBookPattern);
	        	   matcher = pattern.matcher(input);
	        	   if(matcher.find()) {
	        		   int patronId = Integer.parseInt(matcher.group(1));
	        		   int bookId = Integer.parseInt(matcher.group(2));
	        		   int patronPriority = Integer.parseInt(matcher.group(3));
	        		   library.borrowBook(patronId, bookId, patronPriority, access, utility);
	        		   access.write("---------------------------------------".getBytes());
		       		   access.writeUTF(System.lineSeparator());
	        	   }
	           } else if(input.startsWith("ReturnBook")) {
	        	   pattern = Pattern.compile(returnBookPattern);
	        	   matcher = pattern.matcher(input);
	        	   if(matcher.find()) {
	        		   int patronId = Integer.parseInt(matcher.group(1));
	        		   int bookId = Integer.parseInt(matcher.group(2));
	        		   library.returnBook(patronId, bookId, access, utility);
		        	   access.write("---------------------------------------".getBytes());
		       		   access.writeUTF(System.lineSeparator());
	        	   }
	           } else if(input.startsWith("DeleteBook")) {
	        	   pattern = Pattern.compile(deleteBookPattern);
	        	   matcher = pattern.matcher(input);
	        	   if(matcher.find()) {
	        		   int bookId = Integer.parseInt(matcher.group(1));
	        		   library.deleteBook(bookId, access, utility);
	        		   access.write("---------------------------------------".getBytes());
		       		   access.writeUTF(System.lineSeparator());
	        	   }
	        	   
	           } else if(input.startsWith("FindClosestBook")) {
	        	   library.printTree();
	        	   pattern = Pattern.compile(findClosestBookPattern);
	        	   matcher = pattern.matcher(input);
	        	   if(matcher.find()) {
	        		   int bookId = Integer.parseInt(matcher.group(1));
		        	   library.findClosestBooks(bookId, utility, access);
		        	   access.write("---------------------------------------".getBytes());
	        		   access.writeUTF(System.lineSeparator());
	        	   }
	           } else if(input.equals("ColorFlipCount()")) {
	        	   String res = "Color Flip Count: " + library.colorFlipCount;
	        	   access.write(res.getBytes());
	        	   access.writeUTF(System.lineSeparator());
	        	   access.write("---------------------------------------".getBytes());
	        	   access.writeUTF(System.lineSeparator());
	           } else if(input.equals("Quit()")) {
	        	   access.write("Program Terminated!!".getBytes());
	        	   access.writeUTF(System.lineSeparator());
	        	   access.write("---------------------------------------".getBytes());
	        	   System.exit(0);
	           }
	        }
	        reader.close();
	        access.close();
	        library.printTree();
		} catch (Exception e) {
			System.err.println("Expection occurred while reading the file "+ e.getMessage());
		} 
		
		
//		   RedBlackTree bst = new RedBlackTree();
//	    int[] arr = {4,6,5,8,9,7,3,10,500,100,180,120,44,33,11,77,66,88,86,84,85};
//	    for(int i=0;i<arr.length;i++)
//	    {
//	    	bst.insertBook(arr[i], null, null, null);
//	    }
//	    bst.printTree();
//	
//	    System.out.println("\nAfter deleting:");
//	    bst.deleteNode(2);
//	    bst.printTree();
	}
	
}
