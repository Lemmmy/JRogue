#version 420

#define MAX_LIGHTS 64

struct Light {
	vec3 position;
	vec3 colour;
	float attenuation_factor;
};

uniform Lights {
	int count;
	Light lights[MAX_LIGHTS];
} u_lights;

in vec3 v_colour;
in vec3 v_normal;
in vec3 v_surfPos;

out vec4 out_colour;

void main() {
	vec3 final_colour = vec3(1, 1, 1);

	for (int i = 0; i < u_lights.count; i++) {
		/* vec3 surface_to_light = normalize(u_lights.lights[i].position - v_surfPos);
		float brightness = clamp(dot(surface_to_light, v_normal), 0, 1);
		float dist = distance(u_lights.lights[i].position, v_surfPos);
		float attenuation = 1 / (1 + u_lights.lights[i].attenuation_factor * (dist * dist));
		final_colour *= brightness * attenuation; */

		final_colour = u_lights.lights[i].position;
	}

	out_colour = vec4(final_colour * v_colour, 1.0);
}