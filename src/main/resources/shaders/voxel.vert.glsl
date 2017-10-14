#version 300 es

uniform mat4 u_projTrans;

layout(location = 0) in vec3 in_position;
layout(location = 1) in vec3 in_normal;

layout(location = 2) in vec3 in_instance_position;
layout(location = 3) in vec3 in_instance_colour;

out vec3 v_colour;
out vec3 v_normal;
out vec3 v_surfPos;

void main() {
	vec4 final_pos = vec4(in_position + in_instance_position, 1.0f);
	gl_Position = u_projTrans * final_pos;
	v_colour = in_instance_colour;
	v_normal = in_normal;
	v_surfPos = final_pos.xyz;
}