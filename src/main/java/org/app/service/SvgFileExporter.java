package org.app.service;

import org.teavm.jso.JSBody;

/**
 * Сохраняет готовый SVG
 */
public class SvgFileExporter {

    private SvgFileExporter() {
    }

    /** @return имя файла по шаблону {@code plot_YYYY-MM-DD_HH-mm-ss.svg} */
    public static String generateFileName() {
        return "plot_" + currentTimestamp() + ".svg";
    }

    /**
     * Запускает скачивание SVG через браузерный API.
     *
     * @param svgDocument содержимое SVG файла
     * @param filename имя скачиваемого файла
     */
    public static void export(String svgDocument, String filename) {
        triggerDownload(svgDocument, filename);
    }

    @JSBody(params = {}, script =
            "var d = new Date();" +
            "function pad(n) { return n < 10 ? '0' + n : '' + n; }" +
            "return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate())" +
            " + '_' + pad(d.getHours()) + '-' + pad(d.getMinutes()) + '-' + pad(d.getSeconds());")
    private static native String currentTimestamp();

    @JSBody(params = {"content", "filename"}, script =
            "var blob = new Blob([content], {type: 'image/svg+xml'});" +
            "var url = URL.createObjectURL(blob);" +
            "var a = document.createElement('a');" +
            "a.href = url;" +
            "a.download = filename;" +
            "document.body.appendChild(a);" +
            "a.click();" +
            "document.body.removeChild(a);" +
            "URL.revokeObjectURL(url);")
    private static native void triggerDownload(String content, String filename);
}
