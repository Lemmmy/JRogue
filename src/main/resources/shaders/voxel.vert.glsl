#version 300 es

uniform mat4 u_projTrans;

layout(location = 0) in vec3 in_position;
layout(location = 1) in vec3 in_normal;

layout(location = 2) in vec3 in_instance_position;
layout(location = 3) in float in_instance_rotation;
layout(location = 4) in vec3 in_instance_colour;

out vec3 v_colour;
out vec3 v_normal;
out vec3 v_surfPos;

mat3 rotateY(float rad) {
    float c = cos(rad);
    float s = sin(rad);

    return mat3(
        c, 0.0f, -s,
        0.0f, 1.0f, 0.0f,
        s, 0.0f, c
    );
}

void main() {
	mat3 rotation = rotateY(in_instance_rotation);
	vec4 final_pos = vec4((in_position * rotation) + in_instance_position, 1.0f);
	gl_Position = u_projTrans * final_pos;
	v_colour = in_instance_colour;
	v_normal = in_normal * rotation;
	v_surfPos = final_pos.xyz;
}