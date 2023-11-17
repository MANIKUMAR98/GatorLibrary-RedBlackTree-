import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utility {
	
	Long versionNumber = 0L;
	
	public RedBlackTree library = new RedBlackTree();
	
	//this is common method to write the data to the file
	public void write(String data, OutputStream access) throws IOException {
		access.write(data.getBytes());
		this.addNewLine(access);
	}
	
	//this method will write the entire book data into file
	public void writeTheBookData(Book book, OutputStream access) throws IOException {
		String bookString = book.toString();
		access.write(bookString.getBytes());
		this.addNewLine(access);
	}
	
	public String getDataBasedOnAvailability(Book book) {
		return book.getAvailabilityStatus().toLowerCase().equals("yes") ? "None" : String.valueOf(book.getBorrowedBy());
	}
	
	//this method will give the patron id's in reservation heap
	public List<Integer> getReservations(List<Reservation> reservationList) {
		if(reservationList != null && !reservationList.isEmpty()) {
			return reservationList.stream().map(Reservation::getPatronId).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}
	
	//this method helps to create string of patron id's from reservation heap to write the data to the file
	public String reservationString(List<Reservation> reservationList) {
		String str = "";
		if(reservationList != null && !reservationList.isEmpty()) {
			for(Reservation reservation: reservationList) {
				int patronId = reservation.getPatronId();
				str = str + String.valueOf(patronId) +", ";
			}
		}
		return str.length() > 0 ? str.substring(0, str.length()-2) : str;
	}
	
	
	//This method helps to maintain the unique reservation time
	public Long getTimestampForReservation() {
		return ++versionNumber;
	}
	
	public void addNewLine(OutputStream access) throws IOException {
		access.write("\n".getBytes());
	}
}
