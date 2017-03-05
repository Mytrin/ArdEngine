#version 330 core

uniform float transparency; //<0;1>
uniform vec3 coloring;

// Ouput data
layout(location=0) out vec4 color;

void main(){
color = vec4( coloring.r, coloring.g, coloring.b, transparency);
}