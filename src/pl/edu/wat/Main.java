package pl.edu.wat;

import com.github.javaparser.JavaParser;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.DotPrinter;

import javax.tools.*;
import java.io.*;
import java.util.*;

public class Main {

    private static LinkedList<ImportDeclaration> importNames = new LinkedList<>();
    private static LinkedList<Import> importStrings = new LinkedList<>();


    public static void sortImports(){

        //wysunięcie java na początek

        //sortowanie w podgrupach

        Set<String> uniqueFirstPart = new HashSet<>();
        LinkedList<Import> importNamesPom = new LinkedList<>();


        //znalezienie najdłuższego importu
        int max = 0;
        for(Import im : importStrings)
            if(im.importParts.size() > max)
                max = im.importParts.size();

    for(int i=0; i<max; i++) {

        for (int j = 0; j < importStrings.size(); j++) {
            if(importStrings.get(j).importParts.size() > i){
                uniqueFirstPart.add(importStrings.get(j).importParts.get(i));
                System.out.println("Added unique " + importStrings.get(j).importParts.get(i) + " for " + importStrings.get(j).toString());
            }

        }


        for (String s : uniqueFirstPart) { //1 wybrany początek
            System.out.println(s); //dla testu

            for (Import in : importStrings) {

                if (in.importParts.size() > i+1) {
                    if (in.importParts.get(i).equals(s)){
                        importNamesPom.add(in);
                    }
                }

            }

            for (Import im : importNamesPom)
                System.out.println(im.toString());

            final int inc = i;
            Collections.sort(importNamesPom, new Comparator<Import>() {
                @Override
                public int compare(Import i1, Import i2) {
                    return i1.importParts.get(inc+1).compareTo(i2.importParts.get(inc+1));
                }
            });

            //wstawienie posortowanego zbioru do oryginału
            int incI = 0;
            int incP = 0;

            for (Import in : importStrings) {
                if (in.importParts.size() > i+1) {
                    if(in.importParts.get(i).equals(s)) {
                        importStrings.set(incI, importNamesPom.get(incP));
                        incP++;
                    }
                }
                incI++;
            }


            importNamesPom.clear();
        }
        uniqueFirstPart.clear();
    }

    }

    public static Name returnFirstPart(ImportDeclaration id){
        Name firstPart = new Name("null");

        int numOfDots = 0;
        char symbol =' ';

            for (int i = 0; i < id.getNameAsString().length(); i++) {
                symbol = id.getNameAsString().charAt(i);
                if (symbol == '.')
                    numOfDots++;
            }

            firstPart = id.getName().getQualifier().get();

            for (int i = 0; i < numOfDots - 1; i++) {
                firstPart = firstPart.getQualifier().get();
            }
        return firstPart;
    }

    public static int findMinJava(){
        int index= -1;

        for(int i=0; i<importNames.size(); i++){
            if(importNames.get(i).getNameAsString().startsWith("java."))
                return i;
        }

            return index;
    }

    public static int findMinNonJava(){
        int index= -1;

        for(int i=0; i<importNames.size(); i++){
            if(!returnFirstPart(importNames.get(i)).equals("java"))
                return i;
        }

        return index;
    }
    /*
    public static LinkedList <ImportDeclaration> sortImports(LinkedList <ImportDeclaration> importNames){
        LinkedList<ImportDeclaration> javaBegin = new LinkedList<>();

        for(ImportDeclaration id : importNames){
            if(id.getNameAsString().startsWith("java."))
                javaBegin.add(id);
        }

        return javaBegin;
    }
*/


    public static void main(String[] args) throws IOException {
        final String fileName = "src\\Class.java";
        final String alteredFileName = "src\\ClassAltered.java";
        CompilationUnit cu;
        try (FileInputStream in = new FileInputStream(fileName)) {
            cu = JavaParser.parse(in);
        }

        DotPrinter printer = new DotPrinter(true);
        try (FileWriter fileWriter = new FileWriter("ast.dot");
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.print(printer.output(cu));
        }


        new ImportNamesCollector().visit(cu, null);


        /*importNames.forEach(n -> System.out.println("Import Name Collected: " + n.getName()+
                " ,Identifier = " + n.getName().getIdentifier()
                + " ,getChildNodes() = " + n.getChildNodes()
        + " ,getChildNodes().get(0) = " + n.getChildNodes().get(0)
        + " ,getQualifier.getIdentifier() = " + n.getName().getQualifier().get().getIdentifier()));
       // + " ,getName().getQualifier().get().getQualifier() = " + n.getName().getQualifier().get().getQualifier().get().getQualifier()));
*/

        //System.out.println("TESTTTT= " + importNames.get(2).getName().getQualifier().get().getQualifier().get().getQualifier().get().getIdentifier());


        /*for(int i=0; i<importNames.size(); i++)
        System.out.println("FirstValue dla " + importNames.get(i).getName() + " = " + returnFirstPart(importNames.get(i)));*/
/*
       Collections.sort(importNames, new Comparator<ImportDeclaration>() {
           @Override
           public int compare(ImportDeclaration o1, ImportDeclaration o2) {
                return returnFirstPart(o1).toString().compareTo(returnFirstPart(o2).toString());
           }
       });*/

    Collections.sort(importStrings, new Comparator<Import>() {
        @Override
        public int compare(Import i1, Import i2) {
            return i1.importParts.get(0).compareTo(i2.importParts.get(0));
        }
    });


/*
        int minJavaIndex = findMinJava();
        int minNonJavaIndex = findMinNonJava();

        System.out.println("minJavaIndex = " + minJavaIndex);
        System.out.println("minNonJavaIndex = " + minNonJavaIndex);


        for(int i=0; i<importNames.size(); i++){
            if(importNames.get(i).getNameAsString().startsWith("java.")) {
                Collections.swap(importNames, i, minNonJavaIndex);
                minNonJavaIndex++;
                System.out.println("success");
            }
        }

*/

        //for(int i=0)

       importNames.forEach(n -> System.out.println("Sorted import Name Collected: " + n));


        for(Import i : importStrings){
            System.out.println("Test of a new class = " + i.toString());
        }

        sortImports();
        for(int i=0; i<importStrings.size(); i++){
            //cu.setImport(i, importNames.get(i));
            //ImportDeclaration id = new ImportDeclaration()
            cu.setImport(i, new ImportDeclaration(new Name(importStrings.get(i).toString()), importStrings.get(i).isStatic, importStrings.get(i).isAsterisk));
            //cu.addImport("java.util" + i, true, true);
        }
        /*for(int i=0; i<importNames.size(); i++){
            //cu.setImport(i, importNames.get(i));
            //cu.getImport(i).remove();
            cu.addImport("java.util" + i, true, true);
        }*/



        cu.getClassByName("Class").get().setName("ClassAltered");
        try(FileWriter output = new FileWriter(new File(alteredFileName), false)) {
            output.write(cu.toString());
        }

        File[] files = {new File(alteredFileName)};
        String[] options = { "-d", "out//production//Synthesis" };

        //importNames.forEach(n -> System.out.println("LinkedList after change " + n));

    }

    //zapisanie do kolekcji wszystkich ImportDeclaration
    private static class ImportNamesCollector extends VoidVisitorAdapter<Void>{
        @Override
        public void visit(ImportDeclaration id, Void arg){
            super.visit(id, null);
            importNames.add(id);
            importStrings.add(new Import(id, id.isStatic(), id.isAsterisk()));
        }

    }
}
