A Spring MVC ViewResolver using the GroovyTemplate Engine
should go in vanilla-spring

Recently, while working with Spring MVC, I was considering which template framework to use
for my views and I was surprised to notice that there was no implementation that uses 
the Groovy TemplateEngine, other than the Groovy Markup Templates, which in my opinion
are pretty horrible - interesting in themselves, but they seem like they would be a 
nightmare to maintain, and your designers would kill you. The missing functionality 
surprised me; even a quick Google search didn't reveal anything, though there is some
sparse documentation around using the Grails GSP library as a Spring MVC ViefResolver.

I decided to give it a quick pass at an implementation. It actually turns out to be
quite simple. The ViewResolver implementation is an extension of the 
AbstractTemplateViewResolver with one main working method, the `buildView()` method
which contains the following:

```groovy
protected AbstractUrlBasedView buildView(final String viewName) throws Exception {
    GroovyTemplateView view = super.buildView(viewName) as GroovyTemplateView
    view.template = templateEngine.createTemplate(applicationContext.getResource(view.url).getURL())
    view.encoding = defaultEncoding
    return view
}
```

which basically just uses the view resolver framework to find the template file
and load it with the GSTringTemplateEngine - the framework takes care of the
caching and model attribute management.

The View is also quite simple. It's an extension of the AbstractTemplateview, with
the only implmented method being the `renderMergedTemplateModel()` method

```groovy
protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest req, HttpServletResponse res) throws Exception {
    res.contentType = contentType
    res.characterEncoding = encoding

    res.writer.withPrintWriter { PrintWriter out ->
        out.write(template.make(model) as String)
    }
}
```

which, as it suggests, renders the template with the model content to teh response 
output stream.

Lastly, you need to coonfigure the resolver in your application:

```groovy
@Bean ViewResolver viewResolver() {
    new GroovyTemplateViewResolver(
        contentType: 'text/html',
        cache: true,
        prefix: '/WEB-INF/gsp/',
        suffix: '.gsp'
    )
}
```

Notice all the functionality you get by default for very little added code.


>>> create a simpple boot project using it

I am adding a spring helper library to my vanilla project, the vanilla-spring 
project will have the final version of this code, thouhg it should be msimilar to 
what is dicussed here.




```groovy
import groovy.text.GStringTemplateEngine
import groovy.text.Template
import groovy.text.TemplateEngine
import groovy.transform.TypeChecked
import org.springframework.web.servlet.view.AbstractTemplateView
import org.springframework.web.servlet.view.AbstractTemplateViewResolver
import org.springframework.web.servlet.view.AbstractUrlBasedView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.nio.charset.StandardCharsets

/**
 * Spring MVC ViewResolver implementation which uses the Groovy GStringTemplateEngine for view templates.
 *
 * <pre>
    @Bean ViewResolver viewResolver() {
        new GroovyTemplateViewResolver(
            contentType: 'text/html',
            cache: true,
            prefix: '/WEB-INF/gsp/',
            suffix: '.gsp'
        )
    }
 * </pre>
 */
@TypeChecked
class GroovyTemplateViewResolver extends AbstractTemplateViewResolver {

    /**
     * The default character encoding to be used by the template views. Defaults to UTF-8 if not specified.
     */
    String defaultEncoding = StandardCharsets.UTF_8.name()

    private final TemplateEngine templateEngine = new GStringTemplateEngine()

    GroovyTemplateViewResolver() {
        viewClass = requiredViewClass()
    }

    @Override
    protected Class<?> requiredViewClass() {
        GroovyTemplateView
    }

    @Override
    protected AbstractUrlBasedView buildView(final String viewName) throws Exception {
        GroovyTemplateView view = super.buildView(viewName) as GroovyTemplateView
        view.template = templateEngine.createTemplate(applicationContext.getResource(view.url).getURL())
        view.encoding = defaultEncoding
        return view
    }
}

/**
 * Spring MVC View implementation used by the GroovyTemplateViewResolver.
 */
@TypeChecked
class GroovyTemplateView extends AbstractTemplateView {

    Template template
    String encoding

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest req, HttpServletResponse res) throws Exception {
        res.contentType = contentType
        res.characterEncoding = encoding

        res.writer.withPrintWriter { PrintWriter out ->
            out.write(template.make(model) as String)
        }
    }
}
```