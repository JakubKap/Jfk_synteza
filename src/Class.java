import static java.lang.System.*;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.FilerException;

import javax.annotation.processing.SupportedAnnotationTypes.*;
import javax.swing.UIDefaults.LazyInputMap;
import static java.lang.System.*;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.*;
import java.io.FileInputStream.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.*;

import java.awt.Component;
import java.awt.*;
import java.awt.Color;
import java.awt.TextArea;
import javax.swing.*;
import javax.swing.UIDefaults;
import com.github.javaparser.TokenRange;
import com.github.javaparser.*;
import java.awt.Component.BaselineResizeBehavior;

import java.awt.Color.*;

import java.lang.String.*;
import java.lang.String;

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