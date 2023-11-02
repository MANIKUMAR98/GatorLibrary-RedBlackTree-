
public class Reservation {
	
	private int patronId;
	private int priorityNumber;
	private Long timeOfReservation;
	
	public Reservation(int patronId, int priorityNumber, Long timeOfReservation) {
		this.patronId = patronId;
		this.priorityNumber = priorityNumber;
		this.timeOfReservation = timeOfReservation;
	}
	
	public int getPatronId() {
		return this.patronId;
	}

	public int getPriorityNumber() {
		return priorityNumber;
	}

	public void setPriorityNumber(int priorityNumber) {
		this.priorityNumber = priorityNumber;
	}

	public Long getTimeOfReservation() {
		return timeOfReservation;
	}

	public void setTimeOfReservation(Long timeOfReservation) {
		this.timeOfReservation = timeOfReservation;
	}

	public void setPatronId(int patronId) {
		this.patronId = patronId;
	}

}
