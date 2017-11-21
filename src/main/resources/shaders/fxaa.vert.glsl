#version 300 es

in vec4 a_position;
in vec2 a_texCoord0;

uniform mat4 u_projTrans;

out vec2 v_uv;

void main() {
	gl_Position = u_projTrans * a_position;
	v_uv = a_texCoord0;
}