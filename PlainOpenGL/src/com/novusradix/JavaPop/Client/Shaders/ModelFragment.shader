uniform sampler2D tex1;

void main(void)
{
    gl_FragColor = texture2D(tex1, gl_TexCoord[0].xy);
    if(gl_FragColor.b == 1.0)
    {
        gl_FragColor.rgb = gl_Color.rgb;

    }
    gl_FragDepth = gl_FragCoord.z;
}