package org.app.service;

import org.app.domain.AxesLayout;
import org.app.domain.LineStyle;
import org.app.domain.ViewBox;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SvgDocumentBuilderTest {

    @Test
    void writesConcreteSvgDimensions() {
        String svg = SvgDocumentBuilder.build("M0,0",
                new AxesLayout(List.of(), List.of(), List.of()),
                600, 400, "#000000", 2, LineStyle.SOLID,
                false, false, "#000000", 1);

        assertTrue(svg.startsWith("<svg xmlns=\"http://www.w3.org/2000/svg\" "
                + "viewBox=\"0 0 600.00 400.00\" width=\"600.00\" height=\"400.00\">"));
    }

    @Test
    void alignsEdgeLabelsInsideSvg() {
        AxesLayout layout = AxesBuilder.build(new ViewBox(-10, 10, -10, 10), 600, 400);
        String svg = SvgDocumentBuilder.build("M0,0", layout,
                600, 400, "#000000", 2, LineStyle.SOLID,
                true, true, "#000000", 1);

        assertTrue(svg.contains("x=\"600.00\" y=\"396.00\" font-size=\"10\" fill=\"#000000\" "
                + "text-anchor=\"end\" dominant-baseline=\"text-after-edge\">10</text>"));
        assertTrue(svg.contains("x=\"4.00\" y=\"0.00\" font-size=\"10\" fill=\"#000000\" "
                + "text-anchor=\"start\" dominant-baseline=\"hanging\">10</text>"));
    }
}
