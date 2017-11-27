//#version 330 core

#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif


uniform vec2 resolution;

uniform sampler2D colorBuffer;

void main() {
  vec2 v = gl_FragCoord.xy / resolution;

  vec4 color = texture2D(colorBuffer, v);
  
  gl_FragColor = color;
}