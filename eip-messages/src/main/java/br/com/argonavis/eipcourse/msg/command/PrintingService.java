package br.com.argonavis.eipcourse.msg.command;

public class PrintingService {

	public void print(String text, Integer copies) {
		System.out.println("Printing now " + copies + " copies of '" + text + "'");
	}

	public void addToPrintQueue(String text, Integer copies) {
		System.out.println("Adding to queue " + copies + " copies of '" + text + "' to print later!");
	}

	public void printQueue() {
		System.out.println("Printing all documents in queue now!");
	}
}
