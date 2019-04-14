#version 120

#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_texCoords[3];

uniform sampler2D u_texture;

uniform vec2 u_size;

void main() {
    vec2 fp = floor(2.0 * fract(v_texCoords[0].xy * u_size.xy));

    /*
          B	    E0 E1
        D E F   E2 E3
          H
    */

    vec3 B = texture2D(u_texture, v_texCoords[1].xy).xyz;
    vec3 D = texture2D(u_texture, v_texCoords[1].zw).xyz;
    vec3 E = texture2D(u_texture, v_texCoords[0].xy).xyz;
    vec3 F = texture2D(u_texture, v_texCoords[2].xy).xyz;
    vec3 H = texture2D(u_texture, v_texCoords[2].zw).xyz;

    vec3 E0 = B == D ? B : E;
    vec3 E1 = B == F ? B : E;
    vec3 E2 = H == D ? H : E;
    vec3 E3 = H == F ? H : E;

    gl_FragColor = vec4(B != H && D != F ? (fp.y == 0.0 ? (fp.x == 0.0 ? E0 : E1) : (fp.x == 0.0 ? E2 : E3)) : E, 1.0);
}