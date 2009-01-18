package javapoptools;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author gef
 */
public class ModelData {
    public int triangleCount;
    public FloatBuffer vertices;    //vertex data, interleaved vertex, normals, and texcoords, if included.
    public IntBuffer indices;       //index data, or null if vertices are to be rendered in an array
    float radius;                   //Maximum distance from the origin, used for whole object viewport clipping.
    boolean normals;                //Set if vertices have normals
    boolean texcoords;              //Set if vertices have texture coordinates
    
    public int getVertexStride()
    {
        return 3+(normals?3:0)+(texcoords?2:0);
    }
    
    public int getNormalOffset()
    {
        return 3;
    }
    
    public int getTexCoordOffset()
    {
        return 3+(normals?3:0);
    }
    
    public boolean isIndexed()
    {
        return (!(indices==null));
    }
}
