

mud:
	javac cs3524/solutions/mud/MUDServerImpl.java; \
	javac cs3524/solutions/mud/MUDServerMainline.java; \
	javac cs3524/solutions/mud/Edge.java; \
	javac cs3524/solutions/mud/Vertex.java; \
	javac cs3524/solutions/mud/MUD.java; \
	javac cs3524/solutions/mud/Client.java; \
	javac cs3524/solutions/mud/UserInterface.java; \
	javac cs3524/solutions/mud/UserImpl.java; 

mudclean:
	rm -f cs3524/solutions/mud/*.class
