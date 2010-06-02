package edu.rpi.phil.legup.puzzles.masyu;

public abstract class Adder {
	String error = null;
	/**
	 * Returns true only iff a line can be added going north
	 * @param ba
	 * @return true iff a line can be added going north
	 */
	public boolean canAdd(BoardAccessor ba)
	{
		String cname = getClass().getCanonicalName();
		cname = cname.replaceFirst("Adder", "Checker");
		
		Checker c;
		try {
			c = (Checker)Class.forName(cname).newInstance();
			return c.check(ba) == null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}
	/**
	 * Gets error string from last add operation
	 * @return Last error string, null if last add operation did not cause one
	 */
	public String getError()
	{
		return error;
	}
}
