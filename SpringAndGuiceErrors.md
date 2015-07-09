# Introduction #

This is example code that randomly compares Guice error handling with Spring error handling. It uses the example shown [here](GuicySpring.md).

Guice's error handling is one of the framework's great strengths.
  * Lots of mistakes get detected at compile time.
  * You will see all the mistakes you made in one go, not only the first one.
  * Guice links to the offending configuration code in the error message, on which you can click to navigate to the line of configuration that matters (Java IDE feature).
  * Guice's error messages actually help.

THIS IS NOT A COMPLETE COMPARISON, AND WILL NEVER BE. I will add to this document over time.

# One #
Typo in Guice's `@Named` or Spring's corresponding `@Qualifier` configuration.

Guice:
```
bindConstant().annotatedWith(Names.named("colory")).to("White");
```

Guice Result:
```
Exception in thread "main" com.google.inject.CreationException: Guice configuration errors:

1) Error at demo.Cap.<init>(Cap.java:10):
 Binding to java.lang.String annotated with @com.google.inject.name.Named(value=color) not found. Annotations on other bindings to that type include: [@com.google.inject.name.Named(value=colory)]

1 error[s]
	at com.google.inject.BinderImpl.createInjector(BinderImpl.java:277)
	at com.google.inject.Guice.createInjector(Guice.java:79)
	at com.google.inject.Guice.createInjector(Guice.java:53)
	at com.google.inject.Guice.createInjector(Guice.java:43)
	at demo.error.Error1.main(Error1.java:14)
```

As you can see, Guice's message is helpful, and shows you the annotation that would have been right.

Spring:
```
<bean class="java.lang.String" factory-method="valueOf">
    <constructor-arg value="White"/>
    <!-- Typo in value -->
    <qualifier value="colory"/>
</bean>
```

Spring Result (I added some line breaks):
```
Exception in thread "main" org.springframework.beans.factory.UnsatisfiedDependencyException: 
Error creating bean with name 'demo.spring.xml.Cap#0' defined in class path resource [demo/spring/xml/error/error1.xml]:
Unsatisfied dependency expressed through constructor argument with index 0 of type [java.lang.String]: 
No unique bean of type [java.lang.String] is defined: Unsatisfied dependency of type [class java.lang.String]: expected at least 1 matching bean
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:570)
	at org.springframework.beans.factory.support.ConstructorResolver.autowireConstructor(ConstructorResolver.java:190)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.autowireConstructor(AbstractAutowireCapableBeanFactory.java:893)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:803)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:437)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory$1.run(AbstractAutowireCapableBeanFactory.java:404)
	at java.security.AccessController.doPrivileged(Native Method)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:375)
	at org.springframework.beans.factory.support.AbstractBeanFactory$1.getObject(AbstractBeanFactory.java:263)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:170)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:260)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:184)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:163)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:430)
	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:729)
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:381)
	at org.springframework.context.support.ClassPathXmlApplicationContext.<init>(ClassPathXmlApplicationContext.java:139)
	at org.springframework.context.support.ClassPathXmlApplicationContext.<init>(ClassPathXmlApplicationContext.java:83)
	at demo.spring.xml.error.Error1.main(Error1.java:11)
```

Expected at least 1 matching bean? So did I, man.

# Two #
Binding to a regular class where an annotation is required.

Guice:
```
bind(Cap.class).annotatedWith(Cap.class).to(Cap.class);
```

Guice Result:
```
Compile error.
```

Guice will not compile this code; the Java compiler will make sure that you pass in an annotation class.

Spring:
```
<bean class="demo.spring.xml.Cap">
    <qualifier type="demo.spring.xml.Cap"/>
</bean>
```

Spring Result (Added line breaks):
```
org.springframework.beans.factory.UnsatisfiedDependencyException: 
Error creating bean with name 'demo.spring.xml.Smurf#0' defined in class path resource [demo/spring/xml/error/error2.xml]:
Unsatisfied dependency expressed through constructor argument with index 0 of type [demo.spring.xml.Cap]:
No unique bean of type [demo.spring.xml.Cap] is defined: Unsatisfied dependency of type [class demo.spring.xml.Cap]: expected at least 1 matching bean
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:570)
	at org.springframework.beans.factory.support.ConstructorResolver.autowireConstructor(ConstructorResolver.java:190)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.autowireConstructor(AbstractAutowireCapableBeanFactory.java:893)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:803)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:437)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory$1.run(AbstractAutowireCapableBeanFactory.java:404)
	at java.security.AccessController.doPrivileged(Native Method)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:375)
	at org.springframework.beans.factory.support.AbstractBeanFactory$1.getObject(AbstractBeanFactory.java:263)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:170)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:260)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:184)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:163)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:430)
	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:729)
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:381)
	at org.springframework.context.support.ClassPathXmlApplicationContext.<init>(ClassPathXmlApplicationContext.java:139)
	at org.springframework.context.support.ClassPathXmlApplicationContext.<init>(ClassPathXmlApplicationContext.java:83)
	at demo.spring.xml.error.Error2.main(Error2.java:11)

```

