package jr.utils;

import com.badlogic.gdx.Input;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class Directions {
    public static VectorInt SOUTH_WEST     = VectorInt.get(-1, -1);
    public static VectorInt SOUTH         = VectorInt.get( 0, -1);
    public static VectorInt SOUTH_EAST     = VectorInt.get( 1, -1);
    public static VectorInt WEST         = VectorInt.get(-1,  0);
    public static VectorInt CENTER         = VectorInt.ZERO;
    public static VectorInt EAST         = VectorInt.get( 1,  0);
    public static VectorInt NORTH_WEST     = VectorInt.get(-1,  1);
    public static VectorInt NORTH         = VectorInt.get( 0,  1);
    public static VectorInt NORTH_EAST     = VectorInt.get( 1,  1);
    
    /**
     * The four cardinal directions as {@link VectorInt VectorInts}, in the following order:
     *
     * <ul>
     *     <li>{@code [0]} = {@link VectorInt}(1, 0) <i>({@link #EAST})</i></li>
     *     <li>{@code [1]} = {@link VectorInt}(-1, 0) <i>({@link #WEST})</i></li>
     *     <li>{@code [2]} = {@link VectorInt}(0, -1) <i>({@link #SOUTH})</i></li>
     *     <li>{@code [3]} = {@link VectorInt}(0, 1) <i>({@link #NORTH})</i></li>
     * </ul>
     */
    public static final VectorInt[] CARDINAL = new VectorInt[] {
        EAST, WEST, SOUTH, NORTH
    };
    
    /**
     * Returns a {@link Stream} of the {@link #CARDINAL cardinal} directions as {@link VectorInt VectorInts}.
     * @return The {@link Stream}.
     */
    public static Stream<VectorInt> cardinal() {
        return Arrays.stream(CARDINAL);
    }
    
    /**
     * The four cardinal directions and the four intercardinal directions as {@link VectorInt VectorInts}, in the
     * following order:
     *
     * <ul>
     *     <li>{@code [0]} = {@link VectorInt}(-1, 1) <i>({@link #NORTH_WEST})</i></li>
     *     <li>{@code [1]} = {@link VectorInt}(0, 1) <i>({@link #NORTH})</i></li>
     *     <li>{@code [2]} = {@link VectorInt}(1, 1) <i>({@link #NORTH_EAST})</i></li>
     *     <li>{@code [3]} = {@link VectorInt}(-1, 0) <i>({@link #WEST})</i></li>
     *     <li>{@code [4]} = {@link VectorInt}(1, 0) <i>({@link #EAST})</i></li>
     *     <li>{@code [5]} = {@link VectorInt}(-1, -1) <i>({@link #SOUTH_WEST})</i></li>
     *     <li>{@code [6]} = {@link VectorInt}(0, -1) <i>({@link #SOUTH})</i></li>
     *     <li>{@code [7]} = {@link VectorInt}(1, -1) <i>({@link #SOUTH_EAST})</i></li>
     * </ul>
     */
    public static final VectorInt[] COMPASS = new VectorInt[] {
        NORTH_WEST,     NORTH,         NORTH_EAST,
        WEST,                         EAST,
        SOUTH_WEST,     SOUTH,         SOUTH_EAST
    };
    
    /**
     * Returns a {@link Stream} of the {@link #COMPASS compass} directions as {@link VectorInt VectorInts}.
     * @return The {@link Stream}.
     */
    public static Stream<VectorInt> compass() {
        return Arrays.stream(COMPASS);
    }
    
    /**
     * The number pad {@link Input.Keys keys} mapped to their movement directions.
     *
     * <pre>
     * &#x2196;   &uarr;   &#x2197;
     * 7   8   9
     *
     * &larr;       &rarr;
     * 4       6
     *
     * &#x2199;   &darr;   &#x2198;
     * 1   2   3
     * </pre>
     */
    public static final Map<Integer, VectorInt> MOVEMENT_KEYS = new HashMap<Integer, VectorInt>() {{
        put(Input.Keys.NUMPAD_1, SOUTH_WEST);
        put(Input.Keys.NUMPAD_2, SOUTH);
        put(Input.Keys.NUMPAD_3, SOUTH_EAST);
        
        put(Input.Keys.NUMPAD_4, WEST);
        put(Input.Keys.NUMPAD_6, EAST);
        
        put(Input.Keys.NUMPAD_7, NORTH_WEST);
        put(Input.Keys.NUMPAD_8, NORTH);
        put(Input.Keys.NUMPAD_9, NORTH_EAST);
    }};
    
    
    /**
     * The number pad keys (as number {@code chars}) mapped to their movement directions.
     *
     * <pre>
     * &#x2196;   &uarr;   &#x2197;
     * 7   8   9
     *
     * &larr;       &rarr;
     * 4       6
     *
     * &#x2199;   &darr;   &#x2198;
     * 1   2   3
     * </pre>
     */
    public static final Map<Character, VectorInt> MOVEMENT_CHARS = new HashMap<Character, VectorInt>() {{
        put('1', SOUTH_WEST);
        put('2', SOUTH);
        put('3', SOUTH_EAST);
        
        put('4', WEST);
        put('6', EAST);
        
        put('7', NORTH_WEST);
        put('8', NORTH);
        put('9', NORTH_EAST);
    }};
}
