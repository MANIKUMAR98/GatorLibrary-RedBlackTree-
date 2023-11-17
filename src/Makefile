JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
		  gatorLibrary.java \
		  Book.java \
		  RedBlackTree.java \
		  Reservation.java \
		  Utility.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	
run:
	java -cp . gatorLibrary $(args)