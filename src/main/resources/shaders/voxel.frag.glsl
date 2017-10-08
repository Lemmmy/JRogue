#version 140

struct light {
	bool enabled;

	vec3 position;
	vec3 colour;
	float attenuation_factor;
};

in vec3 v_colour;
in vec3 v_normal;
in vec3 v_surfPos;

uniform light u_lights[16];

out vec4 out_colour;

void main() {
	vec3 final_colour = vec3(1, 1, 1);

	for (int i = 0; i < 16; i++) {
		if (u_lights[i].enabled) {
			vec3 surface_to_light = normalize(u_lights[i].position - v_surfPos);
			float brightness = clamp(dot(surface_to_light, v_normal), 0, 1);
			float dist = distance(u_lights[i].position, v_surfPos);
			float attenuation = 1 / (1 + u_lights[i].attenuation_factor * (dist * dist));
			final_colour *= brightness * attenuation;
		}
	}

	out_colour = vec4(final_colour * v_colour, 1.0);
}