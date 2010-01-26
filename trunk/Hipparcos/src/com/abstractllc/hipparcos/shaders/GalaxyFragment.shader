
void main()
{
    gl_FragColor.rgb = gl_TexCoord[0].rgb;
    gl_FragColor.a = 0.9;
}