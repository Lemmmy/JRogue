package jr.utils;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Custom string style for {@link org.apache.commons.lang3.builder.ToStringBuilder}.
 */
public class DebugToStringStyle extends ToStringStyle {
	/** The style. */
	public static final DebugToStringStyle STYLE = new DebugToStringStyle();
	
	/** svuid */
	private static final long serialVersionUID = 1L;
	
	/** Indenting of inner lines. */
	private static final int INDENT = 2;
	
	/** Current indenting. */
	private int spaces = 2;
	
	/**
	 * Constructor
	 */
	public DebugToStringStyle() {
		super();
		
		this.setUseShortClassName(true);
		this.setUseIdentityHashCode(false);
		this.setFieldSeparatorAtStart(true);
	}
	
	private void resetIndent() {
		setArrayStart("{" + System.lineSeparator() + spacer(spaces));
		setArraySeparator("," + System.lineSeparator() + spacer(spaces));
		setArrayEnd(System.lineSeparator() + spacer(spaces - INDENT) + "}");
		
		setContentStart(spacer(spaces).toString());
		setFieldSeparator(System.lineSeparator() + spacer(spaces));
		setContentEnd(System.lineSeparator() + spacer(spaces - INDENT));
	}
	
	@Override
	protected void appendClassName(final StringBuffer buffer, final Object object) {
		if (object != null) {
			buffer.append("[CYAN]" + getShortClassName(object.getClass()) + "[]");
		}
	}
	
	private StringBuilder spacer(final int spaces) {
		final StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < spaces; i++) {
			sb.append(" ");
		}
		
		return sb;
	}
	
	@Override
	public void appendDetail(final StringBuffer buffer, final String fieldName, final Object value) {
		if (!ClassUtils.isPrimitiveWrapper(value.getClass()) && !String.class.equals(value.getClass())) {
			spaces += INDENT;
			resetIndent();
			
			ToStringBuilder tsb = null;
			
			if (value instanceof ToStringBuilder) {
				tsb = (ToStringBuilder) value;
			} else {
				try {
					Method tsbMethod = value.getClass().getMethod("toStringBuilder");
					
					if (tsbMethod != null) {
						tsb = (ToStringBuilder) tsbMethod.invoke(value);
					}
				} catch (Exception ignored) {
					buffer.append(value.toString());
				}
			}
			
			if (tsb != null) {
				String cn = tsb.toString().split("(\\r\\n|\\r|\\n)")[0];
				String s = tsb.toString().replaceFirst(Pattern.quote(cn) + "(\\r\\n|\\r|\\n)", "");
				
				buffer.append(cn + System.lineSeparator());
				spaces += INDENT;
				resetIndent();
				buffer.append(s);
				spaces -= INDENT;
				resetIndent();
			} else {
				buffer.append(value.toString());
			}
			
			spaces -= INDENT;
			resetIndent();
		} else {
			super.appendDetail(buffer, fieldName, value);
		}
	}
	
	@Override
	protected void appendDetail(final StringBuffer buffer, final String fieldName, final Object[] array) {
		spaces += INDENT;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= INDENT;
		resetIndent();
	}
	
	@Override
	protected void reflectionAppendArrayDetail(final StringBuffer buffer, final String fieldName, final Object array) {
		spaces += INDENT;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= INDENT;
		resetIndent();
	}
	
	@Override
	protected void appendDetail(final StringBuffer buffer, final String fieldName, final long[] array) {
		spaces += INDENT;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= INDENT;
		resetIndent();
	}
	
	@Override
	protected void appendDetail(final StringBuffer buffer, final String fieldName, final int[] array) {
		spaces += INDENT;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= INDENT;
		resetIndent();
	}
	
	@Override
	protected void appendDetail(final StringBuffer buffer, final String fieldName, final short[] array) {
		spaces += INDENT;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= INDENT;
		resetIndent();
	}
	
	@Override
	protected void appendDetail(final StringBuffer buffer, final String fieldName, final byte[] array) {
		spaces += INDENT;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= INDENT;
		resetIndent();
	}
	
	@Override
	protected void appendDetail(final StringBuffer buffer, final String fieldName, final char[] array) {
		spaces += INDENT;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= INDENT;
		resetIndent();
	}
	
	@Override
	protected void appendDetail(final StringBuffer buffer, final String fieldName, final double[] array) {
		spaces += INDENT;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= INDENT;
		resetIndent();
	}
	
	@Override
	protected void appendDetail(final StringBuffer buffer, final String fieldName, final float[] array) {
		spaces += INDENT;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= INDENT;
		resetIndent();
	}
	
	@Override
	protected void appendDetail(final StringBuffer buffer, final String fieldName, final boolean[] array) {
		spaces += INDENT;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= INDENT;
		resetIndent();
	}
	
	private Object readResolve() {
		return DebugToStringStyle.STYLE;
	}
}
