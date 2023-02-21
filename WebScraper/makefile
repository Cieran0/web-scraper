default:
	@make all >> /dev/null
	@rm -r WebScraper >> /dev/null

run:
	@make default >> /dev/null
	@java -jar Main.jar

all: 
	javac -d . *.java
	jar cmvf MANIFEST.MF Main.jar WebScraper/

clean:
	-rm *.jar
	-rm *.class
	-rm -r WebScraper