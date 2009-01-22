uniform sampler2D tex1;
uniform sampler2D tiles;
varying vec3 N;


vec4 light()
{
    return gl_LightSource[0].ambient + 
        gl_LightSource[0].diffuse * max(dot(normalize(N),normalize(gl_LightSource[0].position.xyz)),0.0);
}

void main(void)
{
    vec2 texcoord = mod(gl_TexCoord[0].xy,1.0);
    texcoord *= 31.0/256.0; 
    texcoord += 0.5/256.0;
    vec2 tile = (255.0 * texture2D(tiles, gl_TexCoord[0].xy/128.0).xy)/8.0;
    texcoord.xy += tile.xy;
    gl_FragColor = light()*texture2D(tex1, texcoord);
    if(gl_FragColor.a <0.1)
        discard;
}