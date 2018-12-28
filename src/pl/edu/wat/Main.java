package pl.edu.wat;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import javax.tools.*;
import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        final String fileName = "src\\Class.java";
        final String alteredFileName = "src\\ClassAltered.java";
        CompilationUnit cu;
        try (FileInputStream in = new FileInputStream(fileName)) {
            cu = JavaParser.parse(in);
        }

        List<String> importNames = new ArrayList<>();
        new ImportNamesCollector().visit(cu, importNames);
        importNames.forEach(n -> System.out.println("Import Name Collected: " + n));

    }

    //zapisanie do kolekcji nazw importów
    private static class ImportNamesCollector extends VoidVisitorAdapter<List<String>>{
        public void visit(ImportDeclaration id, List<String> collector){
            super.visit(id, collector);
            collector.add(id.getNameAsString());
        }


    }
}
