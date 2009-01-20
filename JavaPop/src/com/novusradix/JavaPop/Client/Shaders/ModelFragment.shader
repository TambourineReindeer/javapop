uniform sampler2D tex1;
uniform vec4 color;
varying vec3 N;
varying vec3 v;


void main(void)
{
    vec4 c = texture2D(tex1, gl_TexCoord[0].xy);  
    gl_FragColor.rgba = c.rgba * c.a + color.rgba*(1.0-c.a);
}