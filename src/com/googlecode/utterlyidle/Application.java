package com.googlecode.utterlyidle;

import com.googlecode.yadic.Container;

public interface Application extends HttpHandler {
    Container applicationScope();

    Container createRequestScope();

    Application add(Module module);

    Engine engine();

}
