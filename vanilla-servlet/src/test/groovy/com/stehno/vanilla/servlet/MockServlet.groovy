package com.stehno.vanilla.servlet


import javax.servlet.*

/**
 * TODO: document me
 */
class MockServlet implements Servlet {

    String servletInfo
    ServletConfig servletConfig
    ServletRequest request
    ServletResponse response
    Closure<Void> serviceClosure = { ServletRequest req, ServletResponse res -> }

    private boolean destroyed

    boolean isDestroyed() {
        destroyed
    }

    @Override
    void init(ServletConfig config) throws ServletException {
        servletConfig = config
    }

    @Override
    void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        this.request = req
        this.response = res
        serviceClosure.call(req, res)
    }

    @Override
    void destroy() {
        destroyed = true
    }
}