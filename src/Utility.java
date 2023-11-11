import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utility {
	
	public RedBlackTree library = new RedBlackTree();
	
	public synchronized void write(String data, DataOutputStream access) throws IOException {
		access.write(data.getBytes());
		access.writeUTF(System.lineSeparator());
	}
	
	public void writeTheBookData(Book book, DataOutputStream access) throws IOException {
		String bookString = book.toString();
		access.write(bookString.getBytes());
		access.writeUTF(System.lineSeparator());
	}
	
	public String getDataBasedOnAvailability(Book book) {
		return book.getAvailabilityStatus().toLowerCase().equals("yes") ? "None" : String.valueOf(book.getBorrowedBy());
	}
	
	public List<Integer> getReservations(List<Reservation> reservationList) {
		if(reservationList != null && !reservationList.isEmpty()) {
			return reservationList.stream().map(Reservation::getPatronId).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}
	
	public String reservationString(List<Reservation> reservationList) {
		String str = "";
		for(Reservation reservation: reservationList) {
			int patronId = reservation.getPatronId();
			str = str + String.valueOf(patronId) +", ";
		}
		return str.length() > 0 ? str.substring(0, str.length()-2) : str;
	}
}
