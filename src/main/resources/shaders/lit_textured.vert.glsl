attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;

varying vec2 v_texCoords;
varying vec4 v_colour;
varying vec4 v_worldPos;

void main() {
    v_texCoords = a_texCoord0;
    v_colour = a_color;
    gl_Position = u_projTrans * a_position;
}