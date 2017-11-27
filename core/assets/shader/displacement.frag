//#version 330 core

#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif

uniform vec2 resolution;
uniform float tick;

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {
  vec2 offset = v_texCoords - vec2(0.5);
  float distance = length(offset);

  float displacementAmount = 1.0-sqrt(1.0-pow(distance*2.0, 2.0));

  vec2 displacement;
  if(distance > 0.5) {
    displacement = vec2(0.5);
  } else {
    displacement = vec2(0.5) + normalize(offset) * displacementAmount * 0.5;
  }

  gl_FragColor = vec4(displacement.x, displacement.y, 0.5, 1.0);
}