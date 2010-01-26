uniform sampler2D tex;
uniform float texelX, texelY;
void main()
{
    vec2 coord1 = gl_TexCoord[0].st+vec2(-texelX/4.0, -texelY/4.0);
    vec2 coord2 = gl_TexCoord[0].st+vec2(-texelX/4.0, texelY/4.0);
    vec2 coord3 = gl_TexCoord[0].st+vec2(texelX/4.0, -texelY/4.0);
    vec2 coord4 = gl_TexCoord[0].st+vec2(texelX/4.0, texelY/4.0);


    gl_FragColor= (texture2D(tex,coord1)+texture2D(tex,coord2)+texture2D(tex,coord3) +texture2D(tex,coord4))/4.0;
    
}