Rrriiight.

# Three #
Converting an enum String to an enum value.

```
public enum Number {
    ONE, TWO
}

// Added for use with Spring
public class NumberUser {
    public NumberUser(demo.error.Number n) {
        System.out.println(n);
    }
}
```

Guice:
```
// Enum with typo
bindConstant().annotatedWith(Names.named("two")).to("TW");
```

Guice Result (Added line breaks):
```
Exception in thread "main" com.google.inject.ConfigurationException:
Error at [unknown source] Error converting String constant bound at demo.error.Error3$1.configure(Error3.java:15) to demo.error.Number:
No enum const class demo.error.Number.TW
	at com.google.inject.BinderImpl$RuntimeErrorHandler.handle(BinderImpl.java:426)
	at com.google.inject.AbstractErrorHandler.handle(AbstractErrorHandler.java:30)
	at com.google.inject.InjectorImpl.handleConstantConversionError(InjectorImpl.java:315)
	at com.google.inject.InjectorImpl.getInternalFactory(InjectorImpl.java:245)
	at com.google.inject.InjectorImpl.getProvider(InjectorImpl.java:693)
	at com.google.inject.InjectorImpl.getInstance(InjectorImpl.java:724)
	at demo.error.Error3.main(Error3.java:19)
```

Notice how Guice also links to the source configuration line (Error3.java:15), which is clickable in any Java IDE.
Note that Guice can also bind enum values directly, which would then result in a compile error if you make a typo. Don't use string conversion when you don't have to.

Spring:
```
    <bean id="nu" class="demo.spring.xml.error.NumberUser">
        <!-- Typo -->
        <constructor-arg value="TW"/>
    </bean>
```

Spring Result:
```
Exception in thread "main" org.springframework.beans.factory.UnsatisfiedDependencyException: 
Error creating bean with name 'nu' defined in class path resource [demo/spring/xml/error/error3.xml]: 
Unsatisfied dependency expressed through constructor argument with index 0 of type [demo.error.Number]: 
Could not convert constructor argument value of type [java.lang.String] to required type [demo.error.Number]:
Failed to convert value of type [java.lang.String] to required type [demo.error.Number]; 
nested exception is java.lang.IllegalArgumentException: Cannot convert value of type [java.lang.String] to required type [demo.error.Number]: 
no matching editors or conversion strategy found
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:544)
	at org.springframework.beans.factory.support.ConstructorResolver.autowireConstructor(ConstructorResolver.java:190)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.autowireConstructor(AbstractAutowireCapableBeanFactory.java:893)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:803)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:437)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory$1.run(AbstractAutowireCapableBeanFactory.java:404)
	at java.security.AccessController.doPrivileged(Native Method)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:375)
	at org.springframework.beans.factory.support.AbstractBeanFactory$1.getObject(AbstractBeanFactory.java:263)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:170)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:260)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:184)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:163)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:430)
	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:729)
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:381)
	at org.springframework.context.support.ClassPathXmlApplicationContext.<init>(ClassPathXmlApplicationContext.java:139)
	at org.springframework.context.support.ClassPathXmlApplicationContext.<init>(ClassPathXmlApplicationContext.java:83)
	at demo.spring.xml.error.Error3.main(Error3.java:9)
```

Which basically tells you the world is about to end. At least, that is what I can make of it.

# Four #
Import non-existent configuration definitions from a configuration unit.

Guice:
```
public class Error4 {
    public static void main(String[] args) {
        Guice.createInjector(new AbstractModule() {
            protected void configure() {
                install(new MyModula());
            }
        });
    }
    
    static class MyModule implements Module {
        public void configure(Binder binder) {
            // whatever...
        }
    }
}
```

Guice Result:
```
Compile error.
```

