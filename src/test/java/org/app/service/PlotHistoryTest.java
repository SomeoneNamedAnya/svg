package org.app.service;

import org.app.domain.PlotConfig;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlotHistoryTest {

    @Test
    void ignoresUnavailableStorage() {
        PlotHistory history = new PlotHistory(new ThrowingStorage());
        PlotConfig config = PlotConfig.explicit("x", -10, 10).build();

        assertDoesNotThrow(() -> history.add(config));
        assertDoesNotThrow(history::loadAll);
        assertTrue(history.loadAll().isEmpty());
    }

    @Test
    void keepsOnlyFiveLatestConfigurations() {
        PlotHistory history = new PlotHistory(new MapStorage());

        for (int i = 0; i < 7; i++) {
            history.add(PlotConfig.explicit("x+" + i, -10, 10).build());
        }

        assertEquals(5, history.loadAll().size());
        assertEquals("x+6", history.loadAll().get(0).getFExpression());
        assertEquals("x+2", history.loadAll().get(4).getFExpression());
    }

    private static final class ThrowingStorage implements PlotHistory.StorageAccess {

        @Override
        public String getItem(String key) {
            throw new RuntimeException();
        }

        @Override
        public void setItem(String key, String value) {
            throw new RuntimeException();
        }

        @Override
        public void removeItem(String key) {
            throw new RuntimeException();
        }
    }

    private static final class MapStorage implements PlotHistory.StorageAccess {

        private final Map<String, String> values = new HashMap<>();

        @Override
        public String getItem(String key) {
            return values.get(key);
        }

        @Override
        public void setItem(String key, String value) {
            values.put(key, value);
        }

        @Override
        public void removeItem(String key) {
            values.remove(key);
        }
    }
}
