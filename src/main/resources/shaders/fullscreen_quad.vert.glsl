#version 300 es

layout(location = 0) in vec2 in_position;
layout(location = 1) in vec2 in_uv;

uniform mat4 u_projTrans;

out vec2 v_uv;

void main() {
	gl_Position = u_projTrans * vec4(in_position, 0.0f, 1.0f);
	v_uv = in_uv;
}