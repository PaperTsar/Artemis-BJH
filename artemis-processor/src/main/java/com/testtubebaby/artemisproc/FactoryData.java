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


import com.testtubebaby.artemismain.annotations.Bind;
import com.testtubebaby.artemismain.core.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Created by Malyom on 2015.07.14..
 */
class FactoryData {
    List<String> compos = new ArrayList<>();
    List<String> simpleCompos = new ArrayList<>();
    Set<CompSetterMethod> methods = new HashSet<>();
    String newFactName;
    TypeElement element;

    FactoryData(TypeElement factory, List<CompSetterMethod> compSetters ) throws ProcessorException {
        element = factory;

        if(factory.getKind() != ElementKind.INTERFACE)
            throw new ProcessorException("Only interfaces can be annotated with @Bind!");


        newFactName = factory.getSimpleName().toString() + "Impl";


        try {
            for(Class<? extends Component> e : factory.getAnnotation(Bind.class).value()) {
                String name = e.getCanonicalName();
                compos.add(name);
                simpleCompos.add(name.substring(name.lastIndexOf('.') + 1));
            }
        } catch (MirroredTypesException e) {
            for(TypeMirror elem : e.getTypeMirrors()) {
                DeclaredType decType = (DeclaredType) elem;
                String name = decType.toString();
                compos.add(name);
                simpleCompos.add(name.substring(name.lastIndexOf('.') + 1));
            }
        }

        if(compos.size() == 0)
            throw new ProcessorException("The EntityFactory has no composition declared in the @Bind annotation!");

        if((new HashSet<String>(compos)).size() != compos.size())
            throw new ProcessorException("Component.class declarations in @Bind must be unique, there is a duplicate!");


        for (String e : compos) {
            boolean found = false;
            for (CompSetterMethod f : compSetters) {
                if(f.fullCompName.equals(e)) {
                    methods.add(f);
                    found = true;
                    break;
                }
            }
            if(found == false)
                throw new ProcessorException("In the @Bind annotation, there is a Component Class which has no @CompSetter annotation: %s", e);
        }
    }
}
