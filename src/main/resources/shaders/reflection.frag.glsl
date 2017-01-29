#version 120

#define PI 3.141592654

#define WAVE_AMPLITUDE 	0.0015 // Wobble strength
#define WAVE_FREQUENCY 	16.0   // The "amount" of wobbles in the sprite
#define WAVE_TIME_SCALE 2.0    // Wobble speed

precision mediump float;

uniform sampler2D u_texture;
uniform float u_time;

varying vec2 v_texCoords;
varying vec4 v_colour;

void main() {
	float ntx = WAVE_AMPLITUDE * sin(2.0 * PI * WAVE_FREQUENCY * v_texCoords.y + u_time * WAVE_TIME_SCALE) + v_texCoords.x;
	vec2 nt = vec2(min(1.0, max(0.0, ntx)), v_texCoords.y);
	vec4 texel = texture2D(u_texture, nt);
	float fy = normalize(nt.y);
	float fade = 10.0 * fy * fy + 0.2;
	gl_FragColor = vec4(texel.rgb, texel.a);
}
