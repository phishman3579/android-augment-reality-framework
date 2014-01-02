package com.jwetherell.augmented_reality.common;

import java.text.CharacterIterator;

/**
 * This class is a make shift mutable String.
 * 
 * WARNING: This class only works with standard ASCII characters.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class ReusableString implements CharSequence, Appendable {

	private static final int DEFAULT_LENGTH = 1024;

	private static StringBuilder builder = new StringBuilder();

	private char[] chars = null;
	private int length = 0;

	public ReusableString() {
		chars = new char[DEFAULT_LENGTH];
	}

	public ReusableString(int length) {
		chars = new char[length];
	}

	public void set(CharSequence str) {
		for (int i=0; i<str.length(); i++) {
			chars[i] = str.charAt(i);
		}
		length = str.length();
	}

	public void set(char[] chrs) {
		for (int i=0; i<chrs.length; i++) {
			chars[i] = chrs[i];
		}
		length = chrs.length;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public char charAt(int index) {
		return chars[index];
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public int length() {
		return length;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public CharSequence subSequence(int start, int end) {
		StringBuilder builder = new StringBuilder(length);
		for (int i=start; i<end; i++) {
			builder.append(chars[i]);
		}
		return builder;
	}

	public void setLength(int len) {
		length = len;
	}

	public Appendable append(int i) {
		// Find number of digits in number
		int digits=1;
		int division=10;
		while (division<=i) {
			division*=10;
			digits++;
		}

		// Added each digit starting from the right
		int digitsLeft=digits;
		while (digitsLeft>0) {
			int digit = i%10;
			i/=10;
			chars[length+(digitsLeft-1)] = (char)(digit+48);
			digitsLeft--;
		}
		length+=digits;
        return this;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Appendable append(char c) {
		chars[length++] = c;
		return this;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Appendable append(CharSequence csq) {
		for (int i=0; i<csq.length(); i++) {
			chars[length++] = csq.charAt(i);
		}
		return this;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Appendable append(CharSequence csq, int start, int end) {
		for (int i=start; i<end; i++) {
			chars[length++] = csq.charAt(i);
		}
		return this;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String toString() {
		builder.setLength(0);
		for (int i=0; i<length; i++) {
			builder.append(chars[i]);
		}
		return builder.toString();
	}

	private MyCharacterIterator charIter = new MyCharacterIterator();

	public CharacterIterator getCharacterIterator() {
		charIter.current = 0;
		return charIter;
	}

	private class MyCharacterIterator implements CharacterIterator {

		private static final char DONE = '\uFFFF';

		private int current = 0;

	    /**
	     * {@inheritDoc}
	     */
		@Override
		public int getBeginIndex() {
			return 0;
		}

	    /**
	     * {@inheritDoc}
	     */
		@Override
		public int getEndIndex() {
			return length;
		}

	    /**
	     * {@inheritDoc}
	     */
		@Override
		public char first() {
			if (length==0) return DONE;

			current = 0;
			return chars[current];
		}

	    /**
	     * {@inheritDoc}
	     */
		@Override
		public char last() {
			if (length==0) return DONE;

			current = length-1;
			return chars[current];
		}

	    /**
	     * {@inheritDoc}
	     */
		@Override
		public char next() {
			if (current==length-1) return DONE;

			return chars[++current];
		}

	    /**
	     * {@inheritDoc}
	     */
		@Override
		public char previous() {
			if (current==0) return DONE;

			return chars[--current];
		}

	    /**
	     * {@inheritDoc}
	     */
		@Override
		public char current() {
			return chars[current];
		}

	    /**
	     * {@inheritDoc}
	     */
		@Override
		public int getIndex() {
			return current;
		}

	    /**
	     * {@inheritDoc}
	     */
		@Override
		public char setIndex(int location) {
			if (location<0 || location>length) throw new IllegalArgumentException();
			if (location==length) return DONE;

			current = location;
			return chars[current];
		}

	    /**
	     * {@inheritDoc}
	     */
		@Override
		public CharacterIterator clone() {
			return null;
		}
	}
}
