#version 330

uniform mat4 u_projTrans;

layout(location = 0) in vec3 in_position;
layout(location = 1) in vec3 in_normal;

layout(location = 2) in vec3 in_instance_position;
layout(location = 3) in vec3 in_instance_colour;

out vec3 v_colour;

void main() {
	gl_Position = u_projTrans * vec4(in_position + in_instance_position, 1.0);
	v_colour = in_instance_colour;
}