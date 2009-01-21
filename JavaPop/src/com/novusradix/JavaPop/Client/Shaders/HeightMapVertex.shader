varying vec3 N;

void main(void)
{
    vec4 v = gl_Vertex;
    gl_Position = gl_ModelViewProjectionMatrix * v;  
    N=gl_NormalMatrix*gl_Normal;
    gl_TexCoord[0].xy = gl_Vertex.xy;
}
