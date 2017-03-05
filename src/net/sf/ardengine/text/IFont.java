package net.sf.ardengine.text;

public interface IFont {

	public static final char[] CZECH_CHARACTERS = ("ÁáÉéÍíÓóÚúŮůČčŘřŽžÝýěňťď").toCharArray();

    /** Automatically called by FontManager to notify renderer*/
    public void free();

	/**
	 * @return px height of loaded font chars
     */
	public int getFontHeight();
}
