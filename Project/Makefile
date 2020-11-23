all:
	jflex src/LexicalAnalyzer.flex
	javac -d bin -cp src/ src/Main.java
	jar cfe dist/Part2.jar Main -C bin .

testing:
		java -jar dist/Part2.jar -v -wt test/Factorial.tex test/Factorial.fs
		java -jar dist/Part2.jar -v -wt test/Some.tex test/Some.fs
		java -jar dist/Part2.jar -v -wt test/No_End.tex test/No_End.fs
		java -jar dist/Part2.jar -v -wt test/Loop.tex test/Loop.fs