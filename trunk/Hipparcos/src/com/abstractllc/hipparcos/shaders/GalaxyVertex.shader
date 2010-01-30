attribute vec3 color;
uniform float exposure;

void main(void)
{
    vec4 v = gl_Vertex;
    vec3 p =  vec3(gl_ModelViewMatrix * v);
    float dist = length(p);    
    gl_Position = gl_ModelViewProjectionMatrix * v;
    gl_TexCoord[1].rgb = color*exposure/(dist*dist);
    gl_PointSize = 32.0;
    
}