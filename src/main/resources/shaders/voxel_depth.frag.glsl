#version 300 es

precision mediump float;

uniform float u_cameraFar;
uniform vec3 u_lightPosition;

in vec4 v_position;

out vec4 out_colour;

void main() {
	out_colour = vec4(vec3(length(v_position.xyz - u_lightPosition) / u_cameraFar), 1.0f);
}