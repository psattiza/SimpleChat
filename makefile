GS = -g
JC = javac
.SUFFIXES: .java .class
default:
	$(JC) $(JFLAGS) -cp lib/JCarrierPigeon-1.3.jar:lib/timingframework-classic-1.1.jar src/ChatClient.java  src/ChatHandler.java  src/ChatServer.java
	mv src/*.class .

server:
	java ChatServer 9001
client:
	java ChatClient 10.79.85.169 9001

clean:
	$(RM) *.class
