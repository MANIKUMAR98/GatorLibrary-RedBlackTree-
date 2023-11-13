
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class RedBlackTree {
	
	private Book root;
	public Book externalNode;
	int colorFlipCount = 0;
	
	Map<Integer, Book> treeMap = new TreeMap<>();
	
	// Preorder
//	private void preOrderHelper(Book node) {
//		if (node != externalNode) {
//			System.out.print(node.getBookId() + " ");
//			preOrderHelper(node.left);
//			preOrderHelper(node.right);
//		}
//	}

	// Inorder
//	private void inOrderHelper(Book node) {
//		if (node != externalNode) {
//			inOrderHelper(node.left);
//			System.out.print(node.getBookId() + " ");
//			inOrderHelper(node.right);
//		}
//	}

//	// Post order
//	private void postOrderHelper(Book node) {
//		if (node != externalNode) {
//			postOrderHelper(node.left);
//			postOrderHelper(node.right);
//			System.out.print(node.getBookId() + " ");
//		}
//	}

	// Search the tree
	private Book getBook(Book node, int value) {
		if (node == externalNode || value == node.getBookId()) {
			return node;
		}

		if (value < node.getBookId()) {
			return getBook(node.left, value);
		}
		return getBook(node.right, value);
	}

	// Balance the tree after deletion of a node
	private void fixDelete(Book x) {
		Book s;
		while (x != root && x.color == 0) {
			if (x == x.parent.left) {
				s = x.parent.right;
				if (s.color == 1) {
					s.color = 0;
					colorFlipCount++;
					if(x.parent.color == 0) {
						colorFlipCount++;
					}
					x.parent.color = 1;
					leftRotate(x.parent);
					s = x.parent.right;
				}

				if (s.left.color == 0 && s.right.color == 0) {
					if(s.color == 0) {
						colorFlipCount++;
					}
					s.color = 1;
					x = x.parent;
				} else {
					if (s.right.color == 0) {
						if(s.left.color == 1) {
							colorFlipCount++;
						}
						s.left.color = 0;
						if(s.color == 0) {
							colorFlipCount++;
						}
						s.color = 1;
						rightRotate(s);
						s = x.parent.right;
					}

					s.color = x.parent.color;
					if(x.parent.color == 1) {
						colorFlipCount++;
					}
					x.parent.color = 0;
					if(s.right.color == 1) {
						colorFlipCount++;
					}
					s.right.color = 0;
					leftRotate(x.parent);
					x = root;
				}
			} else {
				s = x.parent.left;
				if (s.color == 1) {
					s.color = 0;
					colorFlipCount++;
					if(x.parent.color == 0) {
						colorFlipCount++;
					}
					x.parent.color = 1;
					rightRotate(x.parent);
					s = x.parent.left;
				}

				if (s.right.color == 0 && s.right.color == 0) {
					if(s.color == 0) {
						colorFlipCount++;
					}
					s.color = 1;
					x = x.parent;
				} else {
					if (s.left.color == 0) {
						if(s.right.color == 1) {
							colorFlipCount++;
						}
						s.right.color = 0;
						if(s.color == 0) {
							colorFlipCount++;
						}
						s.color = 1;
						leftRotate(s);
						s = x.parent.left;
					}
					if(s.color != x.parent.color) {
						colorFlipCount++;
					}
					s.color = x.parent.color;
					if(x.parent.color == 1) {
						colorFlipCount++;
					}
					x.parent.color = 0;
					if(x.left.color == 1) {
						colorFlipCount++;
					}
					s.left.color = 0;
					rightRotate(x.parent);
					x = root;
				}
			}
		}
//		if(x.color == 1) {
//			colorFlipCount++;
//		}
		x.color = 0;
	}

	private void rbTransplant(Book u, Book v) {
		if (u.parent == null) {
			root = v;
		} else if (u == u.parent.left) {
			u.parent.left = v;
		} else {
			u.parent.right = v;
		}
		v.parent = u.parent;
	}

	public void deleteBook(int bookId, DataOutputStream access, Utility utility) throws IOException {
		Book books = this.root;
		Book z = externalNode;
		Book x, y;
		while (books != externalNode) {
			if (books.getBookId() == bookId) {
				z = books;
			}

			if (books.getBookId() <= bookId) {
				books = books.right;
			} else {
				books = books.left;
			}
		}

		if (z == externalNode) {
			System.out.println("Couldn't find book in the library");
			return;
		}
		
		utility.write("Book " + bookId + " is no longer available"
				+ (z.getReservationHeap().size() == 0 ? "" : ". Reservations made by Patrons " + utility.reservationString(z.getReservationHeap()) + " have been cancelled!"), access);

		y = z;
		int yOriginalColor = y.color;
		if (z.left == externalNode) {
			x = z.right;
			rbTransplant(z, z.right);
		} else if (z.right == externalNode) {
			x = z.left;
			rbTransplant(z, z.left);
		} else {
			y = minimum(z.right);
			yOriginalColor = y.color;
			x = y.right;
			if (y.parent == z) {
				x.parent = y;
			} else {
				rbTransplant(y, y.right);
				y.right = z.right;
				y.right.parent = y;
			}

			rbTransplant(z, y);
			y.left = z.left;
			y.left.parent = y;
//			if(y.color != z.color) {
//				colorFlipCount++;
//			}
			y.color = z.color;
		}
		if (yOriginalColor == 0) {
			fixDelete(x);
		}
	}

	// Balance the node after insertion
	private void ficViolationOfInsert(Book child) {
		Book uncle;
		while (child.parent.color == 1) {
			if (child.parent == child.parent.parent.right) {
				uncle = child.parent.parent.left;
				if (uncle.color == 1) {
					colorFlipCount++;
					uncle.color = 0;
					if(child.parent.color == 1) {
						colorFlipCount++;
					}
					child.parent.color = 0;
//					if(child.parent.parent.color == 0) {
//						colorFlipCount++;
//					}
//					child.parent.parent.color = 1;
					if(child.parent.parent != root) {
						if(child.parent.parent.color == 0) {
							colorFlipCount++;
						}
						child.parent.parent.color = 1;
					}
					child = child.parent.parent;
				} else {
					if (child == child.parent.left) {
						child = child.parent;
						rightRotate(child);
					}
					if(child.parent.color == 1) {
						colorFlipCount++;
					}
					child.parent.color = 0;
//					if(child.parent.parent.color == 0) {
//						colorFlipCount++;
//					}
					if(child.parent.parent.color == 0) {
						colorFlipCount++;
					}
					child.parent.parent.color = 1;
//					child.parent.parent.color = 1;
					leftRotate(child.parent.parent);
				}
			} else {
				uncle = child.parent.parent.right;
				if (uncle.color == 1) {
					uncle.color = 0;
					colorFlipCount++;
					if(child.parent.color == 1) {
						colorFlipCount++;
					}
					child.parent.color = 0;
//					if(child.parent.parent.color == 0) {
//						colorFlipCount++;
//					}
					if(child.parent.parent != root) {
						if(child.parent.parent.color == 0) {
							colorFlipCount++;
						}
						child.parent.parent.color = 1;
					}
					child = child.parent.parent;
				} else {
					if (child == child.parent.right) {
						child = child.parent;
						leftRotate(child);
					}
					if(child.parent.color == 1) {
						colorFlipCount++;
					}
					child.parent.color = 0;
//					if(child.parent.parent.color == 0) {
//						colorFlipCount++;
//					}
					if(child.parent.parent.color == 0) {
						colorFlipCount++;
					}
					child.parent.parent.color = 1;
					rightRotate(child.parent.parent);
				}
			}
			if (child == root) {
				break;
			}
		}
		if(root.color == 1) {
			colorFlipCount++;
		}
		root.color = 0;
	}

//	private void printHelper(Book root, String indent, boolean last) {
//		if (root != externalNode) {
//			System.out.print(indent);
//			if (last) {
//				System.out.print("R----");
//				indent += "   ";
//			} else {
//				System.out.print("L----");
//				indent += "|  ";
//			}
//
//			String sColor = root.color == 1 ? "RED" : "BLACK";
//			System.out.println(root.getBookId() + "(" + sColor + ")");
//			printHelper(root.left, indent, false);
//			printHelper(root.right, indent, true);
//		}
//	}

	public RedBlackTree() {
		externalNode = new Book();
		externalNode.color = 0;
		externalNode.left = null;
		externalNode.right = null;
		root = externalNode;
	}

//	public void preorder() {
//		preOrderHelper(this.root);
//	}
//
//	public void inorder() {
//		inOrderHelper(this.root);
//	}
//
//	public void postorder() {
//		postOrderHelper(this.root);
//	}

	public Book getBookFromLibrary(int bookId) {
		return getBook(this.root, bookId);
	}

	public Book minimum(Book node) {
		while (node.left != externalNode) {
			node = node.left;
		}
		return node;
	}

	public Book maximum(Book node) {
		while (node.right != externalNode) {
			node = node.right;
		}
		return node;
	}

	public Book successor(Book x) {
		if (x.right != externalNode) {
			return minimum(x.right);
		}

		Book y = x.parent;
		while (y != externalNode && x == y.right) {
			x = y;
			y = y.parent;
		}
		return y;
	}

	public Book predecessor(Book x) {
		if (x.left != externalNode) {
			return maximum(x.left);
		}

		Book y = x.parent;
		while (y != externalNode && x == y.left) {
			x = y;
			y = y.parent;
		}

		return y;
	}

	public void leftRotate(Book x) {
		Book y = x.right;
		x.right = y.left;
		if (y.left != externalNode) {
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

	public void rightRotate(Book x) {
		Book y = x.left;
		x.left = y.right;
		if (y.right != externalNode) {
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

	public Book getBook(int bookId, String bookName, String authorName, String availabilityStatus) {
		Book book = new Book();
		book.setBookId(bookId);
		book.setAuthorName(authorName);
		book.setAvailabilityStatus(availabilityStatus);
		book.setBookName(bookName);
		book.setReservationHeap(null);
		book.setBorrowedBy(0);
		book.setReservationHeap(new ArrayList<>());
		book.parent = null;
		book.left = externalNode;
		book.right = externalNode;
		book.color = 1;
		return book;
	}

	public void insertBook(int bookId, String bookName, String authorName, String availabilityStatus) {
		Book node = this.getBook(bookId, bookName, authorName, availabilityStatus);

		Book y = null;
		Book x = this.root;

		while (x != externalNode) {
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
			this.colorFlipCount++;
			return;
		}

		if (node.parent.parent == null) {
			return;
		}

		ficViolationOfInsert(node);
	}

	public Book getRoot() {
		return this.root;
	}

//	public void printTree() {
//		printHelper(this.root, "", true);
//	}
	
	public void returnBook(int patronId, int bookId, DataOutputStream access, Utility utility) throws IOException {
		Book book = this.getBookFromLibrary(bookId);
		if(book.getBorrowedBy() != patronId) {
			utility.write("Patron " + patronId +" never borrowed book "+ bookId, access);
			System.out.println("Patron " + patronId +" never borrowed book "+ bookId);
			return;
		}
		if(book != externalNode) {
			utility.write("Book " + bookId + " returned by Patron "+ patronId, access);
			List<Reservation> reservationHeap = book.getReservationHeap();
			if(reservationHeap != null && !reservationHeap.isEmpty()) {
				int newBorrower = reservationHeap.get(0).getPatronId();
				book.setBorrowedBy(newBorrower);
				reservationHeap.set(0, reservationHeap.get(reservationHeap.size()-1));
				reservationHeap.remove(reservationHeap.size()-1);
				heapfy(reservationHeap, reservationHeap.size(), 0);
				access.writeUTF(System.lineSeparator());
				utility.write("Book " + bookId + " allotted to Patron "+ newBorrower, access);
			} else {
				book.setAvailabilityStatus("Yes");
			}
		}
	}
	
	//code for heapify
	public void borrowBook(int patronId, int bookId, int patronPriority, DataOutputStream access, Utility utility) throws IOException {
		Book book = this.getBookFromLibrary(bookId);
		if(book == externalNode) {
			utility.write("Book not found to add reservation", access);
			return;
		}
		
		if(book.getAvailabilityStatus().toLowerCase().equals("yes")) {
			book.setBorrowedBy(patronId);
			book.setAvailabilityStatus("No");
			utility.write("Book " + bookId + " borrowed by Patron " + patronId, access);
			return;
		}
		
		if(book.getBorrowedBy() == patronId) {
			utility.write("Book " + bookId + " already borrowed by Patron " + patronId, access);
			return;	
		}
		
		List<Reservation> reservationHeap = book.getReservationHeap();
		if(utility.getReservations(reservationHeap).contains(patronId)) {
			utility.write("Book " + bookId + " already reserved by Patron " + patronId, access);
			return;
		}
		
		if(reservationHeap.size() >= 20) {
			utility.write("Reservations for book " + bookId + " is full", access);
			return;
		}
		Reservation reservation = new Reservation(patronId, patronPriority, utility.getTimestampForReservation());
		List<Reservation> reverations = this.insertIntoReservationHeap(reservation, reservationHeap);
		book.setReservationHeap(reverations);
		utility.write("Book " + bookId + " reserved by Patron " + patronId, access);
	} 
	
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
    
    //compare the child and parent node based on priority
	private int compare(Reservation child, Reservation parent) {
        if (child.getPriorityNumber() < parent.getPriorityNumber()) {
            return -1;
        } else if (child.getPriorityNumber() > parent.getPriorityNumber()) {
            return 1;
        } else {
            return Long.compare(child.getTimeOfReservation(), parent.getTimeOfReservation());
        }
    }
	
	//heapify
	private void heapfy(List<Reservation> reservationHeap, int length, int i) {
		int smallest = i;
		int left = (2*i)+1;
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
		if(smallest != i) {
			Reservation temp = reservationHeap.get(smallest);
			reservationHeap.set(smallest, reservationHeap.get(i));
			reservationHeap.set(i, temp);
			heapfy(reservationHeap, length, smallest);
		}
	}
	
	//closest books
	public void findClosestBooks(int bookId, Utility utility, DataOutputStream access) throws IOException {
        Book currentNode = root;
        int closest = root.getBookId();

        while (currentNode != externalNode) {
            if (bookId == currentNode.getBookId()) {
            	this.treeMap.clear();
                this.treeMap.put(currentNode.getBookId() ,currentNode);
                break;
            }
            if (Math.abs(bookId - currentNode.getBookId()) < Math.abs(bookId - closest)) {
                closest = currentNode.getBookId();
                this.treeMap.clear(); // Clear previous closest values
                this.treeMap.put(currentNode.getBookId(), currentNode);
            } else if (Math.abs(bookId - currentNode.getBookId()) == Math.abs(bookId - closest)) {
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
	
	public void printTheBooksInOrder(Utility utility, DataOutputStream access) throws IOException {
		if(this.treeMap != null && !this.treeMap.isEmpty()) {
			int count = 0;
			for(Entry<Integer, Book> entry : this.treeMap.entrySet()) {
				count++;
				utility.writeTheBookData(entry.getValue(), access);
				if(treeMap.size() != count) {
					access.writeUTF(System.lineSeparator());
				}
			}
		}
		this.treeMap.clear();
	}
	
	public void printBooks(int start, int end, Utility utility, DataOutputStream access) throws IOException {
		this.getBooksInRange(this.root, start, end);
		if(this.treeMap.isEmpty() || this.treeMap == null) {
			utility.write("No books found in library in the range of " +start +" to " + end, access);
			return;
		}
		this.printTheBooksInOrder(utility, access);
	}
	
	//get the books in range
	public void getBooksInRange(Book book, int start, int end) {
		if(book == externalNode) {
			return;
		}
		if(book.getBookId() >= start && book.getBookId() <= end) {
			this.treeMap.put(book.getBookId(), book);
		}
		getBooksInRange(book.left, start, end);
		getBooksInRange(book.right, start, end);
		return;
	}
}