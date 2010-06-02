package edu.rpi.phil.legup.puzzles.masyu;

public interface Checker {
	/**
	 * Returns true iff the new wall referenced by ba.hasDir(ba.North(ba.getCell(0,0)))
	 * has is a valid addition
	 * @param ba the board accessor for the new line to be checked
	 * @return true iff above contidions are met
	 */
	String check(BoardAccessor ba);
}