Spring:
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
                        http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
                        http://www.springframework.org/schema/context
                        http://www.springframework.org/schema/context/spring-context-2.5.xsd">
                        
    <import resource="does_not_exist.xml"/>
</beans>
```

Spring Result (Added line breaks):
```
Exception in thread "main" org.springframework.beans.factory.parsing.BeanDefinitionParsingException: Configuration problem: 
Failed to import bean definitions from relative location [does_not_exist.xml]
Offending resource: class path resource [demo/spring/xml/error/error4.xml];
nested exception is org.springframework.beans.factory.BeanDefinitionStoreException:
IOException parsing XML document from class path resource [demo/spring/xml/error/does_not_exist.xml]; 
nested exception is java.io.FileNotFoundException: class path resource [demo/spring/xml/error/does_not_exist.xml] cannot be opened because it does not exist
	at org.springframework.beans.factory.parsing.FailFastProblemReporter.error(FailFastProblemReporter.java:68)
	at org.springframework.beans.factory.parsing.ReaderContext.error(ReaderContext.java:85)
	at org.springframework.beans.factory.parsing.ReaderContext.error(ReaderContext.java:76)
	at org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader.importBeanDefinitionResource(DefaultBeanDefinitionDocumentReader.java:201)
	at org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader.parseDefaultElement(DefaultBeanDefinitionDocumentReader.java:147)
	at org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader.parseBeanDefinitions(DefaultBeanDefinitionDocumentReader.java:132)
	at org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader.registerBeanDefinitions(DefaultBeanDefinitionDocumentReader.java:92)
	at org.springframework.beans.factory.xml.XmlBeanDefinitionReader.registerBeanDefinitions(XmlBeanDefinitionReader.java:489)
	at org.springframework.beans.factory.xml.XmlBeanDefinitionReader.doLoadBeanDefinitions(XmlBeanDefinitionReader.java:384)
	at org.springframework.beans.factory.xml.XmlBeanDefinitionReader.loadBeanDefinitions(XmlBeanDefinitionReader.java:328)
	at org.springframework.beans.factory.xml.XmlBeanDefinitionReader.loadBeanDefinitions(XmlBeanDefinitionReader.java:296)
	at org.springframework.beans.factory.support.AbstractBeanDefinitionReader.loadBeanDefinitions(AbstractBeanDefinitionReader.java:143)
	at org.springframework.beans.factory.support.AbstractBeanDefinitionReader.loadBeanDefinitions(AbstractBeanDefinitionReader.java:178)
	at org.springframework.beans.factory.support.AbstractBeanDefinitionReader.loadBeanDefinitions(AbstractBeanDefinitionReader.java:149)
	at org.springframework.beans.factory.support.AbstractBeanDefinitionReader.loadBeanDefinitions(AbstractBeanDefinitionReader.java:212)
	at org.springframework.context.support.AbstractXmlApplicationContext.loadBeanDefinitions(AbstractXmlApplicationContext.java:113)
	at org.springframework.context.support.AbstractXmlApplicationContext.loadBeanDefinitions(AbstractXmlApplicationContext.java:80)
	at org.springframework.context.support.AbstractRefreshableApplicationContext.refreshBeanFactory(AbstractRefreshableApplicationContext.java:123)
	at org.springframework.context.support.AbstractApplicationContext.obtainFreshBeanFactory(AbstractApplicationContext.java:423)
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:353)
	at org.springframework.context.support.ClassPathXmlApplicationContext.<init>(ClassPathXmlApplicationContext.java:139)
	at org.springframework.context.support.ClassPathXmlApplicationContext.<init>(ClassPathXmlApplicationContext.java:83)
	at demo.spring.xml.error.Error4.main(Error4.java:9)
Caused by: org.springframework.beans.factory.BeanDefinitionStoreException: 
IOException parsing XML document from class path resource [demo/spring/xml/error/does_not_exist.xml]; nested exception is java.io.FileNotFoundException:
class path resource [demo/spring/xml/error/does_not_exist.xml] cannot be opened because it does not exist
	at org.springframework.beans.factory.xml.XmlBeanDefinitionReader.loadBeanDefinitions(XmlBeanDefinitionReader.java:335)
	at org.springframework.beans.factory.xml.XmlBeanDefinitionReader.loadBeanDefinitions(XmlBeanDefinitionReader.java:296)
	at org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader.importBeanDefinitionResource(DefaultBeanDefinitionDocumentReader.java:190)
	... 19 more
