uniform sampler2D tex;
uniform float exposure;

void main()
{
vec4 c=texture2D(tex,gl_TexCoord[0].st)*exposure;
    gl_FragColor = c;
}