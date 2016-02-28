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


import com.testtubebaby.artemismain.annotations.CompSetter;

import java.util.Arrays;
import java.util.HashSet;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * Created by Malyom on 2015.07.15..
 */
class CompSetterMethod {
    String methodName;
    TypeNamePair[] params;
    String fullCompName;
    String simpleCompName;
    TypeElement element;
    private static String[] notAllowedMethodNames =
            {"refetchWholeTable", "newEntity", "editEntity",
            "clone", "equals", "build", "finalize", "getClass", "hashCode",
            "notify", "notifyAll", "toString", "wait"};

    CompSetterMethod(TypeElement comp) throws ProcessorException {
        element = comp;
        String superClass = comp.getSuperclass().toString();
        if(!superClass.equals("com.testtubebaby.artemismain.core.Component") && !superClass.equals("com.testtubebaby.artemismain.core.PooledComponent"))
            throw new ProcessorException("Classes annotated with @CompSetter must extend Component or PooledComponent!");
        if(!comp.getModifiers().contains(Modifier.PUBLIC))
            throw new ProcessorException("Classes annotated with @CompSetter must be public!");

        CompSetter annot = comp.getAnnotation(CompSetter.class);
        String[] annotParams = annot.params();

        // checking if parameter names are unique
        if((new HashSet<String>(Arrays.asList(annotParams)).size()) != annotParams.length)
            throw new ProcessorException("Parameter names in @CompSetter annotation are not unique!");

        // check if there are any empty parameter names
        for (String d : annotParams) {
            if(d.equals(""))
                throw new ProcessorException("One of the parameter names in the @CompSetter annotation is empty!");
        }



        params = new TypeNamePair[annotParams.length];


        if(params.length == 0) {
            Object[] varElems = comp.getEnclosedElements().stream().filter(p -> p.getKind() == ElementKind.FIELD).toArray();
            params = new TypeNamePair[varElems.length];
            for(int i = 0; i < params.length; i++) {
                params[i] = new TypeNamePair();
            }

            for(int i = 0; i < varElems.length; i++) {
                VariableElement varElem = (VariableElement)varElems[i];
                params[i].name = varElem.toString();
                params[i].type = varElem.asType().toString();
            }
        } else {
            for(int i = 0; i < params.length; i++) {
                params[i] = new TypeNamePair();
                params[i].name = annotParams[i];
            }

            Object[] varElems = comp.getEnclosedElements().stream().filter(p -> p.getKind() == ElementKind.FIELD).toArray();
            for(TypeNamePair e : params) {
                for(Object f : varElems) {
                    VariableElement varElem = (VariableElement)f;
                    if(e.name.equals(varElem.toString())){
                        e.type = varElem.asType().toString();
                        if(!varElem.getModifiers().contains(Modifier.PUBLIC))
                            throw new ProcessorException("The field %s must be public for it to be used in an EntityFactory!", varElem.asType().toString());
                        if(varElem.getModifiers().contains(Modifier.STATIC))
                            throw new ProcessorException("The field %s must not be static for it to be used in an EntityFactory!", varElem.asType().toString());
                        if(varElem.getModifiers().contains(Modifier.FINAL))
                            throw new ProcessorException("The field %s must not be final for it to be used in an EntityFactory!", varElem.asType().toString());
                        break;
                    }
                    throw new ProcessorException("The parameter '" + e.name + "' in the annotation @CompSetter has no pair in the component's fields!");
                }

            }
        }


        fullCompName = comp.getQualifiedName().toString();
        simpleCompName = comp.getSimpleName().toString();

        if((methodName = annot.methodName()).equals("")) {
            methodName = simpleCompName.substring(0,1).toLowerCase() + simpleCompName.substring(1);
        }

        for (String s : notAllowedMethodNames) {
            if(s.equals(methodName))
                throw new ProcessorException("Methodname " + methodName + " for @CompSetter is not allowed!");
        }


    }

    static class TypeNamePair {
        private String type = null;
        private String name = null;

        String getType() {
            return type;
        }

        String getName() {
            return name;
        }
    }

}
