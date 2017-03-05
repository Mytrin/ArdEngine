#version 330 core

// Interpolated values from the vertex shaders
in vec2 UV;

// Uniforms
uniform sampler2D imageTexture;
uniform float transparency; //<0;1>
uniform vec3 coloring;

// Ouput data
layout(location=0) out vec4 color;

void main(){
//I had to use GL_RED instead of GL_ALPHA
color = vec4( coloring.r, coloring.g, coloring.b, texture(imageTexture, UV).r*transparency);
}