#define MAX_LIGHT_COUNT 12

uniform sampler2D u_texture;

varying vec2 v_texCoords;
varying vec4 v_colour;
varying vec4 v_worldPos;

struct light {
    bool enabled;
    float radius;
    vec3 colour;
    vec4 position;
};

uniform light u_lights[MAX_LIGHT_COUNT];
uniform vec4 u_ambientLight;

vec4 lighting(light l, vec4 wp) {
    float dist = distance(l.position, wp);
    float att = clamp(1.0 - dist * dist / (l.radius * l.radius), 0.0, 1.0);
    att *= att;
    return vec4(att, att, att, 1.0) * vec4(l.colour, 1.0);
}

void main() {
    vec4 diff = v_colour * texture2D(u_texture, v_texCoords.xy);

    for (int i = 0; i < MAX_LIGHT_COUNT; ++i) {
        if (u_lights[i].enabled) {
            diff *= lighting(u_lights[i], v_worldPos);
        }
    }

    diff *= lighting(u_lights[0], v_worldPos);

    gl_FragColor = diff + u_ambientLight;
}