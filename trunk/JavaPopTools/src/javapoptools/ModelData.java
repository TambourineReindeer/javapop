package javapoptools;

import com.novusradix.JavaPop.Math.Matrix4;
import com.sun.opengl.util.BufferUtil;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author gef
 */
public class ModelData implements Serializable {

    public int triangleCount;
    public FloatBuffer vertices;    //vertex data, interleaved vertex, normals, and texcoords, if included.
    public IntBuffer indices;       //index data, or null if vertices are to be rendered in an array
    float radius;                   //Maximum distance from the origin, used for whole object viewport clipping.
    boolean normals;                //Set if vertices have normals
    boolean texcoords;              //Set if vertices have texture coordinates
    Matrix4 transform;              //Object space transform

    public int getVertexStride() {
        return 3 + (normals ? 3 : 0) + (texcoords ? 2 : 0);
    }

    public int getNormalOffset() {
        return 3;
    }

    public int getTexCoordOffset() {
        return 3 + (normals ? 3 : 0);
    }

    public boolean isIndexed() {
        return (!(indices == null));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(triangleCount);
        out.writeFloat(radius);
        out.writeBoolean(normals);
        out.writeBoolean(texcoords);
        out.writeObject(transform);
        out.writeBoolean(vertices != null);
        if (vertices != null) {
            float[] fs = new float[vertices.limit()];
            vertices.get(fs);
            out.writeObject(fs);
        }
        out.writeBoolean(indices != null);
        if (indices != null) {

            int[] is = new int[indices.limit()];
            indices.get(is);
            out.writeObject(is);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        triangleCount = in.readInt();
        radius = in.readFloat();
        normals = in.readBoolean();
        texcoords = in.readBoolean();
        transform = (Matrix4) in.readObject();
        
        if (in.readBoolean())
        {
            float fs[] = (float[]) in.readObject();
            vertices = BufferUtil.newFloatBuffer(fs.length);
            vertices.put(fs);
            vertices.flip();
        }
        if (in.readBoolean())
        {
            int is[] = (int[]) in.readObject();
            indices = BufferUtil.newIntBuffer(is.length);
            indices.put(is);
            indices.flip();
        }
                
    
    }
}
