uniform sampler2D tex1;
uniform sampler2D tile;

void main(void)
{
    vec4 t = texture2D(tile, gl_TexCoord[0].xy / 128.0);
    vec2 texcoord = gl_TexCoord[0].xy - floor(gl_TexCoord[0].xy);
texcoord = texcoord*0.125;
texcoord.x += t.r*32.0;
    gl_FragColor = texture2D(tex1, texcoord);
    gl_FragDepth = gl_FragCoord.z;


  

}