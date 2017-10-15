#version 300 es

precision mediump float;

in vec3 v_colour;
in vec3 v_normal;
in vec3 v_surfPos;

layout(location = 0) out vec3 out_colour;
layout(location = 1) out vec3 out_normal;
layout(location = 2) out vec3 out_pos;

void main() {
	out_colour = v_colour;
	out_normal = v_normal;
	out_pos = v_surfPos;
}