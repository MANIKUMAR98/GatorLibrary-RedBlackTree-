import java.util.List;

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
