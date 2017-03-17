package jr.utils;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Custom string style for {@link org.apache.commons.lang3.builder.ToStringBuilder}.
 */
public class MultiLineNoPrefixToStringStyle extends ToStringStyle {
	/**
	 * The style.
	 */
	public static final MultiLineNoPrefixToStringStyle STYLE = new MultiLineNoPrefixToStringStyle();
	
	/**
	 * svuid
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 */
	public MultiLineNoPrefixToStringStyle() {
		super();
		
		this.setContentStart("");
		this.setUseShortClassName(true);
		this.setUseIdentityHashCode(false);
		this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + "  ");
		this.setFieldSeparatorAtStart(true);
		this.setContentEnd(SystemUtils.LINE_SEPARATOR + "");
	}
	
	private Object readResolve() {
		return MultiLineNoPrefixToStringStyle.STYLE;
	}
}
