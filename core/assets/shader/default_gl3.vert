#version 330 core

in vec4 a_position;
in vec4 a_color;
in vec2 a_texCoord0;
uniform mat4 u_projTrans;
out vec4 v_color;
out vec2 v_texCoords;
out vec2 position;

void main() {
   v_color = a_color;
   v_color.a = v_color.a;// * (255.0/254.0);
   v_texCoords = a_texCoord0;
   gl_Position =  u_projTrans * a_position;
   position = a_position.xy;
}
