uniform sampler2D u_texture;

varying vec2 v_texCoords;
varying vec4 v_colour;
varying vec4 v_worldPos;

void main() {
    gl_FragColor = v_colour * texture2D(u_texture, v_worldPos.xy);
}