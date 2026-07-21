package org.app.domain;

/**
 * Поддерживаемые способы задания кривой
 */
public enum FunctionMode {
    /** Явная функция вида {@code y = f(x)} */
    EXPLICIT,

    /** Параметрическая система вида {@code x = f(t), y = g(t)} */
    PARAMETRIC
}
