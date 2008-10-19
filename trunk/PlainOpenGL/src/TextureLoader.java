import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;

public class TextureLoader {

	public static void Load(String file,GL gl) {
		ByteBuffer buf;
		BufferedImage img;
		URL u;
		
		try {
			//u = new URL("file", "localhost","file://Users/mom/Documents/workspace/PlainOpenGL/bin/tex.png");
			u = new URL("file", "localhost","/tex.png");
		try {
			img = ImageIO.read(u);

			byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
			buf = ByteBuffer.allocateDirect(data.length);
			buf.order(ByteOrder.nativeOrder());
			buf.put(data, 0, data.length);
			buf.flip();
			
			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA8, 256, 256, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
