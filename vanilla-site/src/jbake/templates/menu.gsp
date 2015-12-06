<!-- Fixed navbar -->
<div class="navbar navbar-default navbar-fixed-top" role="navigation">
  <div class="container">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="<% if (content.rootpath) { %>${content.rootpath}<% } else { %><% } %>">Vanilla</a>
    </div>
    <div class="navbar-collapse collapse">
      <ul class="nav navbar-nav">
        <li><a href="<%if (content.rootpath) {%>${content.rootpath}<% } else { %><% }%>index.html">Home</a></li>
        <li><a href="https://github.com/cjstehno/vanilla/issues">Issues</a></li>
        <li><a href="guide/html5" target="_blank">User Guide</a></li>
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown">Reports <b class="caret"></b></a>
          <ul class="dropdown-menu">
            <li><a href="core/groovydoc" target="_blank">GroovyDoc (Core)</a></li>
            <li><a href="core/tests" target="_blank">JUnit (Core)</a></li>
            <li><a href="core/codenarc/main.html" target="_blank">CodeNarc (Core: Main)</a></li>
            <li><a href="core/codenarc/test.html" target="_blank">CodeNarc (Core: Test)</a></li>
          </ul>
        </li>
      </ul>
    </div><!--/.nav-collapse -->
  </div>
</div>
<div class="container">