package org.noixdecoco.app.exception;

// You're all out of your daily coconut quota!
public class InsufficientCoconutsException extends CoconutException {

	private static final long serialVersionUID = -5130482296182134037L;
	
	private long numberRemaining;
	
	public InsufficientCoconutsException(long numberRemaining) {
		this.numberRemaining = numberRemaining;
	}

	public long getNumberRemaining() {
		return numberRemaining;
	}

	public void setNumberRemaining(long numberRemaining) {
		this.numberRemaining = numberRemaining;
	}
	
}
