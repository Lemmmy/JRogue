#version 120

precision highp float;

attribute vec4 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
varying vec4 v_texCoords[8];

uniform vec2 u_size;

void main() {
    vec2 offset = vec2(1.0 / u_size.x, 1.0 / u_size.y);

    gl_Position = u_projTrans * a_position;

    v_texCoords[0] = a_texCoord0.xyxy;
    v_texCoords[1] = a_texCoord0.xxxy + vec4(-offset.x, 0, offset.x, -2.0 * offset.y); // A1 B1 C1
    v_texCoords[2] = a_texCoord0.xxxy + vec4(-offset.x, 0, offset.x, -offset.y);       //  A  B  C
    v_texCoords[3] = a_texCoord0.xxxy + vec4(-offset.x, 0, offset.x, 0);               //  D  E  F
    v_texCoords[4] = a_texCoord0.xxxy + vec4(-offset.x, 0, offset.x, offset.y);        //  G  H  I
    v_texCoords[5] = a_texCoord0.xxxy + vec4(-offset.x, 0, offset.x, 2.0 * offset.y);  // G5 H5 I5
    v_texCoords[6] = a_texCoord0.xyyy + vec4(-2.0 * offset.x, -offset.y, 0, offset.y); // A0 D0 G0
    v_texCoords[7] = a_texCoord0.xyyy + vec4(2.0 * offset.x, -offset.y, 0, offset.y);  // C4 F4 I4
}