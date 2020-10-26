package edu.upenn.sas.matthews.ms.view;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class Annotation implements Comparable<Annotation> {

    final public double mz;
    final public double h;
    public AttributedString text;
    final public boolean isSpecial;
    final public boolean isNTerm;

    public Annotation(double mz, double h, AttributedString text, boolean isSpecial, boolean isNTerm) {
        this.mz = mz;
        this.h = h;
        this.text = text;
        this.isSpecial = isSpecial;
        this.isNTerm = isNTerm;
    }
    
    @Override
	public int compareTo(Annotation a) {
    	if (this.mz > a.mz) {
    		return 1;
    	} else if (this.mz < a.mz) {
    		return -1;
    	}
    	
		return 0;
	}
    
    @Override
    public String toString() {
    	if (text == null) {
    		return "" + mz + " " + h + " null " + isSpecial + " " + isNTerm;
    	}
    	
    	StringBuffer sb = new StringBuffer("" + mz + " " + h + " ");
    	AttributedCharacterIterator it = text.getIterator();
    	for (char c = it.first(); c != it.DONE; c = it.next()) {
    		sb.append(c);
    	}
    	sb.append(" " + isSpecial + " " + " " + isNTerm);
    	return sb.toString();
    }
	    
}
