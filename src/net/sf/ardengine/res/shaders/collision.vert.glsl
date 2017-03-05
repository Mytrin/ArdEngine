#version 330 core

// Buffer layout
layout(location = 0) in vec2 vertexPosition;

// Uniforms
uniform mat4 ortoMatrix;

void main(){
   gl_Position.xyzw = ortoMatrix * vec4(vertexPosition, 0, 1);
}