Caused by: java.io.FileNotFoundException: class path resource [demo/spring/xml/error/does_not_exist.xml] cannot be opened because it does not exist
	at org.springframework.core.io.ClassPathResource.getInputStream(ClassPathResource.java:142)
	at org.springframework.beans.factory.xml.XmlBeanDefinitionReader.loadBeanDefinitions(XmlBeanDefinitionReader.java:322)
	... 21 more
```

Besides all the useless junk, this exception actually tells you what is going on. But this code blows up at run time, not at compile time.

# Closing Comment #
To see how Guice collects multiple errors and presents them to you all at once (instead of one at a time), take a look at [this test](http://code.google.com/p/google-guice/source/browse/trunk/test/com/google/inject/ErrorHandlingTest.java) in the Guice sources. Thanks to Bob for pointing me to this example.

Output looks like:
```
1) Error at demo.ErrorHandlingTest$BadScope.class(ErrorHandlingTest.java:1):
 Please annotate with @ScopeAnnotation.

2) Error at demo.ErrorHandlingTest$BadScope.class(ErrorHandlingTest.java:1):
 Please annotate with @Retention(RUNTIME). Bound at demo.ErrorHandlingTest$MyModule.configure(ErrorHandlingTest.java:139).

3) Error at demo.ErrorHandlingTest$Bar.<init>(ErrorHandlingTest.java:96):
 Could not find a suitable constructor in demo.ErrorHandlingTest$Bar. Classes must have either one (and only one) constructor annotated with @Inject or a zero-argument constructor.

4) Error at demo.ErrorHandlingTest$Bar.bar(ErrorHandlingTest.java:100):
 Binding to java.lang.String annotated with @com.google.inject.name.Named(value=foo) not found. Annotations on other bindings to that type include: [@com.google.inject.name.Named(value=foo)]

5) Error at demo.ErrorHandlingTest$Bar.setNumbers(ErrorHandlingTest.java:98):
 Binding to java.util.List<java.lang.Integer> annotated with @com.google.inject.name.Named(value=numbers) not found. No bindings to that type were found.

6) Error at demo.ErrorHandlingTest$I.class(ErrorHandlingTest.java:1):
 java.lang.String doesn't extend demo.ErrorHandlingTest$I.

7) Error at demo.ErrorHandlingTest$Invalid.<init>(ErrorHandlingTest.java:112):
 Error while injecting at demo.ErrorHandlingTest$Tee.invalid(ErrorHandlingTest.java:103): Could not find a suitable constructor in demo.ErrorHandlingTest$Invalid. Classes must have either one (and only one) constructor annotated with @Inject or a zero-argument constructor.

8) Error at demo.ErrorHandlingTest$MyModule.configure(ErrorHandlingTest.java:136):
 No scope is bound to @Named.

9) Error at demo.ErrorHandlingTest$MyModule.configure(ErrorHandlingTest.java:137):
 Binding points to itself.

10) Error at demo.ErrorHandlingTest$MyModule.configure(ErrorHandlingTest.java:137):
 A binding to java.lang.Runnable was already configured at demo.ErrorHandlingTest$MyModule.configure(ErrorHandlingTest.java:131).

11) Error at demo.ErrorHandlingTest$MyModule.configure(ErrorHandlingTest.java:147):
 I don't like you

12) Error at demo.ErrorHandlingTest$MyModule.configure(ErrorHandlingTest.java:153):
 An exception was caught and reported. See log for details. Message: java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Integer

13) Error at demo.ErrorHandlingTest$MyModule.configure(ErrorHandlingTest.java:156):
 Binding to core guice framework type is not allowed: Module.

14) Error at demo.ErrorHandlingTest$Tee.tee(ErrorHandlingTest.java:106):
 Binding to int not found. No bindings to that type were found.

15) Error at demo.ErrorHandlingTest$TooManyScopes.class(ErrorHandlingTest.java:116):
 More than one scope annotation was found: @Singleton and @GoodScope

16) Error at demo.ErrorHandlingTest.missing(ErrorHandlingTest.java:44):
 Binding to java.util.List<java.lang.String> annotated with @com.google.inject.name.Named(value=missing) not found. Annotations on other bindings to that type include: [[no annotation]]

```

No more run-fix-run-fix-run-fix. Guice lets you run-fix-fix-fix. :-)