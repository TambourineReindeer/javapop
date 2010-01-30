uniform sampler2D tex;

void main()
{
    gl_FragColor.rgb = gl_TexCoord[1].rgb * texture2D(tex,gl_TexCoord[0].st).a;
    gl_FragColor.a = 1.0;
}