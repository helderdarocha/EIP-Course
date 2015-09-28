package br.com.argonavis.eipcourse.msg.command;

public class PrintingService {
	
    public void print(String text, int copies) {
    	System.out.println("Printing now!");
    }
    
    public void addToPrintQueue(String text, int copies) {
    	System.out.println("Adding to queue to print later!");
    }
    
    public void printQueue() {
    	System.out.println("Printing all documents in queue now!");
    }
}
