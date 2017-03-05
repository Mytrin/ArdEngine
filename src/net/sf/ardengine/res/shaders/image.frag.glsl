#version 330 core

// Interpolated values from the vertex shaders
in vec2 UV;

// Uniforms
uniform sampler2D imageTexture;
uniform float transparency; //<0;1>
uniform vec3 coloring;

// Ouput data
out vec4 color;

void main(){
  color = texture(imageTexture, UV);

  color = vec4(color.r*coloring.r, color.g*coloring.g, color.b*coloring.b, color.a);

  //DEBUG
  //color = vec4(color.r, color.g, color.b, color.a);

    if(color.a > transparency){
	   color.a = transparency;
    }

  //DEBUG
  //color = vec4(1,1,1,1);
}