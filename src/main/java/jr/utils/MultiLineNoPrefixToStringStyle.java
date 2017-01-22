package jr.utils;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MultiLineNoPrefixToStringStyle extends ToStringStyle {
	public static final MultiLineNoPrefixToStringStyle STYLE = new MultiLineNoPrefixToStringStyle();
	
	private static final long serialVersionUID = 1L;
	
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
