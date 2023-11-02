import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utility {
	
	public RedBlackTree library = new RedBlackTree();
	
	public synchronized void write(String data, DataOutputStream access) throws IOException {
		access.write(data.getBytes());
		access.writeUTF(System.lineSeparator());
	}
	
	public synchronized void writeTheBookData(Book book, DataOutputStream access) throws IOException {
		String bookId = "BookID = " + book.getBookId();
		access.write(bookId.getBytes());
		access.writeUTF(System.lineSeparator());
		String title = "Title = " + book.getBookName();
		access.write(title.getBytes());
		access.writeUTF(System.lineSeparator());
		String author = "Author = " + book.getAuthorName();
		access.write(author.getBytes());
		access.writeUTF(System.lineSeparator());
		String availability = "Availability = " + book.getAvailabilityStatus();
		access.write(availability.getBytes());
		access.writeUTF(System.lineSeparator());
		String bowrrowedBy = "BorrowedBy = " + this.getDataBasedOnAvailability(book);
		access.write(bowrrowedBy.getBytes());
		access.writeUTF(System.lineSeparator());
		String reservations = "Reservations = " + this.getReservations(book.getReservationHeap()).toString();
		access.write(reservations.getBytes());
		access.writeUTF(System.lineSeparator());
	}
	
	public String getDataBasedOnAvailability(Book book) {
		return book.getAvailabilityStatus().toLowerCase().equals("yes") ? "None" : String.valueOf(book.getBorrowedBy());
	}
	
	public List<Integer> getReservations(List<Reservation> reservationList) {
		List<Integer> reservations = new ArrayList<>();
		if(reservationList != null && !reservationList.isEmpty()) {
			for(Reservation res : reservationList) {
				 reservations.add(res.getPatronId());
			}	
		}
		return reservations;
	}
}
