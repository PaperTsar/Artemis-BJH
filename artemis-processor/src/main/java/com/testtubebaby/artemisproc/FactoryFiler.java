/*
* Copyright (C) 2015 Bendegúz Nagy
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.testtubebaby.artemisproc;

import java.io.BufferedWriter;
import java.io.IOException;

import javax.annotation.processing.Filer;


class FactoryFiler {
    private static final String mainLib = "com.testtubebaby.artemismain.core.";
    private static final String factPack = "com.testtubebaby.artemismain.genfacts";
    private BufferedWriter out;


    void fileFactory(FactoryData factory, Filer filer) {
        try{
            out = new BufferedWriter(filer.createSourceFile(factory.newFactName).openWriter());
            String[] compos = factory.compos.toArray(new String[0]);
            String[] simpleCompos = factory.simpleCompos.toArray(new String[0]);
            CompSetterMethod[] methods = factory.methods.toArray(new CompSetterMethod[0]);
            String newFactName = factory.newFactName;

            StringBuilder archBuilder = new StringBuilder();
            archBuilder.append(String.format("new ArchetypeBuilder(sysData)"));
            for (String e : compos) {
                archBuilder.append(String.format(".with(%s.class)", e));
            }
            archBuilder.append(".build()");



            // package, imports, class beginning
            output("package %s;", factPack);
            output("");
            output("import %sArchetypeBuilder;", mainLib);
            output("import %sSystemsData;", mainLib);
            output("import %sEntityFactory;", mainLib);
            output("");
            output("public class %s extends EntityFactory {", newFactName);
            for (int i = 0; i < compos.length; i++) {
            output("    private int %sIndex;", simpleCompos[i]);
            } // here ends package, imports, class declaration creation

            // constructor
            output("    ");
            output("    public %s(SystemsData sysData) {", newFactName);
            output("        super(sysData, %s);", archBuilder.toString());
            for(int i = 0; i < compos.length; i++) {
            output("        %sIndex = super.getCompIndex(%s.class);", simpleCompos[i], compos[i]);
            }
            output("        super.init();");
            output("    }"); // here end the constructor creation

            // the createEntity method
            output("    ");
            output("    @Override");
            output("    public %s newEntity() {", newFactName);
            output("        currId = world.createEntity(arch);");
            output("        return this;");
            output("    }"); // here ends createEntity method creation

            // the editEntity method
            output("    ");
            output("    @Override");
            output("    public %s editEntity(int entityId) {", newFactName);
            output("        currId = entityId;");
            output("        return this;");
            output("    }"); // here ends editEntity method creation

            // the methods
            output("    ");
            for (CompSetterMethod e : methods) {
                StringBuilder methodParamSign = new StringBuilder();
                for (CompSetterMethod.TypeNamePair f : e.params) {
                    if(methodParamSign.length() != 0)
                        methodParamSign.append(", ");
                    methodParamSign.append(String.format("%s %s", f.getType(), f.getName()));

                }

            output("    public %s %s(%s) {", newFactName, e.methodName, methodParamSign.toString());
                output("        %s tempComp = (%s)compTable[%sIndex][currId];", e.fullCompName, e.fullCompName, e.simpleCompName);
            for (CompSetterMethod.TypeNamePair f : e.params) {
            output("        tempComp.%s = %s;", f.getName(), f.getName());
            }
            output("        return this;");
            output("    }");
            output("    ");

            } // here ends the method creation


            // finishing up
            output("}");


            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void output(String msg, Object... args) throws IOException {
        out.write(String.format(msg + "%n", args));
    }
}
