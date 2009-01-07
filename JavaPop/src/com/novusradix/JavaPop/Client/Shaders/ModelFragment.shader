uniform sampler2D tex1;

void main(void)
{
    vec4 c = texture2D(tex1, gl_TexCoord[0].xy);    
    gl_FragColor.rgb = c.rgb * c.a + gl_Color.rgb*(1.0-c.a);
    gl_FragColor.a = 1.0;
    gl_FragDepth = gl_FragCoord.z;
}