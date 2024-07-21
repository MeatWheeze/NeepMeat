package com.neep.meatlib.registry.annotation;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes(
        "com.neep.meatlib.registry.annotation.RegisterMe"
)
public class BlockRegistryProcessor extends AbstractProcessor
{
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        for (Element element : roundEnv.getElementsAnnotatedWith(RegisterMe.class))
        {
            if (element.getKind().isClass())
            {
                return true;
            }
            else
            {
                error(element, "Not a class");
            }
        }
        return false;
    }

    private void error(Element e, String msg, Object... args)
    {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }
}
