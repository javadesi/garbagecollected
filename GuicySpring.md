# Introduction #

As a follow-up to the talk I gave at the [Profict Wintercamp](http://java.profict.nl), this document shows the demo application I prepared, which has one Guice and two Spring implementations. Using that example I showed off some of Guice's excellent error handling features, and how the corresponding Spring code handles those. For that content, take a look [over here](SpringAndGuiceErrors.md).

The Spring version(s) of the demo demonstrate Spring 2.5's annotation-driven configuration options.

The Guice example uses Guice 1.0, the Spring examples use Spring 2.5.3.

# Demo: The Guice Smurfs #

Smurfs wear a white cap:

```
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@BindingAnnotation
public @interface White {}
```

```
public class Smurf {
    private final Cap cap;
    
    @Inject
    public Smurf(@White Cap cap) {
        this.cap = cap;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + " with cap: " + cap;
    }
}
```

```
public class Cap {
    private final String color;
    
    @Inject
    public Cap(@Named("color") String color) {
        this.color = color;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + " with color: " + color;
    }
}
```
Gargamel likes cooking grouchy smurf:

```
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@BindingAnnotation
public @interface Grouchy {}
```

```
public class Gargamel {
    @Inject
    public void cook(@Grouchy Smurf smurf) {
        System.out.println("Cooking: " + smurf);
    }
}
```

You can run this example using the following Guice configuration:

```
public class GuiceMain {    
    static class SmurfModule extends AbstractModule {
        protected void configure() {
            bindConstant().annotatedWith(Names.named("color")).to("White");
            bind(Smurf.class).annotatedWith(Grouchy.class).to(Smurf.class);
            bind(Cap.class).annotatedWith(White.class).to(Cap.class);
        }
    }
    
    public static void main(String[] args) {
        Injector i = Guice.createInjector(new SmurfModule());
        i.getInstance(Gargamel.class);
    }
}
```

Output:

`Cooking: Smurf with cap: Cap with color: White`

# Demo: The Spring 2.5 XML Smurfs #

```
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface White {}
```

```
public class Smurf {
    private final Cap cap;
    
    @Autowired
    public Smurf(@White Cap cap) {
        this.cap = cap;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + " with cap: " + cap;
    }
}
```

```
public class Cap {
    private final String color;
    
    @Autowired
    public Cap(@Qualifier("color") String color) {
        this.color = color;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + " with color: " + color;
    }
}
```

```
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface Grouchy {}
```

```
public class Gargamel {
    @Autowired
    public void cook(@Grouchy Smurf smurf) {
        System.out.println("Cooking: " + smurf);
    }
}
```

Now you need to define your "candidate beans" in XML:

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
                        http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
                        http://www.springframework.org/schema/context
                        http://www.springframework.org/schema/context/spring-context-2.5.xsd">
                        
    <context:annotation-config/>

    <bean class="java.lang.String" factory-method="valueOf">
        <constructor-arg value="White"/>
        <qualifier value="color"/>
    </bean>
    
    <bean class="demo.spring.xml.Cap">
        <qualifier type="demo.spring.xml.White"/>
    </bean>
    
    <bean class="demo.spring.xml.Smurf">
        <qualifier type="demo.spring.xml.Grouchy"/>
    </bean>
    
    <bean id="gargamel" class="demo.spring.xml.Gargamel"/>
</beans>
```

Finally, you can run this code using the following code:
```
public class SpringMain {
    public static void main(String[] args) {
        ApplicationContext ctx = 
            new ClassPathXmlApplicationContext("demo/spring/applicationContext.xml");
        Gargamel g = (Gargamel) ctx.getBean("gargamel");
    }
}
```

# Demo: The Spring 2.5 single-line-of-XML Smurfs #

Reusing the same `@Grouchy` and `@White` annotations from the previous Spring example, you can reduce the amount of XML further by enabling class path component scanning. Using that option, you need to annotate your classes themselves with their configuration. I'm not a fan (Guice modules, please!), but maybe you are.

```
@Grouchy
@Component
public class Smurf {
    private final Cap cap;
    
    @Autowired
    public Smurf(@White Cap cap) {
        this.cap = cap;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + " with cap: " + cap;
    }
}
```

```
@White 
@Component
public class Cap {
    private final String color;
    
    @Autowired
    public Cap(@Qualifier("color") String color) {
        this.color = color;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + " with color: " + color;
    }
}
```

```
@Component
public class Gargamel {
    @Autowired
    public void cook(@Grouchy Smurf smurf) {
        System.out.println("Cooking: " + smurf);
    }
}
```

Obviously, it's not possible to annotate `java.lang.String`, so next to enabling component scanning, I have to configure the "color" property in XML. Maybe I can get rid of that using a `FactoryBean`, but I didn't try.

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
                        http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
                        http://www.springframework.org/schema/context
                        http://www.springframework.org/schema/context/spring-context-2.5.xsd">
                        
    <context:component-scan base-package="demo.spring" />
    
    <bean class="java.lang.String" factory-method="valueOf">
        <constructor-arg value="White"/>
        <qualifier value="color"/>
    </bean>
</beans>
```

To run the code above, you can reuse the same `SpringMain` class from the previous example.

# Where's Spring's JavaConfig? #
To see how that would look, I refer to the XML. ;-)

# Hidden Mistake #
Spring's default is singleton scope, Guice's default is "no scope" (what the Spring folks call "prototype"). So these examples do not behave exactly the same.