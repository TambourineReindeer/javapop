varying vec3 N;
varying vec3 v;

void main(void)
{
    vec4 v = gl_Vertex;
    gl_Position = gl_ModelViewProjectionMatrix * v;  
    gl_TexCoord[0].xy = gl_MultiTexCoord0.xy;
    gl_TexCoord[1].xy = gl_MultiTexCoord1.xy;
    v = gl_ModelViewMatrix * v;
    N = gl_NormalMatrix * gl_Normal;
}
