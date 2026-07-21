package org.app.service;

import org.app.domain.FunctionMode;
import org.app.domain.PlotConfig;
import org.teavm.jso.JSBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Хранит последние 5 успешных конфигураций построения графика между открытиями страницы в
 * localStorage браузера
 */
public class PlotHistory {

    private static final int MAX_ENTRIES = 5;
    private static final String KEY_PREFIX = "plotHistoryEntry";
    private final StorageAccess storage;

    public PlotHistory() {
        this(new BrowserStorageAccess());
    }

    PlotHistory(StorageAccess storage) {
        this.storage = storage;
    }

    /**
     * Сохраняет конфигурацию как самую свежую, сдвигая более старые записи
     *
     * @param config успешно использованная конфигурация
     */
    public void add(PlotConfig config) {
        List<String> encoded = new ArrayList<>();
        encoded.add(PlotConfigCodec.encode(config));

        for (int i = 0; i < MAX_ENTRIES - 1; i++) {
            String existing = getItem(KEY_PREFIX + i);
            if (existing == null) {
                break;
            }
            encoded.add(existing);
        }

        for (int i = 0; i < MAX_ENTRIES; i++) {
            if (i < encoded.size()) {
                setItem(KEY_PREFIX + i, encoded.get(i));
            } else {
                removeStoredItem(KEY_PREFIX + i);
            }
        }
    }

    /**
     * Повреждённые и устаревшие записи пропускаются
     *
     * @return до пяти последних конфигураций, начиная с самой свежей
     */
    public List<PlotConfig> loadAll() {
        List<PlotConfig> result = new ArrayList<>();

        for (int i = 0; i < MAX_ENTRIES; i++) {
            String encoded = getItem(KEY_PREFIX + i);
            if (encoded == null) {
                continue;
            }
            try {
                result.add(PlotConfigCodec.decode(encoded));
            } catch (RuntimeException e) {
                // повреждённая или устаревшего формата запись
            }
        }

        return result;
    }

    private String getItem(String key) {
        try {
            return storage.getItem(key);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private void setItem(String key, String value) {
        try {
            storage.setItem(key, value);
        } catch (RuntimeException e) {
        }
    }

    private void removeStoredItem(String key) {
        try {
            storage.removeItem(key);
        } catch (RuntimeException e) {
        }
    }

    interface StorageAccess {
        String getItem(String key);

        void setItem(String key, String value);

        void removeItem(String key);
    }

    private static final class BrowserStorageAccess implements StorageAccess {

        @Override
        public String getItem(String key) {
            return safeGetItem(key);
        }

        @Override
        public void setItem(String key, String value) {
            safeSetItem(key, value);
        }

        @Override
        public void removeItem(String key) {
            safeRemoveItem(key);
        }

        @JSBody(params = {"key"}, script = "try { return window.localStorage.getItem(key); } catch (e) { return null; }")
        private static native String safeGetItem(String key);

        @JSBody(params = {"key", "value"}, script = "try { window.localStorage.setItem(key, value); } catch (e) {}")
        private static native void safeSetItem(String key, String value);

        @JSBody(params = {"key"}, script = "try { window.localStorage.removeItem(key); } catch (e) {}")
        private static native void safeRemoveItem(String key);
    }

    /**
     * @param config конфигурация построения
     * @return короткая подпись для списка, например {@code y = sin(x), x∈[-10, 10]}
     */
    public static String describe(PlotConfig config) {
        String head = config.getMode() == FunctionMode.EXPLICIT
                ? "y = " + config.getFExpression()
                : "x=" + config.getFExpression() + ", y=" + config.getGExpression();
        String rangeVar = config.getMode() == FunctionMode.EXPLICIT ? "x" : "t";
        return head + "  " + rangeVar + "∈[" + trimNumber(config.getRangeMin()) + ", " + trimNumber(config.getRangeMax()) + "]";
    }

    private static String trimNumber(double value) {
        if (Math.abs(value - Math.round(value)) < 1e-9) {
            return String.valueOf(Math.round(value));
        }
        return DecimalFormatter.format(value, 2);
    }
}
