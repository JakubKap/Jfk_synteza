import java.net.BindException.*;
import static java.lang.System.*;
import java.util.*;
import java.util.LinkedList;
import java.net.*;
import java.util.LinkedList.*;


import java.applet.*;
import javax.tools.*;
import java.net.Socket;
import java.net.BindException;
import static java.lang.System.*;
import javax.swing.UIDefaults.LazyInputMap;
import static java.lang.String.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter.*;
import com.github.javaparser.ast.visitor.GenericVisitor;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.PrintWriter;
import javax.swing.*;
import pl.edu.wat.Main;
import pl.edu.wat.Main.*;
import java.awt.Font.*;
import java.awt.Stroke.*;
import javax.swing.UIDefaults;
import java.awt.TextArea;
import java.awt.*;
import java.awt.color.*;
import java.awt.Stroke;
import javax.swing.border.AbstractBorder;
import java.awt.TextArea.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;




public final class Class {
    public static void main(String[] args) {
        String upper = upper("Kapusta Jakub");
        out.println(upper + " = " + count(upper));
    }

    private static String upper(String s) {
        String aux = s.toUpperCase();
        return aux;
    }

    public static int count(String s) {
        int length = s.length();
        return length;
    }

    private static void log(String str) {
        out.print((char) 27 + "[32m");
        out.println(str);
        out.print((char) 27 + "[0m");
    }
}