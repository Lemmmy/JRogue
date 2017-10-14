#version 300 es

#define MAX_LIGHTS 128

precision mediump float;

struct Light {
	vec3 position;
	float padding1;
	vec3 colour;
	float padding2;
	float attenuation_factor;
	float padding3[3];
};

layout(std140) uniform Lights {
	int count;
	Light lights[MAX_LIGHTS];
} u_lights;

in vec3 v_colour;
in vec3 v_normal;
in vec3 v_surfPos;

out vec4 out_colour;

void main() {
	vec3 final_colour = vec3(0.0f, 0.0f, 0.0f);

	for (int i = 0; i < u_lights.count; i++) {
		vec3 surface_to_light = normalize(u_lights.lights[i].position - v_surfPos);
		float brightness = clamp(dot(surface_to_light, v_normal), 0.0f, 1.0f);
		float dist = distance(u_lights.lights[i].position, v_surfPos);
		float attenuation = 1.0f / (1.0f + u_lights.lights[i].attenuation_factor * (dist * dist));
		final_colour += u_lights.lights[i].colour * brightness * attenuation;
	}

	out_colour = vec4(final_colour * v_colour, 1.0f);
}