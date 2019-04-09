package jr.language.transformers;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

public class Capitalise implements TransformerType {
    public static final Transformer first = (s, m) -> StringUtils.capitalize(s);
    public static final Transformer all = (s, m) -> WordUtils.capitalize(s);
}
