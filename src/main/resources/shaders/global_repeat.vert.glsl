attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform mat4 u_proj;

varying vec2 v_texCoords;
varying vec4 v_colour;
varying vec4 v_worldPos;

void main() {
    v_texCoords = a_texCoord0;
    v_colour = a_color;
    v_worldPos = u_proj * a_position;
    gl_Position = u_projTrans * a_position;
}