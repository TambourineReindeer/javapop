uniform sampler2D tex;
uniform float exposure;

void main()
{
    vec4 c=texture2D(tex,gl_TexCoord[0].st);
    if(c.g*exposure >1)
    {
        gl_FragColor = c;
    }
    else
    {
        gl_FragColor = vec3(0.0,0.0,0.0);
    }
}