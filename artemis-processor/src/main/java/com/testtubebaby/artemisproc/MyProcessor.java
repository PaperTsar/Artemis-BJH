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

import com.google.auto.service.AutoService;

import com.testtubebaby.artemismain.annotations.Bind;
import com.testtubebaby.artemismain.annotations.CompSetter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {
    private Messager messager;
    private Filer filer;
    private List<FactoryData> factories = new ArrayList<>();
    private List<CompSetterMethod> compSetters = new ArrayList<>();
    private FactoryFiler factFiler;
    private Set<String> factoryNames = new HashSet<>();
    private Set<String> methodNames = new HashSet<>();

    @Override
    public void init(ProcessingEnvironment env) {
        super.init(env);
        messager = env.getMessager();
        filer = env.getFiler();
        factFiler = new FactoryFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if(roundEnv.processingOver() || annotations.size() == 0)
            return true;

        Element procElem = null;
        try {
            for (Element f : roundEnv.getElementsAnnotatedWith(CompSetter.class)) {
                procElem = f;
                compSetters.add(new CompSetterMethod((TypeElement) f));
            }

            for (Element f : roundEnv.getElementsAnnotatedWith(Bind.class)) {
                procElem = f;
                factories.add(new FactoryData((TypeElement) f, compSetters));
            }
        } catch (ProcessorException e) {
            error(procElem, e.getMessage());
            return true;
        }

        for (CompSetterMethod e : compSetters) {
            if(methodNames.add(e.methodName) == false) {
                error(e.element, "There are at least two components with setter methods named %s, duplicates are not allowed!", e.methodName);
                return true;
            }
        }

        for (FactoryData e : factories) {
            if(factoryNames.add(e.newFactName) == false) {
                error(e.element, "There are at least two factories named %s, factory implementations are stored in the same namespace, therefore they must have different names!", e.newFactName);
                return true;
            }
        }

        factories.stream().forEach(p -> factFiler.fileFactory(p, filer));
        factories.clear();

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Bind.class.getCanonicalName());
        annotations.add(CompSetter.class.getCanonicalName());
        return annotations;
    }

    private void error(Element e, String msg, Object... params) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, params), e);
    }
}
