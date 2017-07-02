#version 330 core

// Buffer layout
layout(location = 0) in vec4 vertexPosition;
layout(location = 1) in vec2 vertexUV;

// Uniforms
uniform mat4 rotationMatrix; //Passing rotation
uniform mat4 transformMatrix; //Passing scale and position
uniform mat4 ortoMatrix; 

// Output data
out vec2 UV;

void main(){
   gl_Position.xyzw = ortoMatrix * (transformMatrix *
                        (rotationMatrix * vertexPosition));

   UV = vertexUV;
}