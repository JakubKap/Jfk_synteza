package pl.edu.wat;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.DotPrinter;


import javax.tools.*;
import java.io.*;
import java.util.*;

public class Main {

    private static LinkedList<ImportDeclaration> importNames = new LinkedList<>();
    private static LinkedList<Import> importStrings = new LinkedList<>();
    private static int firstNonJavaIndex = -1;
    private static int firstStaticIndex = -1;

    public static void sortImports() {

        //posortowanie po pierwszym członie

        Collections.sort(importStrings, new Comparator<Import>() {
            @Override
            public int compare(Import i1, Import i2) {
                return i1.importParts.get(0).compareTo(i2.importParts.get(0));
            }
        });

        //sortowanie w podgrupach

        Set<String> uniqueFirstPart = new HashSet<>();
        LinkedList<Import> importNamesPom = new LinkedList<>();
        LinkedList<Import> importNamesRest = new LinkedList<>();


        //znalezienie najdłuższego importu
        int max = 0;
        for (Import im : importStrings)
            if (im.importParts.size() > max)
                max = im.importParts.size();

        for (int i = 0; i < max; i++) {

            for (int j = 0; j < importStrings.size(); j++) {
                if (importStrings.get(j).importParts.size() > i) {
                    uniqueFirstPart.add(importStrings.get(j).importParts.get(i));
                    System.out.println("Added unique " + importStrings.get(j).importParts.get(i) + " for " + importStrings.get(j).toString());
                }

            }


            for (String s : uniqueFirstPart) { //1 wybrany początek
                System.out.println(s); //dla testu

                for (Import in : importStrings) {

                    if (in.importParts.size() > i + 1) {
                        if (in.importParts.get(i).equals(s)) {
                            importNamesPom.add(in);
                        }
                    } else if ((in.importParts.size() == i + 1)) {
                        if (in.importParts.get(i).equals(s)) {
                            importNamesRest.add(in);
                            System.out.println("success");
                        }
                    }

                }

                for (Import im : importNamesPom)
                    System.out.println(im.toString());

                final int inc = i;
                Collections.sort(importNamesPom, new Comparator<Import>() {
                    @Override
                    public int compare(Import i1, Import i2) {
                        return i1.importParts.get(inc + 1).compareToIgnoreCase(i2.importParts.get(inc + 1));
                    }
                });

                //wstawienie posortowanego zbioru do oryginału
                int incI = 0;
                int incP = 0;

                for (Import in : importStrings) {
                    if (in.importParts.size() > i + 1) {
                        if (in.importParts.get(i).equals(s)) {
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


        importNamesRest.forEach(n -> System.out.println("Wartość importNamesRest =  " + n));

        int index = 0;
        for (Import im : importNamesRest) {
            for (int i = 0; i < importStrings.size(); i++) {
                if (importStrings.get(i).toString().startsWith(im.toString()) &&
                        im.importParts.size() < importStrings.get(i).importParts.size()) {

                    System.out.println("Znaleziona wartość w posortowanych importach = " + importStrings.get(i) + " dla początku = " + im.toString() + "\n");
                    for (int j = 0; j < importStrings.size(); j++) {
                        if (importStrings.get(j).toString().equals(im.toString()))
                            index = j;
                    }

                    if (i > 0) {
                        importStrings.remove(index);
                        importStrings.add(i, im);
                    } else {
                        importStrings.remove(index);
                        importStrings.add(0, im);
                        //importStrings.remove(importStrings.indexOf(im));
                    }

                    System.out.println("\nZawartość zbioru po zmianie: ");
                    importStrings.forEach(n -> System.out.println("Wartość importStrings =  " + n));


                    //jak został znaleziony i zamieniony, to przechodzi do następnego "początku"
                    i = importStrings.size();
                }
            }
        }



        //przesunięcie java na samą górę

        int minJavaIndex = findMinJava();
        int minNonJavaIndex = findMinNonJava();

        System.out.println("minJavaIndex before = " + minJavaIndex);
        System.out.println("minNonJavaIndex before = " + minNonJavaIndex);



        if(minJavaIndex>=0 && minNonJavaIndex>=0 && minNonJavaIndex < minJavaIndex) {
            for (int i = 0; i < importStrings.size(); i++) {
                if (importStrings.get(i).toString().startsWith("java") && !importStrings.get(i).toString().startsWith("javax")) {
                    if(minNonJavaIndex < importStrings.size()) {
                        Collections.swap(importStrings, i, minNonJavaIndex);
                        minNonJavaIndex++;

                        importStrings.forEach(n-> System.out.println("After swap :" + n));
                    }
                    System.out.println("success");
                }
            }
        }

        System.out.println("Przed końcowym sprzątaniem");
        importStrings.forEach(n -> System.out.println(n));


        //ponowne przesortowanie zbioru (bez namespace - bez java.)

        //znalezienie najdłuższego importu
        max = 0;
        uniqueFirstPart.clear();
        importNamesPom.clear();
        importNamesRest.clear();

        for(Import im : importStrings)
            if(im.importParts.size() > max )//&& !im.importParts.get(0).equals("java"))
                max = im.importParts.size();

        System.out.println("max = " +max);

        for(int i=0; i<max; i++) {

            for (int j = 0; j < importStrings.size(); j++) {
                if(importStrings.get(j).importParts.size() > i){
                    uniqueFirstPart.add(importStrings.get(j).importParts.get(i));
                    System.out.println("Sort 2 - Added unique " + importStrings.get(j).importParts.get(i) + " for " + importStrings.get(j).toString());
                }

            }


            for (String s : uniqueFirstPart) { //1 wybrany początek
                System.out.println("Drugi sort = " + s); //dla testu

                for (Import in : importStrings) {

                    if (in.importParts.size() > i+1) {
                        if (in.importParts.get(i).equals(s)){
                            importNamesPom.add(in);
                        }
                    }
                    else if((in.importParts.size() == i+1)){
                        if (in.importParts.get(i).equals(s)){
                            importNamesRest.add(in);
                            System.out.println("success");
                        }
                    }

                }

                for (Import im : importNamesPom)
                    System.out.println("Drugi sort = " + im.toString());

                final int incr = i;
                Collections.sort(importNamesPom, new Comparator<Import>() {
                    @Override
                    public int compare(Import i1, Import i2) {
                        return i1.importParts.get(incr+1).compareToIgnoreCase(i2.importParts.get(incr+1));
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


        if(minNonJavaIndex > 0)
            firstNonJavaIndex = minNonJavaIndex;
        System.out.println("minJavaIndex after = " + minJavaIndex);
        System.out.println("minNonJavaIndex after = " + minNonJavaIndex);


        //wstawienie na końcu static

        LinkedList<Import> staticImports = new LinkedList<>();

        int inc=0;

        for (int i = 0; i < importStrings.size(); i++) {
            if (importStrings.get(i).isStatic) {
                staticImports.add(inc, importStrings.get(i));
                inc++;
            }
        }

        if (staticImports.size() > 0) {

            for (Import im : staticImports) {
                importStrings.add(importStrings.size(), im);

            }

        }

        int numOfStaticImports = staticImports.size();

        if(staticImports.size()>0){
            for(Import im : staticImports){
                importStrings.remove(im);
            }

        }


        if(numOfStaticImports > 0) firstStaticIndex=importStrings.size() - numOfStaticImports;

        //poprawki

        importStrings.forEach(n-> System.out.println("Przed poprawkami = " + n));

        for(int i=0; i<importStrings.size(); i++){
            if((i+1) < importStrings.size()){
                if((importStrings.get(i).toString().toLowerCase().equals(importStrings.get(i+1).toString().toLowerCase()))
                        || (importStrings.get(i).toString().startsWith(importStrings.get(i+1).toString()))
                        || (importStrings.get(i+1).toString().startsWith(importStrings.get(i).toString()))){

                    System.out.println("first case = " + importStrings.get(i).orgImportDeclaration + " ,second case = " + importStrings.get(i + 1).orgImportDeclaration);

                    if(((!importStrings.get(i+1).toString().startsWith(importStrings.get(i).toString())
                            || (importStrings.get(i).orgImportDeclaration.getNameAsString().equals(importStrings.get(i+1).orgImportDeclaration.getNameAsString()) && importStrings.get(i).isAsterisk && !importStrings.get(i+1).isAsterisk))))
                        //||(importStrings.get(i).equals(importStrings.get(i+1)) && importStrings.get(i).isAsterisk && !importStrings.get(i+1).isAsterisk))))
                        Collections.swap(importStrings, i, i+1);
                }

            }
            firstNonJavaIndex=findMinNonJava();
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

        for(int i=0; i<importStrings.size(); i++){
            if(importStrings.get(i).toString().startsWith("java") && !importStrings.get(i).toString().startsWith("javax"))
                return i;
        }

        return index;
    }

    public static int findMinNonJava(){
        int index= -1;

        for(int i=0; i<importNames.size(); i++){
            if(!importStrings.get(i).toString().startsWith("java") || importStrings.get(i).toString().startsWith("javax"))
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

        importNames.forEach(n -> System.out.println("Sorted import Name Collected: " + n.toString()));


        for(Import i : importStrings){
            System.out.println("Test of a new class = " + i.toString());
        }

        sortImports();
        //importStrings.forEach(n-> System.out.println(n));

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

            if(firstNonJavaIndex>=0 && firstStaticIndex>0) {
                output.write(cu.toString().replace(importStrings.get(firstNonJavaIndex).orgImportDeclaration.toString(), "\n" + importStrings.get(firstNonJavaIndex).orgImportDeclaration.toString()).replace(importStrings.get(firstStaticIndex).orgImportDeclaration.toString(), "\n" + importStrings.get(firstStaticIndex).orgImportDeclaration.toString()));
            }


            else if(firstNonJavaIndex>=0 && firstStaticIndex<0)
                output.write(cu.toString().replace(importStrings.get(firstNonJavaIndex).orgImportDeclaration.toString(), "\n" + importStrings.get(firstNonJavaIndex).orgImportDeclaration.toString()));

                else if(firstNonJavaIndex<0 && firstStaticIndex>0)
                output.write(cu.toString().replace(importStrings.get(firstStaticIndex).orgImportDeclaration.toString(), "\n" + importStrings.get(firstStaticIndex).orgImportDeclaration.toString()));

                 else
                    output.write(cu.toString());
        }

        File[] files = {new File(alteredFileName)};
        String[] options = { "-d", "out//production//Synthesis" };

        //importNames.forEach(n -> System.out.println("LinkedList after change " + n));

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            Iterable<? extends JavaFileObject> compilationUnits =
                    fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));
            compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    Arrays.asList(options),
                    null,
                    compilationUnits).call();

            diagnostics.getDiagnostics().forEach(d -> System.out.println(d.getMessage(null)));
        }

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

    //wstawienie nowej linii po sekcji z java.
    private static class InsertSpace extends VoidVisitorAdapter<Void>{
        @Override
        public void visit(ImportDeclaration id, Void arg){
            super.visit(id, null);

            //MethodReferenceExpr met = new MethodReferenceExpr()

        }

    }

}