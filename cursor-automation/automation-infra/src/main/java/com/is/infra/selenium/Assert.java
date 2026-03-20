package com.is.infra.selenium;

public abstract class Assert<T extends BasePage> {
    protected final T page;

    public Assert(T page) {
        this.page = page;
    }

    /** Returns the page instance for continued fluent actions. */
    public T returnToPage() {
        return page;
    }
}
