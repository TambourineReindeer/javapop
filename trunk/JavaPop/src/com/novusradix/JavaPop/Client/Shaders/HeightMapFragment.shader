uniform sampler2D tex1;
uniform sampler2D tiles;
varying vec3 N;


vec4 light()
{
    return gl_LightSource[1].ambient + 
        gl_LightSource[1].diffuse * max(dot(N,gl_LightSource[1].position.xyz),0.0);
}

void main(void)
{
    vec2 texcoord = mod(gl_TexCoord[0].xy,1.0);
    texcoord *= 0.12109375; 
    texcoord += 0.00195313;
    vec2 tile = texture2D(tiles, gl_TexCoord[0].xy/128.0).xy;
    texcoord.xy += tile.xy;
    gl_FragColor = light()*texture2D(tex1, texcoord);
}