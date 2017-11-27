#version 330 core

in vec3 a_position;
in vec4 a_color;
in vec3 a_normal;
in vec2 a_texCoord0;

uniform mat4 cameraCombined;
uniform mat4 worldTransform;

uniform vec4 cameraPosition;

out vec2 textureCoord;
out vec3 position;
out vec3 normal;

struct Light {
    vec3 position;
    float attenuationLinear;
    float attenuationQuadratic;
    mat4 transform;
    float cameraFar;
    samplerCube depthMap;
};

uniform int numLights;

out vec4 worldPosition;

uniform Light light[8];

void main() {
  position = a_position;
  normal = a_normal;
  textureCoord = a_texCoord0;

  worldPosition = worldTransform * vec4(a_position, 1.0);

  gl_Position = cameraCombined * worldPosition;
}
