#version 330 core

uniform vec3 color;

// Ouput data
layout(location=0) out vec4 outputColor;

void main(){
outputColor = vec4( color.r, color.g, color.b, 1);
}