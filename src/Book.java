import java.util.List;
import java.util.stream.Collectors;

public class Book {

	private int bookId;
	private String bookName;
	private String authorName;
	private String availabilityStatus;
	private int borrowedBy;
	private List<Reservation> reservationHeap;
	Book left;
	Book right;
	int color;
	Book parent;
	
	public Book() {
	}
	
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	
	public int getBookId() {
		return this.bookId;
	}

	//this tostring() method will help us to create the string by the book so we can write it into file
	@Override
	public String toString() {
	    String borrowedByString = borrowedBy == 0 ? "None" : String.valueOf(borrowedBy);
	    return String.format("BookID = %d\nTitle = \"%s\"\nAuthor = \"%s\"\nAvailability = \"%s\"\nBorrowedBy = %s\nReservations = %s",
	            bookId, bookName, authorName, availabilityStatus, borrowedByString, reservationHeap.stream().map(Reservation::getPatronId).collect(Collectors.toList()).toString());
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String isAvailabilityStatus() {
		return availabilityStatus;
	}

	public void setAvailabilityStatus(String availabilityStatus) {
		this.availabilityStatus = availabilityStatus;
	}

	public String getAvailabilityStatus() {
		return availabilityStatus;
	}

	public int getBorrowedBy() {
		return borrowedBy;
	}

	public void setBorrowedBy(int borrowedBy) {
		this.borrowedBy = borrowedBy;
	}

	public List<Reservation> getReservationHeap() {
		return reservationHeap;
	}

	public void setReservationHeap(List<Reservation> reservationHeap) {
		this.reservationHeap = reservationHeap;
	}
}
