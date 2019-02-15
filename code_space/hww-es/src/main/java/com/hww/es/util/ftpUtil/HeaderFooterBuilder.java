package com.hww.es.util.ftpUtil;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * <p>处理页眉页脚</p>
 * 2018年11月28日下午2:08:11
 * @author lvjie
 */
public interface HeaderFooterBuilder {


    /**
     * @description 写页眉
     */
    void writeHeader(PdfWriter writer, Document document, Object data, Font font, PdfTemplate template);
    /**
     * @description 写页脚
     */
    void writeFooter(PdfWriter writer, Document document, Object data, Font font, PdfTemplate template);

    /**
     * @description 关闭文档前,获取替换页眉页脚处设置模板的文本
     */
    String  getReplaceOfTemplate(PdfWriter writer, Document document, Object data);

}
