package org.app.domain;

import java.util.List;

/**
 * Готовая геометрия осей, сетки и подписей в пикселях
 */
public final class AxesLayout {

    private final List<GridLine> gridLines;
    private final List<GridLine> axisLines;
    private final List<TickLabel> tickLabels;

    /**
     * Создаёт рассчитанную раскладку элементов координатной системы
     *
     * @param gridLines линии фоновой сетки
     * @param axisLines линии координатных осей
     * @param tickLabels подписи к осям
     */
    public AxesLayout(List<GridLine> gridLines, List<GridLine> axisLines, List<TickLabel> tickLabels) {
        this.gridLines = gridLines;
        this.axisLines = axisLines;
        this.tickLabels = tickLabels;
    }

    /** @return линии фоновой сетки */
    public List<GridLine> getGridLines() {
        return gridLines;
    }

    /**
     * @return линии осей {@code x=0}/{@code y=0}; список содержит только оси, попавшие в область отображения
     */
    public List<GridLine> getAxisLines() {
        return axisLines;
    }

    /** @return подписи к осям */
    public List<TickLabel> getTickLabels() {
        return tickLabels;
    }
}
