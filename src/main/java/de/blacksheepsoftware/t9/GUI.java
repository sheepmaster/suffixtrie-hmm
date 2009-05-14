package de.blacksheepsoftware.t9;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JTextArea;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class GUI extends JApplet implements ActionListener {

    protected JTextArea textArea;
    
    protected Text text;
    
    protected class NumberKeyListener implements ActionListener {

        protected NumberKey numberKey;
        
        public NumberKeyListener(NumberKey numberKey) {
            this.numberKey = numberKey;
        }
        
        public void actionPerformed(ActionEvent e) {
            text.insertNumberKey(numberKey);
            update();
        }
        
    }
    
    protected void update() {
        text.check();
        textArea.setText(text.getContents());
        int cursorStart = text.getCursorStart();
        int cursorEnd = text.getCursorEnd();
        if (cursorStart == cursorEnd) {
            textArea.setCaretPosition(cursorStart);
        } else {
            textArea.setSelectionStart(cursorStart);
            textArea.setSelectionEnd(cursorEnd);
        }
        textArea.requestFocusInWindow();
//        System.err.println("text: \""+text.getContents()+"\"; wordSelected: "+text.wordSelected+"; activeWord: "+text.activeWord);
    }
    
    public void init() {
        final String modelName = getParameter("model");

        try {
            InputStream stream;
            try {
                stream = new URL(getDocumentBase(), modelName).openStream();
            } catch (IOException e) {
                stream = getClass().getResourceAsStream(modelName);
            }
            
            if (stream == null) {
                System.err.println("Couldn't load model \""+modelName+"\"");
                return;
            }
            
            final ObjectInputStream ois = new ObjectInputStream(stream);
            
            final Model model = (Model)ois.readObject();
            ois.close();
            
            text = new Text(model);
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        
        
        
        textArea = new JTextArea(10, 5);
        textArea.setLineWrap(true);
//        textArea.setEditable(false);
        
        setLayout(new BorderLayout());
        add(textArea, BorderLayout.NORTH);
        Container c = new Container();
        add(c, BorderLayout.SOUTH);
        c.setLayout(new GridLayout(5, 3));
        
        c.add(new JButton("")).setEnabled(false);
        
        JButton bUp = new JButton("\u2191");
        bUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                text.moveUp();
                update();
            }
        });
        c.add(bUp);
        
        JButton bDel = new JButton("\u232b");
        bDel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                text.deleteChar();
                update();
            }
        });
        c.add(bDel);
        
        JButton bLeft = new JButton("\u2190");
        bLeft.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                text.moveLeft();
                update();
            }
        });
        c.add(bLeft);
        
        JButton bDown = new JButton("\u2193");
        bDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                text.moveDown();
                update();
            }
        });
        c.add(bDown);
        
        JButton bRight = new JButton("\u2192");
        bRight.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                text.moveRight();
                update();
            }
        });
        c.add(bRight);
        
        
        JButton b1 = new JButton("_");
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                text.insertChar(' ');
                update();
            }  
        });
        c.add(b1);
        
        final JButton b2 = new JButton("abc");
        b2.addActionListener(new NumberKeyListener(new NumberKey(2)));
        c.add(b2);
        final JButton b3 = new JButton("def");
        b3.addActionListener(new NumberKeyListener(new NumberKey(3)));
        c.add(b3);
        final JButton b4 = new JButton("ghi");
        b4.addActionListener(new NumberKeyListener(new NumberKey(4)));
        c.add(b4);
        final JButton b5 = new JButton("jkl");
        b5.addActionListener(new NumberKeyListener(new NumberKey(5)));
        c.add(b5);
        final JButton b6 = new JButton("mno");
        b6.addActionListener(new NumberKeyListener(new NumberKey(6)));
        c.add(b6);
        final JButton b7 = new JButton("pqrs");
        b7.addActionListener(new NumberKeyListener(new NumberKey(7)));
        c.add(b7);
        final JButton b8 = new JButton("tuv");
        b8.addActionListener(new NumberKeyListener(new NumberKey(8)));
        c.add(b8);
        final JButton b9 = new JButton("wxyz");
        b9.addActionListener(new NumberKeyListener(new NumberKey(9)));
        c.add(b9);
    }

    
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        System.err.println(e);
    }

}
