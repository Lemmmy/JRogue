uniform sampler2D u_texture;

varying vec2 v_texCoords;
varying vec4 v_colour;

void main() {
	vec4 texel = texture2D(u_texture, v_texCoords);
	vec2 nt = normalize(v_texCoords);
	float fade = 4.0 * nt.y * nt.y;
	gl_FragColor = vec4(texel.rgb, texel.a * fade);
}
