uniform sampler2D tex1;

void main(void)
{
    gl_FragColor = texture2D(tex1, gl_TexCoord[0].xy);    
    gl_FragColor.rgb = gl_FragColor.rgb * gl_FragColor.a + gl_Color.rgb*(1.0-gl_FragColor.a);
    gl_FragDepth = gl_FragCoord.z;
}