#version 330 core

struct Material {
    vec4 diffuseColor;
    sampler2D diffuseTexture;

    vec4 specularColor;
    float shininess;
};

struct Light {
    vec3 position;
    float attenuationLinear;
    float attenuationQuadratic;
    mat4 transform;
    float cameraFar;
    samplerCube depthMap;
};

uniform int numLights;

uniform Light light[8];
uniform Material material;

in vec3 position;
in vec3 normal;
in vec2 textureCoord;

uniform vec4 cameraPosition;

#define Gamma 2.2
#define MinShadowBias 0.005
#define MaxShadowBias 0.035
#define PI 3.1415926535897932384626433832795

out vec4 FragColor;

vec3 calculateLight(int i) {
  vec3 toCamera = normalize(cameraPosition.xyz-position);

  vec3 lightDifference = light[i].position-position;
  vec3 lightDirection = normalize(lightDifference);
  float lightDistance = length(lightDifference);

  vec3 halfway = normalize(lightDirection + toCamera);

  vec3 textureColor = texture(material.diffuseTexture, textureCoord).rgb;
  float diffuseCoeff = max(0.0, dot(normal, lightDirection));
  vec3 diffuse = clamp(diffuseCoeff * textureColor, 0.0, 1.0);

  float specularCoeff = pow(max(0.0, dot(normal, halfway)), material.shininess);
  vec3 specular = clamp(material.specularColor.rgb * specularCoeff * 0.2, 0.0, 1.0);

  float attenuation = 1.0 /
    (1.0 + light[i].attenuationLinear * lightDistance +
    light[i].attenuationQuadratic * (lightDistance * lightDistance));

  vec3 lightLevel = (diffuse + specular) * attenuation;

  vec3 directionFromLight = position - light[i].position;

  float localBias = max(MaxShadowBias * (1.0 - dot(normal, lightDirection)), MinShadowBias);
  float normalizedLightDistance = length(position.xyz - light[i].position) / light[i].cameraFar;

  float distanceFromDepthMap = texture(light[i].depthMap, position - light[i].position).r;

  if(distanceFromDepthMap < normalizedLightDistance - localBias) {
    lightLevel *= 0.6;
  }

  return lightLevel;
}

void main() {
  vec3 ambient = material.diffuseColor.rgb * 0.02;

  vec3 lighting = vec3(0.0);

  for(int i=0; i<numLights; i++) {
    lighting += calculateLight(i);
  }

  FragColor = vec4(lighting + ambient, 1.0);
  FragColor = vec4(lighting, 1.0);
}
