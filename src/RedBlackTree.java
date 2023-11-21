
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class RedBlackTree {
	
	private Book root;
	int colorFlipCount = 0;
	Map<Integer, Book> treeMap = new TreeMap<>();
	Map<Integer, Integer> dictionary = new HashMap<>();
	Map<Integer, Integer> colorChanged = new HashMap<>();
	
	/**
	 * Recursively searches for a book in the library's binary search tree based on the provided book ID.
	 *
	 * This method performs a recursive search in the library's binary search tree to find the
	 * book with the specified ID. If the book is found, it is returned; otherwise, null is returned.
	 *
	 * @param book  The current node in the binary search tree.
	 * @param value The book ID to search for.
	 * @return      The Book object with the specified ID if found, or null if not found.
	 */
	public Book getBookFromLibrary(Book book, int value) {
		if (book == null || value == book.getBookId()) {
			return book;
		}
		if (value < book.getBookId()) {
			return getBookFromLibrary(book.left, value);
		}
		return getBookFromLibrary(book.right, value);
	}

	/**
	 * Deletes a book from the library based on the provided book ID and updates the library
	 * records accordingly. The method also cancels reservations made for the deleted book
	 * and applies necessary Red-Black Tree balancing to maintain the tree's properties.
	 *
	 * This method first searches for the book with the given ID in the library. If the book is
	 * found, it generates a message indicating the unavailability of the book and, if applicable,
	 * cancels reservations made for the book. The method then removes the book from the dictionary,
	 * calls the `deleteBook` method to perform the deletion, and finally checks for potential color
	 * changes in the tree nodes, updating the color flip count.
	 *
	 * @param bookId  The ID of the book to be deleted from the library.
	 * @param access  The output stream to write the result message.
	 * @param utility An instance of the Utility class for book and reservation management.
	 * @throws IOException If an I/O error occurs during writing to the output stream.
	 */
	public void deleteBookFromLibrary(int bookId, OutputStream access, Utility utility) throws IOException {
		Book book = getBookFromLibrary(root, bookId);
		if (book == null) {
			utility.write("Book "+bookId+ " not found in the Library", access);
			return;
		}
		String message = "Book " + bookId + " is no longer available";
		int size = book.getReservationHeap().size();
		if(size != 0) {
			String reservationString = utility.reservationString(book.getReservationHeap());
			if(size == 1) {
				message = message + ". Reservation made by Patron " + reservationString + " has been cancelled!";
			} else {
				message = message +  ". Reservations made by Patrons " + reservationString + " have been cancelled!";
			}
		}
		utility.write(message, access);
		//will remove the book from dictionary map
		this.dictionary.remove(book.getBookId());
		//call the deleteBook method and fix the violations 
		this.deleteBook(book);
		//once the delete operation is performed check for potential color change of the node
		this.updateColorFlipCount();
	}

	// Fix the violation after the insert is done
	private void ficViolationOfInsert(Book child) {
		Book uncle;
		while (child.parent.color == 1) {
			if (child.parent == child.parent.parent.right) {
				uncle = child.parent.parent.left;
				//if uncle is red just do the color flip and rotation not required
				if (uncle != null && uncle.color == 1) {
					uncle.color = 0;
					this.colorChanged.put(uncle.getBookId(), 0);
					child.parent.color = 0;
					this.colorChanged.put(child.parent.getBookId(), 0);
					if(child.parent.parent != root) {
						child.parent.parent.color = 1;
						this.colorChanged.put(child.parent.parent.getBookId(), 1);
					}
					child = child.parent.parent;
				} else {
					//if uncle is black  then do the appropriate rotation and do the color flip
					if (child == child.parent.left) {
						child = child.parent;
						rightRotate(child);
					}
					child.parent.color = 0;
					this.colorChanged.put(child.parent.getBookId(), 0);
					child.parent.parent.color = 1;
					this.colorChanged.put(child.parent.parent.getBookId(), 1);
					leftRotate(child.parent.parent);
				}
			} else {
				uncle = child.parent.parent.right;
				//if uncle is red just do the color flip and rotation not required
				if (uncle != null && uncle.color == 1) {
					uncle.color = 0;
					this.colorChanged.put(uncle.getBookId(), 0);
					child.parent.color = 0;
					this.colorChanged.put(child.parent.getBookId(), 0);
					if(child.parent.parent != root) {
						child.parent.parent.color = 1;
						this.colorChanged.put(child.parent.parent.getBookId(), 1);
					}
					child = child.parent.parent;
				} else {
					//if uncle is black  then do the appropriate rotation and do the color flip
					if (child == child.parent.right) {
						child = child.parent;
						leftRotate(child);
					}
					child.parent.color = 0;
					this.colorChanged.put(child.parent.getBookId(), 0);
					child.parent.parent.color = 1;
					this.colorChanged.put(child.parent.parent.getBookId(), 1);
					rightRotate(child.parent.parent);
				}
			}
			if (child == root) {
				break;
			}
		}
		root.color = 0;
		this.colorChanged.put(root.getBookId(), 0);
	}

	//instantiate the Red black tree
	public RedBlackTree() {
	}


	//this method is responsible for doing the right at the given node x 
	public void leftRotate(Book x) {
		Book y = x.right;
		x.right = y.left;
		if (y.left != null) {
			y.left.parent = x;
		}
		y.parent = x.parent;
		if (x.parent == null) {
			this.root = y;
		} else if (x == x.parent.left) {
			x.parent.left = y;
		} else {
			x.parent.right = y;
		}
		y.left = x;
		x.parent = y;
	}

	//this method is responsible for doing the left at the given node x 
	public void rightRotate(Book x) {
		Book y = x.left;
		x.left = y.right;
		if (y.right != null) {
			y.right.parent = x;
		}
		y.parent = x.parent;
		if (x.parent == null) {
			this.root = y;
		} else if (x == x.parent.right) {
			x.parent.right = y;
		} else {
			x.parent.left = y;
		}
		y.right = x;
		x.parent = y;
	}

	//this helps to create new book when we have to insert new book into library
	public Book getBook(int bookId, String bookName, String authorName, String availabilityStatus) {
		Book book = new Book();
		book.setBookId(bookId);
		book.setAuthorName(authorName);
		book.setAvailabilityStatus(availabilityStatus);
		book.setBookName(bookName);
		book.setBorrowedBy(0);
		book.setReservationHeap(new ArrayList<>());
		book.parent = null;
		book.left = null;
		book.right = null;
		book.color = 1;
		return book;
	}

	/**
	 * Inserts a new book into the library's binary search tree and applies Red-Black Tree
	 * balancing to maintain the tree's properties.
	 *
	 * This method creates a new book node with the provided details and inserts it into the
	 * Red-Black Tree. It then applies the necessary rotations and color adjustments to ensure
	 * that the Red-Black Tree properties are maintained. The method also updates a dictionary
	 * to keep track of the color flip count for each book ID.
	 *
	 * @param bookId            The ID of the new book.
	 * @param bookName          The name of the new book.
	 * @param authorName        The author of the new book.
	 * @param availabilityStatus The availability status of the new book.
	 */
	public void insertBook(int bookId, String bookName, String authorName, String availabilityStatus) {
		Book node = this.getBook(bookId, bookName, authorName, availabilityStatus);
		Book y = null;
		Book x = this.root;
		while (x != null) {
			y = x;
			if (node.getBookId() < x.getBookId()) {
				x = x.left;
			} else {
				x = x.right;
			}
		}

		node.parent = y;
		if (y == null) {
			root = node;
		} else if (node.getBookId() < y.getBookId()) {
			y.left = node;
		} else {
			y.right = node;
		}
		
		if (node.parent == null) {
			node.color = 0;
			dictionary.put(node.getBookId(), 0);
			return;
		}
		dictionary.put(node.getBookId(), 1);
		if (node.parent.parent == null) {
			return;
		}
		//fix the violations if there is any
		ficViolationOfInsert(node);
		//update the color flip count
		this.updateColorFlipCount();
	}
	
	//This method will update the color flip count once the delete or insert operation is performed, here checking only the nodes which might have changes the color
	public void updateColorFlipCount() {
		if(!this.colorChanged.isEmpty()) {
			for(Integer key : this.colorChanged.keySet()) {
				if(this.colorChanged.get(key) != this.dictionary.get(key)) {
					this.colorFlipCount++;
					this.dictionary.put(key, this.colorChanged.get(key));
				}
			}
			this.colorChanged.clear();
		}
	}

	public Book getRoot() {
		return this.root;
	}
	
	/**
	 * Processes a book return request by a patron and updates the library records accordingly.
	 *
	 * This method handles the return of a book by a patron, checking if the book was borrowed
	 * by the patron and, if so, updating the book's status. If the book has reservations, it
	 * assigns the book to the patron with the highest priority from the reservation list. The
	 * method communicates the outcome of the return request to the provided OutputStream using
	 * the provided Utility instance.
	 *
	 * @param patronId       The ID of the patron returning the book.
	 * @param bookId         The ID of the book to be returned.
	 * @param access         The output stream to write the result message.
	 * @param utility        An instance of the Utility class for book and reservation management.
	 * @throws IOException   If an I/O error occurs during writing to the output stream.
	 */
	public void returnBook(int patronId, int bookId, OutputStream access, Utility utility) throws IOException {
		//first get the book from the library
		Book book = this.getBookFromLibrary(this.root, bookId);
		//if the borrowed book doesn't match with the patron's Id then patron never borrowed the book to return
		if(book == null || book.getBorrowedBy() != patronId) {
			utility.write("Patron " + patronId +" never borrowed the book "+ bookId, access);
			return;
		}
		utility.write("Book " + bookId + " Returned by Patron "+ patronId, access);
		//once the book is returned by the patron, assign the book to the patron with highest priority in the reservation list
		List<Reservation> reservationHeap = book.getReservationHeap();
		if(reservationHeap != null && !reservationHeap.isEmpty()) {
			int newBorrower = reservationHeap.get(0).getPatronId();
			book.setBorrowedBy(newBorrower);
			utility.addNewLine(access);
			utility.write("Book " + bookId + " Allotted to Patron "+ newBorrower, access);
			reservationHeap.set(0, reservationHeap.get(reservationHeap.size()-1));
			reservationHeap.remove(reservationHeap.size()-1);
			//once the book is assigned to the patron from the reservation list, fix the heap to maintain the order of reservation heap
			heapfy(reservationHeap, reservationHeap.size(), 0);
		} else {
			//if reservation list is empty then set the availability status of the book to Yes
			book.setAvailabilityStatus("Yes");
		}	
	}
	
	/**
	 * Processes a book borrowing request for a patron and updates the library records accordingly.
	 *
	 * This method handles the borrowing of a book by a patron, considering various scenarios
	 * such as the book being available, already borrowed, reserved, or if the reservation limit
	 * has been reached. It communicates the outcome of the borrowing request to the provided
	 * OutputStream using the provided Utility instance.
	 *
	 * @param patronId       The ID of the patron requesting to borrow the book.
	 * @param bookId         The ID of the book to be borrowed.
	 * @param patronPriority The priority level of the patron making the request.
	 * @param access         The output stream to write the result message.
	 * @param utility        An instance of the Utility class for book and reservation management.
	 * @throws IOException   If an I/O error occurs during writing to the output stream.
	 */
	public void borrowBook(int patronId, int bookId, int patronPriority, OutputStream access, Utility utility) throws IOException {
		//search the book from the library
		Book book = this.getBookFromLibrary(this.root, bookId);
		//if book not found then write not found message to the file
		if(book == null) {
			utility.write("Book "+bookId+" not found in the Library", access);
			return;
		}
		
		//if the book is found in the library and no one borrowed it, then assign the book to patron and change its availability status to Not available
		if(book.getAvailabilityStatus().toLowerCase().equals("yes")) {
			book.setBorrowedBy(patronId);
			book.setAvailabilityStatus("No");
			utility.write("Book " + bookId + " Borrowed by Patron " + patronId, access);
			return;
		}
		
		//if book is already borrowed by patron
		if(book.getBorrowedBy() == patronId) {
			utility.write("Book " + bookId + " already Borrowed by Patron " + patronId, access);
			return;	
		}
		
		//if patron is already in the reservation list
		List<Reservation> reservationHeap = book.getReservationHeap();
		if(utility.getPatronIds(reservationHeap).contains(patronId)) {
			utility.write("Book " + bookId + " already Reserved by Patron " + patronId, access);
			return;
		}
		
		//only 20 patrons can request particular book
		if(reservationHeap.size() >= 20) {
			utility.write("Reservations for book " + bookId + " is full", access);
			return;
		}
		//create the reservation for the patron
		Reservation reservation = new Reservation(patronId, patronPriority, utility.getTimestampForReservation());
		//insert the patron into reservation heap
		List<Reservation> reverations = this.insertIntoReservationHeap(reservation, reservationHeap);
		book.setReservationHeap(reverations);
		utility.write("Book " + bookId + " Reserved by Patron " + patronId, access);
	} 
	
	/**
	 * Inserts a reservation into a min-heap of reservations and maintains the min-heap property.
	 *
	 * This method adds the specified reservation to the end of the reservation heap and
	 * ensures that the min-heap property is maintained by comparing the reservation's
	 * priority and time of reservation with its parent. If necessary, it swaps elements
	 * and continues the process until the heap property is satisfied.
	 *
	 * @param reservation      The reservation to be inserted into the heap.
	 * @param reservationHeap  The list representing the min-heap of reservations.
	 * @return                 The updated reservation heap after inserting the new reservation.
	 */
    public List<Reservation> insertIntoReservationHeap(Reservation reservation, List<Reservation> reservationHeap) {
    	reservationHeap.add(reservation);
        int currentIndex = reservationHeap.size() - 1;
        while (currentIndex > 0) {
           int parentIndex = (currentIndex - 1) / 2;
           if (compare(reservation, reservationHeap.get(parentIndex)) >= 0) {
                break;
           }
           reservationHeap.set(currentIndex, reservationHeap.get(parentIndex));
           reservationHeap.set(parentIndex, reservation);
           currentIndex = parentIndex;
        }
        return reservationHeap;
    }
    
    //compare the two patrons based on priority and if priority is same then compare the time at which reservation made for patrons
	private int compare(Reservation child, Reservation parent) {
        if (child.getPriorityNumber() < parent.getPriorityNumber()) {
            return -1;
        } else if (child.getPriorityNumber() > parent.getPriorityNumber()) {
            return 1;
        } else {
            return Long.compare(child.getTimeOfReservation(), parent.getTimeOfReservation());
        }
    }
	
	/**
	 * Maintains the min-heap property in a list of reservations starting from a specified index.
	 *
	 * This private helper method is crucial for heap operations, ensuring that the min-heap
	 * property is preserved in a list of reservations. The method compares the priority and
	 * time of reservation of the current node with its left and right child nodes, identifying
	 * the smallest among the three. If the smallest node is not the current node, it swaps the
	 * elements and recursively calls itself on the affected child.
	 *
	 * @param reservationHeap  The list representing the min-heap of reservations.
	 * @param length           The current size of the heap.
	 * @param i                The index of the current node in the heap.
	 */
	private void heapfy(List<Reservation> reservationHeap, int length, int i) {
		int smallest = i;
		//to get the left child index
		int left = (2*i)+1;
		//to get the right child index
		int right = (2*i)+2;
		if(left < length) {
			if((reservationHeap.get(left).getPriorityNumber() == reservationHeap.get(smallest).getPriorityNumber()
					&& reservationHeap.get(left).getTimeOfReservation() < reservationHeap.get(smallest).getTimeOfReservation())
					|| reservationHeap.get(left).getPriorityNumber() < reservationHeap.get(smallest).getPriorityNumber()) {
				smallest = left;
			}
		}
		
		if(right < length) {
			if((reservationHeap.get(right).getPriorityNumber() == reservationHeap.get(smallest).getPriorityNumber()
					&& reservationHeap.get(right).getTimeOfReservation() < reservationHeap.get(smallest).getTimeOfReservation()) 
					|| reservationHeap.get(right).getPriorityNumber() < reservationHeap.get(smallest).getPriorityNumber()) {
				smallest = right;
			} 
		}
		//if one of the child is smaller than parent then swap and do it recursively
		if(smallest != i) {
			Reservation temp = reservationHeap.get(smallest);
			reservationHeap.set(smallest, reservationHeap.get(i));
			reservationHeap.set(i, temp);
			heapfy(reservationHeap, length, smallest);
		}
	}
	
	/**
	 * Finds the closest books to a given book ID in a binary tree and prints the details.
	 *
	 * This method navigates a binary tree of books to find the closest books to the provided
	 * book ID. It populates a TreeMap with the book details and prints the results using the
	 * provided Utility and OutputStream instances.
	 *
	 * @param bookId   The ID of the target book.
	 * @param utility  An instance of the Utility class for book printing.
	 * @param access   The output stream to print the book details.
	 * @throws IOException If an I/O error occurs during printing.
	 */
	public void findClosestBooks(int bookId, Utility utility, OutputStream access) throws IOException {
        Book currentNode = root;
        int closest = root.getBookId();

        while (currentNode != null) {
            if (bookId == currentNode.getBookId()) {
            	this.treeMap.clear();
                this.treeMap.put(currentNode.getBookId() ,currentNode);
                break;
            }
            //if there is a exact match then will print the book details
            if (Math.abs(bookId - currentNode.getBookId()) < Math.abs(bookId - closest)) {
                closest = currentNode.getBookId();
                this.treeMap.clear(); 
                this.treeMap.put(currentNode.getBookId(), currentNode);
            } else if (Math.abs(bookId - currentNode.getBookId()) == Math.abs(bookId - closest)) {
            	//if there is two books closest then will print two books
            	this.treeMap.put(currentNode.getBookId() ,currentNode);
            }

            if (bookId < currentNode.getBookId()) {
                currentNode = currentNode.left;
            } else {
                currentNode = currentNode.right;
            }
        }
        this.printTheBooksInOrder(utility, access);
    }
	
	
	//this method will print the books into the file in order has we are using tree map
	public void printTheBooksInOrder(Utility utility, OutputStream access) throws IOException {
		if(this.treeMap != null && !this.treeMap.isEmpty()) {
			int count = 0;
			for(Entry<Integer, Book> entry : this.treeMap.entrySet()) {
				count++;
				utility.writeTheBookData(entry.getValue(), access);
				if(treeMap.size() != count) {
					utility.addNewLine(access);
				}
			}
		}
		//once the data is written to the file then clear the map
		this.treeMap.clear();
	}
	
	//this method wil print the books in the range
	public void printBooks(int start, int end, Utility utility, OutputStream access) throws IOException {
		this.getBooksInRange(this.root, start, end);
		if(this.treeMap.isEmpty() || this.treeMap == null) {
			utility.write("No books found in library in the range of " +start +" to " + end, access);
			return;
		}
		this.printTheBooksInOrder(utility, access);
	}
	
	//this method will get the books in the the range x and y
	public void getBooksInRange(Book book, int start, int end) {
		if(book == null) {
			return;
		}
		if(book.getBookId() >= start && book.getBookId() <= end) {
			this.treeMap.put(book.getBookId(), book);
		}
		getBooksInRange(book.left, start, end);
		getBooksInRange(book.right, start, end);
		return;
	}
	
	
	/**
	 * Deletes a book from the library's Red-Black Tree and performs necessary operations to maintain
	 * Red-Black Tree properties.
	 *
	 * This method handles the deletion of a book node from the Red-Black Tree. It considers various
	 * cases, such as the node being a leaf, having one child, or having two children. If the node
	 * being deleted is a leaf, it checks its color and fixes violations if needed. If the node has
	 * one child, it handles the deletion accordingly. If the node has two children, it finds the
	 * predecessor, copies its data, and recursively deletes the predecessor. The method ensures that
	 * Red-Black Tree properties are maintained throughout the deletion process.
	 *
	 * @param book The book node to be deleted from the Red-Black Tree.
	 */
	public void deleteBook(Book book) {
		if(this.root == book && book.left == null && book.right == null) {
			this.root = null;
			return;
		}
		
		//if both children's are not null then find the predecessor and do the recursively
		Book x = null;
		if(book.right != null && book.left != null) {
			x = findPredcessor(book);
			copyPredecessorData(x, book);
			deleteBook(x);
			return;
		}
		
		//if both the children's are null then its leaf node
		if(book.right == null && book.left == null) {
			if(book.color == 1) {
				//Node the leaf and the color of node is red 
				if(book.parent != null) {
					if(book.parent.right == book) {
						book.parent.right = null;
					} else {
						book.parent.left = null;
					}
				}
				return;
			} else {
				//The node the leaf and it is black will call delete method and fix the violations if required
				this.fixDelete(book);
				if(book.parent != null) {
					if(book.parent.right == book) {
						book.parent.right = null;
					} else {
						book.parent.left = null;
					}
				}
				return;
			}
		}
		
		//this case will handle if the node that is being deleted has only one child
		if(book.right == null || book.left == null) {
			Book y;
			if(book.left != null) {
				y = book.left;
			} else {
				y = book.right;
			}
			if(book.color == 1) {
				//The node with one child is red so directly delete it
				if(book.parent != null) {
					if(book.parent.left == book) {
						book.parent.left = y;
					} else {
						book.parent.right = y;
					}
					if(y != null) {
						y.parent = book.parent;
					}
				} else {
					this.root = y;
					if(y != null) {
						y.parent = null;
					}
				}
				return;
			} else {
				//the node which need to be deleted has one child and color is black we need to fix the violations because 
				//the number of black nodes in this path will be less
				if(book.parent != null) {
					if(book.parent.left == book) {
						book.parent.left = y;
					} else {
						book.parent.right = y;
					}
					if(y != null) {
						y.parent = book.parent;
					}
				} else {
					this.root = y;
					if(y != null) {
						y.parent = null;
					}
				}
				if(y != null && y.color == 1) {
					y.color =0;
					this.colorChanged.put(y.getBookId(), 0);
					return;
				} else {
					this.fixDelete(y);
				}
				return;
			}
		}
	}

	//the method will copy the data from the Predecessor 
	private void copyPredecessorData(Book x, Book book) {
		book.setBookName(x.getBookName());
		book.setAuthorName(x.getAuthorName());
		book.setAvailabilityStatus(x.getAvailabilityStatus());
		book.setBookId(x.getBookId());
		book.setBorrowedBy(x.getBorrowedBy());
		book.setReservationHeap(x.getReservationHeap());
		this.colorChanged.put(book.getBookId(), book.color);
	}

	//this method will find the predecessor of the node which being passed
	private Book findPredcessor(Book book) {
		Book pred = book.left;
		while(pred.right != null) {
			pred = pred.right;
		}
		return pred;
	}
	
	public void fixDelete(Book book) {
		//if the book is null or if the book is same as root will return
		if(book == null || book == this.root) {
			return;
		}
		//check if the node has sibling and its color is black
		if(getSibling(book).color == 0) {
			//get red count of the sibling
			if(getRedCount(getSibling(book)) > 0) {
				if(getRedCount(getSibling(book)) == 1) {
					if(book.parent.right == book) {
						// Check if the left sibling of the current node (book) exists and is red
						if(getSibling(book).left != null && getSibling(book).left.color == 1) {
							// Check if the parent of the current node (book) is red
							if(book.parent.color == 1) {
								// Adjust colors for the red sibling and parent, then perform a left rotation
								getSibling(book).color = 1;
								this.colorChanged.put(getSibling(book).getBookId(), 1);
								book.parent.color = 0;
								this.colorChanged.put(book.parent.getBookId(), 0);
							}
							 // Set the color of the left child of the sibling to black
							getSibling(book).left.color = 0;
							this.colorChanged.put(getSibling(book).left.getBookId(), 0);
							// Perform a left rotation on the left child of the sibling
							rotateLeft(getSibling(book).left);
							return;
						} else {
							// The left sibling's left child is not red
						    // Check if the parent of the current node (book) is red
							if(book.parent.color == 1) {
								book.parent.color = 0;
								this.colorChanged.put(book.parent.getBookId(), 0);
							} else {
								getSibling(book).right.color = 0;
								this.colorChanged.put(getSibling(book).right.getBookId(), 0);
							}
							// Perform a left-right rotation on the right child of the sibling
							this.rotateLeftRight(getSibling(book).right);
							return;
						}
					} else {
						// Check if the parent of the current node (book) is red
						if(getSibling(book).right != null && getSibling(book).right.color == 1) {
							// Adjust colors for the red sibling and parent, then perform a right rotation
							if(book.parent.color == 1) {
								getSibling(book).color = 1;
								this.colorChanged.put(getSibling(book).getBookId(), 1);
								book.parent.color = 0;
								this.colorChanged.put(book.parent.getBookId(), 0);
							}
							getSibling(book).right.color = 0;
							this.colorChanged.put(getSibling(book).right.getBookId(), 0);
							// Perform a right rotation on the right child of the sibling
							this.rotateRight(getSibling(book).right);
							return;
						} else {
							// The right sibling's right child is not red
						    // Check if the parent of the current node (book) is red
							if(book.parent.color == 1) {
								book.parent.color = 0;
								this.colorChanged.put(book.parent.getBookId(), 0);
							} else {
								getSibling(book).left.color = 0;
								this.colorChanged.put(getSibling(book).left.getBookId(), 0);
							}
							// Perform a right-left rotation on the left child of the sibling
							rotateRightLeft(getSibling(book).left);
							return;
						}
					}
				} else {
					// Check if the current node (book) is the right child of its parent
					if(book.parent.right == book) {
						 // Current node is the right child
					    // Check if the parent of the current node is red
						if(book.parent.color == 1) {
							// Adjust the color of the parent to black
							book.parent.color =0;
							this.colorChanged.put(book.parent.getBookId(), 0);
						} else {
							getSibling(book).right.color = 0;
							this.colorChanged.put(getSibling(book).right.getBookId(), 0);
						}
						// Perform a left-right rotation on the right child of the sibling
						this.rotateLeftRight(getSibling(book).right);
						return;
					} else {
						// Current node is the left child
					    // Check if the parent of the current node is red
						if(book.parent.color == 1) {
							book.parent.color = 0;
							this.colorChanged.put(book.parent.getBookId(), 0);
						} else {
							// Adjust the color of the left child of the sibling to black
							getSibling(book).left.color = 0;
							this.colorChanged.put(getSibling(book).left.getBookId(), 0);
						}
						// Perform a right-left rotation on the left child of the sibling
						this.rotateRightLeft(getSibling(book).left);
						return;
					}
				}
			} else {
				// Check if the parent of the current node (book) is red
				if(book.parent.color == 1) {
					// Parent is red
				    // Adjust the color of the parent to black
					book.parent.color = 0;
					this.colorChanged.put(book.parent.getBookId(), 0);
					getSibling(book).color = 1;
					this.colorChanged.put(getSibling(book).getBookId(), 1);
					return;
				} else {
					// Parent is black
				    // Adjust the color of the sibling to red
					getSibling(book).color = 1;
					this.colorChanged.put(getSibling(book).getBookId(), 1);
					// Recursively fix the tree upwards by calling the fixDelete method on the parent
					this.fixDelete(book.parent);
					return;
				}
			}
		} else {
			if(book.parent.right == book) {
				// Check if the right child of the sibling is null or has no red nodes in its subtree
				if(getSibling(book).right == null || (getSibling(book).right != null && this.getRedCount(getSibling(book).right) == 0)) {
					 // Case 1: Right child of the sibling is null or has no red nodes
				    // Set the color of the sibling to black
					getSibling(book).color = 0;
					this.colorChanged.put(getSibling(book).getBookId(), 0);
					if(getSibling(book).right != null) {
						// Set the color of the right child of the sibling to red
						getSibling(book).right.color = 1;
						this.colorChanged.put(getSibling(book).right.getBookId(), 1);
					}
					// Perform a left rotation on the left child of the sibling
					this.rotateLeft(getSibling(book).left);
					return;
				} else if(getSibling(book).right != null && getRedCount(getSibling(book).right) == 1) {
				    // Case 2: Right child of the sibling has exactly one red node
					if(getSibling(book).right.left != null && getSibling(book).right.left.color == 1) {
						// Right child's left child is red
				        // Adjust the color of the right child's left child to black
						getSibling(book).right.left.color = 0;
						this.colorChanged.put(getSibling(book).right.left.getBookId(), 0);
				        // Perform a left-right rotation on the right child of the sibling
						this.rotateLeftRight(getSibling(book).right);
						return;
					} else {
						// Right child's left child is not red
				        // Adjust the color of the right child's right child to black
						getSibling(book).right.right.color = 0;
						this.colorChanged.put(getSibling(book).right.right.getBookId(), 0);
				        // Perform a right rotation on the right child's right child of the sibling
						this.rotateRight(getSibling(book).right.right);
				        // Perform a left-right rotation on the right child of the sibling
						this.rotateLeftRight(getSibling(book).right);
				        // Update the tree structure to maintain the Red-Black Tree properties
						Book grandParent = book.parent.parent;
						Book grandChild = grandParent.left.left;
						Book child = grandParent.left;
						child.parent = grandChild;
						child.left = grandChild.right;
				        // Update the parent reference for the right child of grandChild
						if(grandChild.right != null) {
							grandChild.right.parent = child;
						}
						grandChild.right = child;
						grandChild.parent = grandParent;
				        // Update the parent's reference to grandChild
						grandParent.left = grandChild;
						return;
					}
				} else if(getSibling(book).right != null && getRedCount(getSibling(book).right) == 2) {
					// Right child of the sibling has exactly two red nodes
				    // Adjust the color of the right child's right child to black
					getSibling(book).right.right.color = 0;
					this.colorChanged.put(getSibling(book).right.right.getBookId(), 0);
				    // Perform a right rotation on the right child's right child of the sibling
					this.rotateRight(getSibling(book).right.right);
				    // Perform a left-right rotation on the right child of the sibling
					this.rotateLeftRight(getSibling(book).right);
				    // Update the tree structure to maintain the Red-Black Tree properties
					Book grandParent = book.parent.parent;
					Book grandChild = grandParent.left.left;
					Book child = grandParent.left;
					child.parent = grandChild;
					child.left = grandChild.right;
				    // Update the parent reference for the right child of grandChild
					if(grandChild.right != null) {
						grandChild.right.parent = child;
					}
				    // Update references for grandChild
					grandChild.right = child;
					grandChild.parent = grandParent;
					grandParent.left = grandChild;
					return;
				} else {
				    // Swap the colors of the parent and the sibling
					int parentColor = book.parent.color;
					book.parent.color = getSibling(book).color;
					this.colorChanged.put(book.parent.getBookId(), getSibling(book).color);
					getSibling(book).color = parentColor;
					this.colorChanged.put(getSibling(book).getBookId(), parentColor);
				    // Store a reference to the sibling for further operations
					Book sibling = getSibling(book);
				    // Update the root if the parent of the current node is the root
					if(book.parent == this.root) {
						this.root = sibling;
					}
				    // Adjust the parent's left child to be the right child of the sibling
					book.parent.left = sibling.right;
				    // Update the parent reference for the right child of the sibling
					if(sibling.right != null) {
						sibling.right.parent = book.parent;
					}
				    // Update parent and sibling references
					sibling.parent = book.parent;
					book.parent = sibling;
				    // Update the parent's reference to the sibling
					if(sibling.parent != null) {
						if(sibling.parent.right == book.parent) {
							sibling.parent.right = sibling;
						} else {
							sibling.parent.left = sibling;
						}
					}
				    // Recursively fix the tree structure upwards
					this.fixDelete(book);
					return;
				}
			} else {
				// Check if the left child of the sibling is null or has no red nodes in its subtree
				if (getSibling(book).left == null || ( getSibling(book).left != null && this.getRedCount(getSibling(book).left) == 0)) {
					// Left child of the sibling is null or has no red nodes
				    // Set the color of the sibling to black
					 getSibling(book).color = 0;
					 this.colorChanged.put(getSibling(book).getBookId(), 0);
					 // Check if the left child of the sibling is not null
					 if(getSibling(book).left != null) {
					     // Set the color of the left child of the sibling to red
						 getSibling(book).left.color = 1;
						 this.colorChanged.put(getSibling(book).left.getBookId(), 1);
					 }
					  // Perform a right rotation on the right child of the sibling
					 this.rotateRight(getSibling(book).right);
					 return;
					 
				} else if (getSibling(book).left != null && this.getRedCount(getSibling(book).left) == 1) {
				    // Left child of the sibling has exactly one red node
					if(getSibling(book).left.right != null && getSibling(book).left.right.color == 1) {
						// Left child's right child is red
				        // Adjust the color of the left child's right child to black
						getSibling(book).left.right.color = 0;
						this.colorChanged.put(getSibling(book).left.right.getBookId(), 0);
				        // Perform a right-left rotation on the left child of the sibling
						this.rotateRightLeft(getSibling(book).left);
						return;
					} else {
						// Left child's right child is not red
				        // Adjust the color of the left child's left child to black
						getSibling(book).left.left.color = 0;
						this.colorChanged.put(getSibling(book).left.left.getBookId(), 0);
				        // Perform a left rotation on the left child's left child of the sibling
						this.rotateLeft(getSibling(book).left.left);
				        // Perform a right-left rotation on the left child of the sibling
						this.rotateRightLeft(getSibling(book).left);
				        // Update the tree structure to maintain the Red-Black Tree properties
						Book grandParent = book.parent.parent;
						Book grandChild = grandParent.right.right;
						Book child = grandParent.right;
						child.parent = grandChild;
						child.right = grandChild.left;
				        // Update the parent reference for the left child of grandChild
						if(grandChild.left != null) {
							grandChild.left.parent = child;
						}
						grandChild.left = child;
						grandChild.parent = grandParent;
				        // Update the parent's reference to grandChild
						grandParent.right = grandChild;
			            return;
					}
				} else if(getSibling(book).left != null && this.getRedCount(getSibling(book).left) == 2) {
					 //Left child of the sibling has exactly two red nodes
					 // Adjust the color of the left child's left child to black
					getSibling(book).left.left.color = 0;
					this.colorChanged.put(getSibling(book).left.left.getBookId(), 0);
				    // Perform a left rotation on the left child's left child of the sibling
					this.rotateLeft(getSibling(book).left.left);
				    // Perform a right-left rotation on the left child of the sibling
					this.rotateRightLeft(getSibling(book).left);
				    // Update the tree structure to maintain the Red-Black Tree properties
					Book grandParent = book.parent.parent;
					Book grandChild = grandParent.right.right;
					Book child = grandParent.right;
					child.parent = grandChild;
					child.right = grandChild.left;
				    // Update the parent reference for the left child of grandChild
					if(grandChild.left != null) {
						grandChild.left.parent = child;
					}
				    // Update references for grandChild
					grandChild.left = child;
					grandChild.parent = grandParent;
				    // Update the parent's reference to grandChild
					grandParent.right = grandChild;
		            return;
				} else {
					// Swap the colors of the parent and the sibling
					int parentColor = book.parent.color;
					book.parent.color = getSibling(book).color;
					this.colorChanged.put(book.parent.getBookId(), getSibling(book).color);
					getSibling(book).color = parentColor;
					this.colorChanged.put(getSibling(book).getBookId(), parentColor);
					// Store a reference to the sibling for further operations
					Book sibling = getSibling(book);
					// Update the root if the parent of the current node is the root
					if(book.parent == this.root) {
						this.root = sibling;
					}
					// Adjust the parent's left child to be the right child of the sibling
					book.parent.left = sibling.right;
					// Update the parent reference for the right child of the sibling
					if(sibling.right != null) {
						sibling.right.parent = book.parent;
					}
					// Update parent and sibling references
					sibling.parent = book.parent;
					book.parent = sibling;
					// Update the parent's reference to the sibling
					if(sibling.parent != null) {
						if(sibling.parent.right == book.parent) {
							sibling.parent.right = sibling;
						} else {
							sibling.parent.left = sibling;
						}
					}
					// Recursively fix the tree structure upwards
					this.fixDelete(book);
					return;
				}
			}
		}
	}
	
	// Rotate the given book node to the left
	private Book rotateLeft(Book book) {
	    // Store references to the grandparent and the new parent (book's parent) of the rotated subtree
		Book fNode = book.parent.parent;
	    // Update the parent of the rotated subtree to the grandparent of the original parent
		book.parent.parent = fNode.parent;
		
		fNode.left = book.parent.right;
	    // Update the parent reference for the new left child of the grandparent
		if(book.parent.right != null) {
			book.parent.right.parent = fNode;
		}
	    // Update the right child of the new parent to be the grandparent
		book.parent.right = fNode;
	    // Update the parent reference for the grandparent
		fNode.parent = book.parent;
	    // Check if the grandparent has a parent and update its reference to the new parent
		if(book.parent.parent != null && book.parent.parent.getBookId() < book.parent.getBookId()) {
			book.parent.parent.right = book.parent;
		} else if(book.parent.parent != null) {
			book.parent.parent.left = book.parent; 
		}
	    // Update the root if the original grandparent was the root
		if(fNode == this.root) {
			this.root = book.parent;
		}
	    // Return the new parent (book's parent) after the rotation
		return book.parent;
	}
	
	// Rotate the given lastNode to the right
	private Book rotateRight(Book lastNode) {
	    // Store references to the grandparent and the new parent (lastNode's parent) of the rotated subtree
		Book fNode = lastNode.parent.parent;
		lastNode.parent.parent = fNode.parent;
		
		fNode.right = lastNode.parent.left;
	    // Update the parent reference for the new right child of the grandparent
		if(lastNode.parent.left != null) {
			lastNode.parent.left.parent = fNode;
		}
	    // Update the left child of the new parent to be the grandparent
		lastNode.parent.left = fNode;
		fNode.parent = lastNode.parent;
		
	    // Check if the grandparent has a parent and update its reference to the new parent
		if(lastNode.parent.parent != null && lastNode.parent.parent.getBookId() < lastNode.parent.getBookId()) {
			lastNode.parent.parent.right = lastNode.parent;
		} else if(lastNode.parent.parent != null) {
			lastNode.parent.parent.left = lastNode.parent; 
		}
		if(fNode == this.root) {
			this.root = lastNode.parent;
		}
	    // Return the new parent (lastNode's parent) after the rotation
		return lastNode.parent;
	}
	
	// Perform a left-right rotation on the given lastNode
	private Book rotateLeftRight(Book lastNode) {
	    // Store references to the grandparent and the new parent (lastNode's parent) of the rotated subtree
		Book fNode = lastNode.parent.parent;
	    // Update the left child of the grandparent to be the right child of lastNode
		fNode.left = lastNode.right;
	    // Update the parent reference for the new left child of the grandparent
		 if(lastNode.right != null) {
			 lastNode.right.parent = fNode;
		 }
		 lastNode.right = fNode;
		    // Update the right child of lastNode's parent to be the left child of lastNode
		 lastNode.parent.right = lastNode.left;
		 
		 
		 if(lastNode.left != null) {
			 lastNode.left.parent = lastNode.parent;
		 }
		    // Update the left child of lastNode to be lastNode's parent
		 lastNode.left = lastNode.parent;
		 lastNode.parent = fNode.parent;
		 fNode.parent = lastNode;
		 lastNode.left.parent = lastNode;
		 
		    // Check if lastNode has a parent and update its reference to lastNode
		 if(lastNode.parent != null && lastNode.parent.getBookId() < lastNode.getBookId()) {
			 lastNode.parent.right = lastNode;
		 } else if(lastNode.parent != null) {
			 lastNode.parent.left = lastNode;
		 }
		    // Update the root if the original grandparent was the root
		 if(fNode == this.root) {
			 this.root = lastNode;
		 }
		    // Return lastNode after the left-right rotation
		 return lastNode;
	}
	
	// Perform a right-left rotation on the given node
	private Book rotateRightLeft(Book node) {
	    // Store references to the grandparent and the new parent (node's parent) of the rotated subtree
		Book fNode = node.parent.parent;
	    // Update the right child of the grandparent to be the left child of node
		fNode.right = node.left;
	    // Update the parent reference for the new right child of the grandparent
		 if(node.left != null) {
			 node.left.parent = fNode;
		 }
		 node.left = fNode;
		 node.parent.left = node.right;
		 
		    // Update the parent reference for the new left child of node's parent
		 if(node.right != null) {
			 node.right.parent = node.parent;
		 }
		 
		 node.parent.parent = node;
		 node.right = node.parent;
		 node.parent = fNode.parent;
		    // Update the parent reference for the right child of node
		 fNode.parent = node;
		    // Check if node has a parent and update its reference to node
		 if(node.parent != null && node.parent.getBookId() < node.getBookId()) {
			 node.parent.right = node;
		 } else if(node.parent != null) {
			 node.parent.left = node;
		 }
		    // Update the root if the original grandparent was the root
		 if(fNode == this.root) {
			 this.root = node;
		 }
		    // Return node after the right-left rotation
		 return node;
	}

	//return the redCount for the given node
	public int getRedCount(Book book) {
		int redCount = 0;
		if(book != null) {
			if(book.left != null && book.left.color == 1) {
				redCount = redCount + 1;
			} 
			if(book.right != null && book.right.color == 1){
				redCount = redCount + 1;
			}
		}
		return redCount;
	}
	
	//this method returns the given node's sibling
	public Book getSibling(Book node) {
		if(node.parent != null) {
			if(node.parent.left == node) {
				return node.parent.right;
			}
			return node.parent.left;
		}
		return null;
	}     
}