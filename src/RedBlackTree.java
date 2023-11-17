
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
	
	// Search the library to get the book
	public Book getBookFromLibrary(Book book, int value) {
		if (book == null || value == book.getBookId()) {
			return book;
		}
		if (value < book.getBookId()) {
			return getBookFromLibrary(book.left, value);
		}
		return getBookFromLibrary(book.right, value);
	}

	public void deleteBookFromLibrary(int bookId, OutputStream access, Utility utility) throws IOException {
		Book books = this.root;
		Book z = null;
		while (books != null) {
			if (books.getBookId() == bookId) {
				z = books;
				break;
			}
			if (books.getBookId() <= bookId) {
				books = books.right;
			} else {
				books = books.left;
			}
		}
		if (z == null) {
			System.out.println("Couldn't find book in the library");
			return;
		}
		utility.write("Book " + bookId + " is no longer available"
				+ (z.getReservationHeap().size() == 0 ? "" : ". Reservations made by Patrons " + utility.reservationString(z.getReservationHeap()) + " have been cancelled!"), access);
		this.dictionary.remove(z.getBookId());
		this.deleteBook(z);
		this.updateColorFlipCount(this.root);
	}

	// Balance the node after insertion
	private void ficViolationOfInsert(Book child) {
		Book uncle;
		while (child.parent.color == 1) {
			if (child.parent == child.parent.parent.right) {
				uncle = child.parent.parent.left;
				if (uncle != null && uncle.color == 1) {
					uncle.color = 0;
					child.parent.color = 0;
					if(child.parent.parent != root) {
						child.parent.parent.color = 1;
					}
					child = child.parent.parent;
				} else {
					if (child == child.parent.left) {
						child = child.parent;
						rightRotate(child);
					}
					child.parent.color = 0;
					child.parent.parent.color = 1;
					leftRotate(child.parent.parent);
				}
			} else {
				uncle = child.parent.parent.right;
				if (uncle != null && uncle.color == 1) {
					uncle.color = 0;
					child.parent.color = 0;
					if(child.parent.parent != root) {
						child.parent.parent.color = 1;
					}
					child = child.parent.parent;
				} else {
					if (child == child.parent.right) {
						child = child.parent;
						leftRotate(child);
					}
					child.parent.color = 0;
					child.parent.parent.color = 1;
					rightRotate(child.parent.parent);
				}
			}
			if (child == root) {
				break;
			}
		}
		root.color = 0;
	}

	private void printHelper(Book root, String indent, boolean last) {
		if (root != null) {
			System.out.print(indent);
			if (last) {
				System.out.print("R----");
				indent += "   ";
			} else {
				System.out.print("L----");
				indent += "|  ";
			}

			String sColor = root.color == 1 ? "RED" : "BLACK";
			System.out.println(root.getBookId() + "(" + sColor + ")");
			printHelper(root.left, indent, false);
			printHelper(root.right, indent, true);
		}
	}

	public RedBlackTree() {
	}


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
		book.left = null;
		book.right = null;
		book.color = 1;
		return book;
	}

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
		ficViolationOfInsert(node);
		updateColorFlipCount(this.root);
	}

	private void updateColorFlipCount(Book book) {
		if(book == null) {
			return;
		}
		if(dictionary.get(book.getBookId()) != book.color) {
			this.colorFlipCount++;
			this.dictionary.put(book.getBookId(), book.color);
		}
		updateColorFlipCount(book.left);
		updateColorFlipCount(book.right);
		return;
	}

	public Book getRoot() {
		return this.root;
	}

	public void printTree() {
		printHelper(this.root, "", true);
	}
	
	//this method will responsible to return the book and assign it to the patron with highest priority in reservation heap
	public void returnBook(int patronId, int bookId, OutputStream access, Utility utility) throws IOException {
		Book book = this.getBookFromLibrary(this.root, bookId);
		if(book.getBorrowedBy() != patronId) {
			if(utility.getReservations(book.getReservationHeap()).contains(patronId)) {
				utility.write("Patron " + patronId +" never borrowed the book "+ bookId + ", Patron is still in the reservation to get the book", access);
			} else {
				utility.write("Patron " + patronId +" never borrowed the book "+ bookId, access);
			}
			return;
		}
		if(book != null) {
			utility.write("Book " + bookId + " Returned by Patron "+ patronId, access);
			List<Reservation> reservationHeap = book.getReservationHeap();
			if(reservationHeap != null && !reservationHeap.isEmpty()) {
				int newBorrower = reservationHeap.get(0).getPatronId();
				book.setBorrowedBy(newBorrower);
				reservationHeap.set(0, reservationHeap.get(reservationHeap.size()-1));
				reservationHeap.remove(reservationHeap.size()-1);
				heapfy(reservationHeap, reservationHeap.size(), 0);
				utility.addNewLine(access);
				utility.write("Book " + bookId + " Allotted to Patron "+ newBorrower, access);
			} else {
				book.setAvailabilityStatus("Yes");
			}
		}
	}
	
	//this code method will help to borrow book if available else creates the reservation in the reservation heap
	public void borrowBook(int patronId, int bookId, int patronPriority, OutputStream access, Utility utility) throws IOException {
		Book book = this.getBookFromLibrary(this.root, bookId);
		if(book == null) {
			utility.write("Book not found to add reservation", access);
			return;
		}
		
		if(book.getAvailabilityStatus().toLowerCase().equals("yes")) {
			book.setBorrowedBy(patronId);
			book.setAvailabilityStatus("No");
			utility.write("Book " + bookId + " Borrowed by Patron " + patronId, access);
			return;
		}
		
		if(book.getBorrowedBy() == patronId) {
			utility.write("Book " + bookId + " already Borrowed by Patron " + patronId, access);
			return;	
		}
		
		List<Reservation> reservationHeap = book.getReservationHeap();
		if(utility.getReservations(reservationHeap).contains(patronId)) {
			utility.write("Book " + bookId + " already Reserved by Patron " + patronId, access);
			return;
		}
		
		if(reservationHeap.size() >= 20) {
			utility.write("Reservations for book " + bookId + " is full", access);
			return;
		}
		Reservation reservation = new Reservation(patronId, patronPriority, utility.getTimestampForReservation());
		List<Reservation> reverations = this.insertIntoReservationHeap(reservation, reservationHeap);
		book.setReservationHeap(reverations);
		utility.write("Book " + bookId + " Reserved by Patron " + patronId, access);
	} 
	
	//this method will help to insert the reservation of patron into reservation heap
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
    
    //compare the child and parent node based on priority and if priority is same then compare the time of reservation
	private int compare(Reservation child, Reservation parent) {
        if (child.getPriorityNumber() < parent.getPriorityNumber()) {
            return -1;
        } else if (child.getPriorityNumber() > parent.getPriorityNumber()) {
            return 1;
        } else {
            return Long.compare(child.getTimeOfReservation(), parent.getTimeOfReservation());
        }
    }
	
	//heapify strategy to mantain the order
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
	
	//this method will find the closest book 
	public void findClosestBooks(int bookId, Utility utility, OutputStream access) throws IOException {
        Book currentNode = root;
        int closest = root.getBookId();

        while (currentNode != null) {
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
	
	
	//this method will print the books into the file in order
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
		this.treeMap.clear();
	}
	
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
	
	
	//deleting the boook from the tree
	public void deleteBook(Book book) {
		if(this.root == book && book.left == null && book.right == null) {
			this.root = null;
			return;
		}
		
		Book x = null;
		if(book.right != null && book.left != null) {
			x = findPredessor(book);
			copyPredessorData(x, book);
			deleteBook(x);
			return;
		}
		
		if(book.right == null && book.left == null) {
			if(book.color == 1) {
				//leaf will be red here deleting
				if(book.parent != null) {
					if(book.parent.right == book) {
						book.parent.right = null;
					} else {
						book.parent.left = null;
					}
				}
				return;
			} else {
				//leaf is black
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
		
		if(book.right == null || book.left == null) {
			Book y;
			if(book.left != null) {
				y = book.left;
			} else {
				y = book.right;
			}
			if(book.color == 1) {
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
					return;
				} else {
					this.fixDelete(y);
				}
				return;
			}
		}
	}

	private void copyPredessorData(Book x, Book book) {
		book.setBookName(x.getBookName());
		book.setAuthorName(x.getAuthorName());
		book.setAvailabilityStatus(x.getAvailabilityStatus());
		book.setBookId(x.getBookId());
		book.setBorrowedBy(x.getBorrowedBy());
		book.setReservationHeap(x.getReservationHeap());
	}

	private Book findPredessor(Book book) {
		Book pred = book.left;
		while(pred.right != null) {
			pred = pred.right;
		}
		return pred;
	}
	
	public void fixDelete(Book book) {
		if(book == null || book == this.root) {
			return;
		}
		if(getSibling(book).color == 0) {
			if(getRedCount(getSibling(book)) > 0) {
				if(getRedCount(getSibling(book)) == 1) {
					if(book.parent.right == book) {
						if(getSibling(book).left != null && getSibling(book).left.color == 1) {
							if(book.parent.color == 1) {
								getSibling(book).color = 1;
								book.parent.color = 0;
							}
							getSibling(book).left.color = 0;
							rotateLeft(getSibling(book).left);
							return;
						} else {
							if(book.parent.color == 1) {
								book.parent.color = 0;
							} else {
								getSibling(book).right.color = 0;
							}
							this.rotateLeftRight(getSibling(book).right);
							return;
						}
					} else {
						if(getSibling(book).right != null && getSibling(book).right.color == 1) {
							if(book.parent.color == 1) {
								getSibling(book).color = 1;
								book.parent.color = 0;
							}
							getSibling(book).right.color = 0;
							this.rotateRight(getSibling(book).right);
							return;
						} else {
							if(book.parent.color == 1) {
								book.parent.color = 0;
							} else {
								getSibling(book).left.color = 0;
							}
							rotateRightLeft(getSibling(book).left);
							return;
						}
					}
				} else {
					if(book.parent.right == book) {
						if(book.parent.color == 1) {
							book.parent.color =0;
						} else {
							getSibling(book).right.color = 0;
						}
						this.rotateLeftRight(getSibling(book).right);
						return;
					} else {
						if(book.parent.color == 1) {
							book.parent.color = 0;
						} else {
							getSibling(book).left.color = 0;
						}
						this.rotateRightLeft(getSibling(book).left);
						return;
					}
				}
			} else {
				if(book.parent.color == 1) {
					book.parent.color = 0;
					getSibling(book).color = 1;
					return;
				} else {
					getSibling(book).color = 1;
					this.fixDelete(book.parent);
					return;
				}
			}
		} else {
			if(book.parent.right == book) {
				if(getSibling(book).right == null || (getSibling(book).right != null && this.getRedCount(getSibling(book).right) == 0)) {
					getSibling(book).color = 0;
					if(getSibling(book).right != null) {
						getSibling(book).right.color = 1;
					}
					this.rotateLeft(getSibling(book).left);
					return;
				} else if(getSibling(book).right != null && getRedCount(getSibling(book).right) == 1) {
					if(getSibling(book).right.left != null && getSibling(book).right.left.color == 1) {
						getSibling(book).right.left.color = 0;
						this.rotateLeftRight(getSibling(book).right);
						return;
					} else {
						getSibling(book).right.right.color = 0;
						this.rotateRight(getSibling(book).right.right);
						this.rotateLeftRight(getSibling(book).right);
						Book x = book.parent.parent;
						Book v = x.left.left;
						Book w = x.left;
						w.parent = v;
						w.left = v.right;
						if(v.right != null) {
							v.right.parent = w;
						}
						v.right = w;
						v.parent = x;
						x.left = v;
						return;
					}
					//check
				} else if(getSibling(book).right != null && getRedCount(getSibling(book).right) == 2) {
					getSibling(book).right.right.color = 0;
					this.rotateRight(getSibling(book).right.right);
					this.rotateLeftRight(getSibling(book).right);
					Book x = book.parent.parent;
					Book v = x.left.left;
					Book w = x.left;
					w.parent = v;
					w.left = v.right;
					if(v.right != null) {
						v.right.parent = w;
					}
					v.right = w;
					v.parent = x;
					x.left = v;
					return;
				} else {
					int parentColor = book.parent.color;
					book.parent.color = getSibling(book).color;
					getSibling(book).color = parentColor;
					Book sibling = getSibling(book);
					
					if(book.parent == this.root) {
						this.root = sibling;
					}
					
					book.parent.left = sibling.right;
					if(sibling.right != null) {
						sibling.right.parent = book.parent;
					}
					sibling.parent = book.parent;
					book.parent = sibling;
					if(sibling.parent != null) {
						if(sibling.parent.right == book.parent) {
							sibling.parent.right = sibling;
						} else {
							sibling.parent.left = sibling;
						}
					}
					this.fixDelete(book);
					return;
				}
			} else {
				if (getSibling(book).left == null || ( getSibling(book).left != null && this.getRedCount(getSibling(book).left) == 0)) {
					 getSibling(book).color = 0;
					 if(getSibling(book).left != null) {
						 getSibling(book).left.color = 1;
					 }
					 this.rotateRight(getSibling(book).right);
					 return;
				} else if (getSibling(book).left != null && this.getRedCount(getSibling(book).left) == 1) {
					if(getSibling(book).left.right != null && getSibling(book).left.right.color == 1) {
						getSibling(book).left.right.color = 0;
						this.rotateRightLeft(getSibling(book).left);
						return;
					} else {
						getSibling(book).left.left.color = 0;
						this.rotateLeft(getSibling(book).left.left);
						this.rotateRightLeft(getSibling(book).left);
						Book x = book.parent.parent;
						Book v = x.right.right;
						Book w = x.right;
						w.parent = v;
						w.right = v.left;
						if(v.left != null) {
							v.left.parent = w;
						}
						v.left = w;
						v.parent = x;
			            x.right = v;
			            return;
					}
				} else if(getSibling(book).left != null && this.getRedCount(getSibling(book).left) == 2) {
					getSibling(book).left.left.color = 0;
					this.rotateLeft(getSibling(book).left.left);
					this.rotateRightLeft(getSibling(book).left);
					Book x = book.parent.parent;
					Book v = x.right.right;
					Book w = x.right;
					w.parent = v;
					w.right = v.left;
					if(v.left != null) {
						v.left.parent = w;
					}
					v.left = w;
					v.parent = x;
		            x.right = v;
		            return;
				} else {
					int parentColor = book.parent.color;
					book.parent.color = getSibling(book).color;
					getSibling(book).color = parentColor;
					
					Book sibling = getSibling(book);
					if(book.parent == this.root) {
						this.root = sibling;
					}
					book.parent.left = sibling.right;
					if(sibling.right != null) {
						sibling.right.parent = book.parent;
					}
					sibling.parent = book.parent;
					book.parent = sibling;
					if(sibling.parent != null) {
						if(sibling.parent.right == book.parent) {
							sibling.parent.right = sibling;
						} else {
							sibling.parent.left = sibling;
						}
					}
					this.fixDelete(book);
					return;
				}
			}
		}
	}
	
	private Book rotateLeft(Book book) {
		Book fNode = book.parent.parent;
		book.parent.parent = fNode.parent;
		
		fNode.left = book.parent.right;
		
		if(book.parent.right != null) {
			book.parent.right.parent = fNode;
		}
		book.parent.right = fNode;
		fNode.parent = book.parent;
		
		if(book.parent.parent != null && book.parent.parent.getBookId() < book.parent.getBookId()) {
			book.parent.parent.right = book.parent;
		} else if(book.parent.parent != null) {
			book.parent.parent.left = book.parent; 
		}
		if(fNode == this.root) {
			this.root = book.parent;
		}
		return book.parent;
	}
	
	private Book rotateRight(Book lastNode) {
		Book fNode = lastNode.parent.parent;
		lastNode.parent.parent = fNode.parent;
		
		fNode.right = lastNode.parent.left;
		
		if(lastNode.parent.left != null) {
			lastNode.parent.left.parent = fNode;
		}
		lastNode.parent.left = fNode;
		fNode.parent = lastNode.parent;
		
		if(lastNode.parent.parent != null && lastNode.parent.parent.getBookId() < lastNode.parent.getBookId()) {
			lastNode.parent.parent.right = lastNode.parent;
		} else if(lastNode.parent.parent != null) {
			lastNode.parent.parent.left = lastNode.parent; 
		}
		if(fNode == this.root) {
			this.root = lastNode.parent;
		}
		return lastNode.parent;
	}
	
	private Book rotateLeftRight(Book lastNode) {
		Book fNode = lastNode.parent.parent;
		fNode.left = lastNode.right;

		 if(lastNode.right != null) {
			 lastNode.right.parent = fNode;
		 }
		 lastNode.right = fNode;
		 lastNode.parent.right = lastNode.left;
		 
		 
		 if(lastNode.left != null) {
			 lastNode.left.parent = lastNode.parent;
		 }
		 
		 lastNode.left = lastNode.parent;
		 lastNode.parent = fNode.parent;
		 fNode.parent = lastNode;
		 lastNode.left.parent = lastNode;
		 
		 if(lastNode.parent != null && lastNode.parent.getBookId() < lastNode.getBookId()) {
			 lastNode.parent.right = lastNode;
		 } else if(lastNode.parent != null) {
			 lastNode.parent.left = lastNode;
		 }
		 
		 if(fNode == this.root) {
			 this.root = lastNode;
		 }
		 
		 return lastNode;
		 
	}
	
	private Book rotateRightLeft(Book lastNode) {
		Book fNode = lastNode.parent.parent;
		fNode.right = lastNode.left;

		 if(lastNode.left != null) {
			 lastNode.left.parent = fNode;
		 }
		 lastNode.left = fNode;
		 lastNode.parent.left = lastNode.right;
		 
		 
		 if(lastNode.right != null) {
			 lastNode.right.parent = lastNode.parent;
		 }
		 
		 lastNode.parent.parent = lastNode;
		 lastNode.right = lastNode.parent;
		 lastNode.parent = fNode.parent;
		 fNode.parent = lastNode;
		 
		 if(lastNode.parent != null && lastNode.parent.getBookId() < lastNode.getBookId()) {
			 lastNode.parent.right = lastNode;
		 } else if(lastNode.parent != null) {
			 lastNode.parent.left = lastNode;
		 }
		 
		 if(fNode == this.root) {
			 this.root = lastNode;
		 }
		 
		 return lastNode;
		 
	}

	public int getRedCount(Book book) {
		int redCount = 0;
		if(book != null) {
			if(book.left != null && book.left.color == 1) {
				redCount += 1;
			} else if(book.right != null && book.right.color == 1){
				redCount += 1;
			}
		}
		return redCount;
	}
	
	public Book getSibling(Book node) {
		if(node.parent != null) {
			if(node.parent.left == node) {
				return node.parent.right;
			}
			return node.parent.left;
		}
		return null;
	}

	
	//to check the tree is RBT 
	static class INT
	{
	    static int d;
	    INT()
	    {
	        d = 0;
	    }
	}
	 
	@SuppressWarnings("static-access")
	static boolean isBalancedUtil(Book root,
	                        INT maxh, INT minh)
	{
	     
	    if (root == null)
	    {
	        maxh.d = minh.d = 0;
	        return true;
	    }
	     
	    INT lmxh=new INT(), lmnh=new INT(); 
	     
	    INT rmxh=new INT(), rmnh=new INT(); 
	 
	    if (isBalancedUtil(root.left, lmxh, lmnh) == false)
	        return false;
	 
	    if (isBalancedUtil(root.right, rmxh, rmnh) == false)
	        return false;
	 
	    maxh.d = Math.max(lmxh.d, rmxh.d) + 1;
	    minh.d = Math.min(lmnh.d, rmnh.d) + 1;
	 
	    if (maxh.d <= 2*minh.d)
	        return true;
	 
	    return false;
	}
	 
	// A wrapper over isBalancedUtil()
	static boolean isBalanced(Book root)
	{
	    INT maxh=new INT(), minh=new INT();
	    return isBalancedUtil(root, maxh, minh);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}