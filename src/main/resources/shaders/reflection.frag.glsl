#version 120

#define PI 3.141592654

uniform float u_waveAmplitude;
uniform float u_waveFrequency;
uniform float u_timeScale;

uniform float u_fadeAmplitude;
uniform float u_fadeBase;

precision mediump float;

uniform sampler2D u_texture;
uniform float u_time;

varying vec2 v_texCoords;
varying vec4 v_colour;

void main() {
	float ntx = u_waveAmplitude * sin(2.0 * PI * u_waveFrequency * v_texCoords.y + u_time * u_timeScale) + v_texCoords.x;
	vec2 nt = vec2(min(1.0, max(0.0, ntx)), v_texCoords.y);
	vec4 texel = texture2D(u_texture, nt);
	float fy = normalize(nt).y;
	float fade = u_fadeAmplitude * fy * fy + u_fadeBase;
	gl_FragColor = v_colour * vec4(texel.rgb, texel.a * fade);
}
