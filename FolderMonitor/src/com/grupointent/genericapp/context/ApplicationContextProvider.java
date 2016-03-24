package com.grupointent.genericapp.context;

import java.io.PrintStream;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextProvider
    implements ApplicationContextAware
{

    public static ApplicationContext appContext;

    public ApplicationContextProvider()
    {
    }

    public void setApplicationContext(ApplicationContext ctx)
        throws BeansException
    {
        appContext = ctx;
        System.out.println((new StringBuilder("Context hecho!!!!")).append(appContext).toString());
    }
}
