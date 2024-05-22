package com.project.stepDefinitions;

import com.project.pageObjectManager.PageObjectManager;
import com.project.pages.CommonSteps;

public abstract class AbstractSteps {
    // TODO: this field and its getter and setter could be removed once non-pages getters are removed from PageObjectManager
//    private static final ThreadLocal<PageObjectManager> pageObjectManager = new ThreadLocal<>();
//
//    public static void setPageObjectManager() {
//        pageObjectManager.set(new PageObjectManager());
//    }
//
//    public static PageObjectManager getPageObjectManager() {
//        return pageObjectManager.get();
//    }

    public static <T extends CommonSteps> T getPage(Class<T> c) {
        return PageObjectManager.get(c);
    }
}
