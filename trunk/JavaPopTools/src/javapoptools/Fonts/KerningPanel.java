package javapoptools.Fonts;

import com.novusradix.JavaPop.Client.GLText;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

/**
 *
 * @author gef
 */
public class KerningPanel extends GLCanvas implements GLEventListener, MouseListener, KeyListener {

    GLText text;
    private String testString;
    private PageType currentpage;
    private char currentChar;
    private char[] row,  column;
    private static final char[] alpha = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final char[] numeric = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char[] symbols = new char[]{',', '.', '!', '?'};
    private static final char[] all;


    static {
        all = new char[alpha.length + numeric.length + symbols.length];
        System.arraycopy(alpha, 0, all, 0, alpha.length);
        System.arraycopy(numeric, 0, all, alpha.length, numeric.length);
        System.arraycopy(symbols, 0, all, alpha.length + numeric.length, symbols.length);
    }

    public enum PageType {

        ALPHA, NUMERIC, SYMBOLS, CHARACTER
    };

    KerningPanel(GLCapabilities caps) {
        super(caps);
        addGLEventListener(this);
        addMouseListener(this);
        addKeyListener(this);
        text = new GLText();
        testString = "The Quick Brown JavaPop";

        row = alpha;
        column = alpha;
        currentpage = PageType.ALPHA;
        currentChar ='Q';
    }

    public void init(GLAutoDrawable glad) {
        GL gl = glad.getGL();
        text.init(gl);
    }

    public void display(GLAutoDrawable glad) {
        GL gl = glad.getGL();
        gl.glClearColor(0.1f, 0.1f, 0.5f, 0.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        StringBuffer sb = new StringBuffer();
        sb.setLength(2);



        for (int a = 0; a < row.length; a++) {
            sb.setCharAt(0, row[a]);

            for (int b = 0; b < column.length; b++) {
                sb.setCharAt(1, column[b]);
                String s = sb.toString();
                text.drawString(gl, s, (float) a / row.length, (float) b / column.length * 0.95f, 0.04f);
            }
        }



        text.drawString(gl, testString, 0, 0.95f, 0.05f);
    }

    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
    }

    public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
    }

    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        int a, b;
        a = row.length * p.x / getWidth();
        b = (int) (column.length * (1.0f - 1.0f*p.y / getHeight())/0.95f);

        if (a < row.length && b < column.length) {

            if (e.getButton() == MouseEvent.BUTTON3 || e.getModifiersEx() == MouseEvent.CTRL_DOWN_MASK) {
                text.increaseKern(row[a], column[b]);
            } else {
                text.decreaseKern(row[a], column[b]);
            }
        }
    }

    public void mousePressed(MouseEvent arg0) {
    }

    public void mouseReleased(MouseEvent arg0) {
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 's':
                text.saveKerning();
                break;
            default:


        }
    }

    public void keyPressed(KeyEvent arg0) {
    }

    public void keyReleased(KeyEvent arg0) {
    }

    void setText(String text) {
        testString = text;
    }

    void setPage(PageType p) {
        currentpage = p;
        setRowAndColumn();
    }

    void setCharacter(char c) {
        currentChar = c;
        setRowAndColumn();
    }

    private void setRowAndColumn() {
        switch (currentpage) {
            case ALPHA:
                row = column = alpha;
                break;
            case NUMERIC:
                row = column = numeric;
                break;
            case SYMBOLS:
                row = alpha;
                column = symbols;
                break;
            case CHARACTER:
                row = all;
                column = new char[]{currentChar};
                break;
        }
    }
    public void setRowAndColumn(String r, String c)
    {
        row = r.toCharArray();
        column = c.toCharArray();
    }
}
