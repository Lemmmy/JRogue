package jr.utils;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;
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
		
		resetIndent();
	}
	
	private void resetIndent() {
		setArrayStart("{" + System.lineSeparator() + spacer());
		setArraySeparator("," + System.lineSeparator() + spacer());
		setArrayEnd(System.lineSeparator() + spacer(spaces - INDENT) + "}");
		
		setContentStart("");
		setContentEnd("");
		setFieldSeparator(SystemUtils.LINE_SEPARATOR + spacer());
		setFieldSeparatorAtStart(true);
		setFieldSeparatorAtEnd(false);
	}
	
	@Override
	protected void appendClassName(final StringBuffer buffer, final Object object) {
		if (object != null) {
			buffer.append("+[CYAN]").append(getShortClassName(object.getClass())).append("[]");
		}
	}
	
	private StringBuilder spacer() {
		return spacer(spaces);
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
					appendRegularDetail(buffer, fieldName, value);
					return;
				}
			}
			
			if (tsb != null) {
				String[] lines = Arrays.stream(tsb.toString().split("(\\r?\\n)"))
					.filter(l -> !l.trim().isEmpty())
					.toArray(String[]::new);
				String cn = lines[0];
				
				buffer.append(cn);
				
				if (lines.length > 1) {
					buffer.append(System.lineSeparator());
						
					for (int i = 1; i < lines.length; i++) {
						String sep = i < lines.length - 1 ? System.lineSeparator() : "";
						buffer.append(spacer(spaces - INDENT)).append(lines[i]).append(sep);
					}
				}
			} else {
				appendRegularDetail(buffer, fieldName, value);
				return;
			}
			
			spaces -= INDENT;
			resetIndent();
		} else {
			super.appendDetail(buffer, fieldName, value);
		}
	}
	
	private void appendRegularDetail(final StringBuffer buffer, final String fieldName, final Object value) {
		if (value instanceof UUID) {
			String s = value.toString();
			
			buffer.append(s.substring(0, 4))
				.append("[GREY] .. []")
				.append(s.substring(s.length() - 4));
		} else {
			buffer.append(value.toString());
		}
		
		spaces -= INDENT;
		resetIndent();
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
	
	protected void appendFieldStart(final StringBuffer buffer, final String fieldName) {
		if (isUseFieldNames() && fieldName != null) {
			buffer.append("[GREY]" + fieldName + "[]");
			buffer.append(getFieldNameValueSeparator());
		}
	}
	
	private Object readResolve() {
		return DebugToStringStyle.STYLE;
	}
}
