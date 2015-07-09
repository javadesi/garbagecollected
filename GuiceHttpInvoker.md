# Spring's HttpInvoker on Guice #

So I ported Spring's HttpInvoker to Guice. This is a direct port of code in the Spring Framework, I just removed the dependency on Spring's AOP and added some Guice builder magic to make the whole thing easy to configure.

Legal stuff:
This product includes software developed by the Spring Framework
Project (http://www.springframework.org). This is _not_ the Spring Framework.

# Download #
Get the bits [here](http://garbagecollected.googlecode.com/files/guice-httpinvoker-0.1.zip).

# Dependencies #
  * [Guice](http://code.google.com/p/google-guice)
  * [Warp Servlet](http://www.wideplay.com/warp%3A%3Aservlet)
  * [Apache HttpClient](http://hc.apache.org/httpclient-3.x/) (optional)

(I removed the clogging dependency for now)

# Example #

```
public interface SampleService {
    String hello();
}

public class SampleServiceImpl implements SampleService {
    public String hello() {
        return "Hello, World!";
    }
}
```

Now how do you expose this service at a remote location, and use it locally? Here's how.

## Server Configuration ##

For the server configuration you need Warp Servlet. After you configured the web.xml and put the JAR in the classpath as documented on the [Warp Servlet website](http://www.wideplay.com/warp%3A%3Aservlet), here's my WarpServletContextListener for this example:

```
public class MyInjectorCreator extends WarpServletContextListener {
    @Override
    protected Injector getInjector() {
        // build regular module
        Module bindings = new AbstractModule() {
            protected void configure() {
                bind(SampleService.class).to(SampleServiceImpl.class);
            }
        };

        // Create a remoting configuration
        RemotingConfiguration remoting = Remoting.host(SampleService.class).at("/sampleService");

        // build warp servlet module that hosts the remoting servlet for this service
        Module warpServlet = Servlets.configure()
    		                         .filters()
    		                         .servlets().serve(remoting.getLocation()).with(remoting.getServletKey())
    		                         .buildModule();

        return Guice.createInjector(bindings, warpServlet, remoting.getModule());
    }
}
```

For your reference, here's my web.xml:

```
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="2.4"
         xmlns="http://java.sun.com/xml/ns/j2ee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd" >

    <filter>
        <filter-name>warpServletFilter</filter-name>
        <filter-class>com.wideplay.warp.servlet.WebFilter</filter-class>
    </filter>

    <filter-mapping>
        <filter-name>warpServletFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <listener>
        <listener-class>org.garbargecollected.remoting.sample.MyInjectorCreator</listener-class>
    </listener>
</web-app>
```

## Client Configuration ##

How you can connect to the remote service at the client side:
```
public class Client {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(SampleService.class).toProvider(Remoting.connectTo("http://localhost:8080/sampleService")
                                                             .with(SampleService.class));
            }
        });
        
        SampleService service = injector.getInstance(SampleService.class);
        System.out.println(service.hello());
    }
}
```

Optionally, you can specify an executor, for example to use Apache HttpClient (which supports authentication and more advanced features that are not in the JDK). You obviously do need HttpClient in your classpath for this to work.
```
public class Client {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(SampleService.class).toProvider(Remoting.connectTo("http://localhost:8080/sampleService")
                                                             .using(new CommonsHttpInvokerRequestExecutor()) // optional
                                                             .with(SampleService.class));
            }
        });
        
        SampleService service = injector.getInstance(SampleService.class);
        System.out.println(service.hello());
    }
}
```

# Patches? Questions? #
robbie dot vanbrabant at googlemail