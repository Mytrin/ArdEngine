#version 330 core

// Buffer layout
layout(location = 0) in vec4 vertexPosition;

// Uniforms
uniform mat4 rotationMatrix; //Passing rotation
uniform mat4 transformMatrix; //Passing scale and position
uniform mat4 ortoMatrix;

void main(){
   gl_Position.xyzw = ortoMatrix * (transformMatrix * (rotationMatrix * vertexPosition));
}