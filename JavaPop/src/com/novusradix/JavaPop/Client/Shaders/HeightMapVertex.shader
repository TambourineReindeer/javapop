uniform sampler2D height;
void main(void)
{
    vec4 v = gl_Vertex;
    
    if(v.x != floor(v.x))
    {
vec4 h;
h.x = texture2D(height, v.xy/128.0 + vec2(1.0/256.0,1.0/256.0)).x*256.0;
h.y = texture2D(height, v.xy/128.0 + vec2(-1.0/256.0,1.0/256.0)).x*256.0;
h.z = texture2D(height, v.xy/128.0 + vec2(1.0/256.0,-1.0/256.0)).x*256.0;
h.w = texture2D(height, v.xy/128.0 + vec2(-1.0/256.0,-1.0/256.0)).x*256.0;

        v.z = (h.x+h.y+h.z+h.w)/4.0;
    }else{
        v.z = texture2D(height,v.xy/128.0).x * 256.0;
    }
v.z*=0.5;
    gl_Position = gl_ModelViewProjectionMatrix * v;  
    gl_TexCoord[0].xy = gl_Vertex.xy;
    gl_TexCoord[1].xy = gl_Vertex.xy;
}
