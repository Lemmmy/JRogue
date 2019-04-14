#version 120

attribute vec4 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
varying vec4 v_texCoords[3];

uniform vec2 u_size;

void main() {
    vec2 offset = vec2(1.0 / u_size.x, 1.0 / u_size.y);

    gl_Position = u_projTrans * a_position;

    v_texCoords[0] = a_texCoord0.xyxy;
    v_texCoords[1] = a_texCoord0.xyxy + vec4(0, -offset.y, -offset.x, 0);
    v_texCoords[2] = a_texCoord0.xyxy + vec4(offset.x, 0, 0, offset.y);
}