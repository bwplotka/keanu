package io.improbable.keanu.codegen.python;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.javadoc.*;
import com.sun.tools.doclets.standard.Standard;
import io.improbable.keanu.annotation.ExportVertexToPythonBindings;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class KeanuProjectDoclet extends Standard {

    private static final String DESTINATION_FILE_NAME = "javadocstrings.json";
    private static final String READ_DESTINATION = "./build/resources/";
    private static final String WRITE_DESTINATION = "./codegen/build/resources/";

    public static boolean start(RootDoc root) {

        Map<String, DocString> docStrings = new HashMap<>();
        ClassDoc[] classes = root.classes();
        for (ClassDoc classDoc : classes) {
            for (ConstructorDoc constructorDoc : classDoc.constructors()) {
                if (constructorDoc.annotations().length != 0) {
                    if (isConstructorAnnotated(constructorDoc)) {
                        Map<String, String> params = ParamStringProcessor.getNameToCommentMapping(constructorDoc);
                        DocString docString = new DocString(constructorDoc.commentText(), params);
                        docStrings.put(constructorDoc.qualifiedName(), docString);
                    }
                }
            }
        }
        writeDocStringsToFile(docStrings);
        return true;
    }

    private static boolean isConstructorAnnotated(ConstructorDoc constructorDoc) {
        for (AnnotationDesc an: constructorDoc.annotations()) {
            String exportVertexAnnotationName = "@" + ExportVertexToPythonBindings.class.getName();
            if (an.toString().equals(exportVertexAnnotationName)) {
                return true;
            }
        }
        return false;
    }

    private static void writeDocStringsToFile(Map<String, DocString> docString) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(docString);
            File outputFile = new File(WRITE_DESTINATION + DESTINATION_FILE_NAME);
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile(); // if file already exists will do nothing
            OutputStream outputStream = new FileOutputStream(WRITE_DESTINATION + DESTINATION_FILE_NAME, false);
            System.out.println("Writing docstrings");
            outputStream.write(json.getBytes());
            System.out.println("Finished writing docstrings");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not write to file while processing JavaDoc strings");
        }
    }

    static Map<String, DocString> getDocStringsFromFile() throws IOException {
        try {
            Type listType = new TypeToken<Map<String, DocString>>(){}.getType();
            Gson gson = new Gson();
            Reader reader = new FileReader(READ_DESTINATION + DESTINATION_FILE_NAME);
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            throw new IOException("Could not read JavaDoc strings from file at " + System.getProperty("user.dir") + READ_DESTINATION + DESTINATION_FILE_NAME);
        }
    }
}
