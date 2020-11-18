all:
	jflex src/LexicalAnalyzer.flex
	javac -d bin -cp src/ src/Main.java
	jar cfe dist/Part2.jar Main -C bin .

testing:
		java -jar dist/Part2.jar test/Factorial.fs
		java -jar dist/Part2.jar test/Sum.fs
		java -jar dist/Part2.jar test/NoSpace.fs