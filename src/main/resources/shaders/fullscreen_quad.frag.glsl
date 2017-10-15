#version 300 es

#define MAX_LIGHTS 256

precision mediump float;

struct Light {
	vec3 position;
	float padding1;
	vec3 colour;
	float attenuation_factor;
};

layout(std140) uniform Lights {
	int count;
	Light lights[MAX_LIGHTS];
} u_lights;

uniform sampler2D u_g_diffuse;
uniform sampler2D u_g_normal;
uniform sampler2D u_g_pos;
uniform sampler2D u_g_depth;

in vec2 v_uv;

out vec3 out_colour;

void main() {
	vec3 final_colour = vec3(0.0f, 0.0f, 0.0f);

	vec3 diffuse = texture(u_g_diffuse, v_uv).rgb;
	vec3 normal = texture(u_g_normal, v_uv).xyz;
	vec3 surf_pos = texture(u_g_pos, v_uv).xyz;

	for (int i = 0; i < u_lights.count; i++) {
		vec3 surface_to_light = normalize(u_lights.lights[i].position - surf_pos);
		float brightness = clamp(dot(surface_to_light, normal), 0.0f, 1.0f);
		float dist = distance(u_lights.lights[i].position, surf_pos);
		float attenuation = 1.0f / (1.0f + u_lights.lights[i].attenuation_factor * (dist * dist));
		final_colour += u_lights.lights[i].colour * brightness * attenuation;
	}

	out_colour = diffuse * final_colour;
}