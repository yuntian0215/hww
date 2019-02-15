package com.hww.es.service.impl;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.hww.es.pojo.WebReports;
import com.hww.es.pojo.es.EsNews;
import com.hww.es.pojo.es.EsNewsQueryParam;
import com.hww.es.service.IReportsService;
import com.hww.es.service.ITemplateFtpService;
import com.hww.es.service.es.QueryParamRemoveService;
import com.hww.es.service.es.QueryParamService;
import com.hww.es.service.es.SearchService;
import com.hww.es.service.websource.WebSourceService;
import com.hww.es.util.PageBean;
import com.hww.es.util.ftpUtil.DataUtil;
import com.hww.es.util.ftpUtil.DefaultLineChart;
import com.hww.es.util.ftpUtil.PDFHeaderFooter;
import com.hww.es.util.ftpUtil.PDFKit;
import com.hww.es.util.ftpUtil.ScatterPlotChart;
import com.hww.es.util.ftpUtil.ScatterPlotChartTest;
import com.hww.es.util.ftpUtil.TemplateBO;
import com.hww.es.util.ftpUtil.XYLine;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
/**
 * <p>按模版生成FTP文件</p>
 * 2018年11月26日上午9:27:59
 * @author lvjie
 */
@Service
public class TemplateFtpServiceImpl implements ITemplateFtpService{
	@Autowired
	QueryParamService queryParamService;
	@Autowired
	WebSourceService webSourceService;
	@Autowired
	private SearchService searchService;
	@Autowired
	QueryParamRemoveService queryParamRemoveService;
	@Value("${upload}")
    private String UPLOAD_FOLDER;
	@Autowired
	private IReportsService reportsService;
	/**
	 * <p>生成FTP文档-数据填充</p>
	 * 2018年11月26日上午9:31:31
	 * @author lvjie
	 */
	@Override
	public void templateFtp() {
        TemplateBO templateBO=new TemplateBO();
        templateBO.setTemplateName("Hello iText! Hello freemarker! Hello jFreeChart!");
        templateBO.setFreeMarkerUrl("http://www.zheng-hang.com/chm/freemarker2_3_24/ref_directive_if.html");
        templateBO.setITEXTUrl("http://developers.itextpdf.com/examples-itext5");
        templateBO.setJFreeChartUrl("http://www.yiibai.com/jfreechart/jfreechart_referenced_apis.html");
        templateBO.setImageUrl("http://mss.vip.sankuai.com/v1/mss_74e5b6ab17f44f799a524fa86b6faebf/360report/logo_1.png");
        List<String> scores=new ArrayList<String>();
        scores.add("90");
        scores.add("95");
        scores.add("98");
        templateBO.setScores(scores);
        //折线图
        List<XYLine> lineList=getTemperatureLineList();
        DefaultLineChart lineChart=new DefaultLineChart();
        lineChart.setHeight(500);
        lineChart.setWidth(300);
        String picUrl=lineChart.draw(lineList,0);
        templateBO.setPicUrl(picUrl);

        //散点图
        String scatterUrl=ScatterPlotChart.draw(ScatterPlotChartTest.getData(),1,"他评得分(%)","自评得分(%)");
        templateBO.setScatterUrl(scatterUrl);
        //模版所在路径
//        String templatePath="D:/javaTool/workspace/workspace2/hww-es/src/main/resources/templates";
        URL url = this.getClass().getClassLoader().getResource("templates");
        String templatePath = url.getPath();
        String path= this.createPDF(templatePath,templateBO,"hello.pdf");
        System.out.println(path);
		
	}
	/**
	 * <p>生成FTP文档，并保存</p>
	 * @param templatePath
	 * @param data
	 * @param fileName
	 * @return
	 * 2018年11月26日上午9:35:29
	 * @author lvjie
	 */
	public  String createPDF(String templatePath,Object data, String fileName){
        //pdf保存路径
        try {
            //设置自定义PDF页眉页脚工具类
            PDFHeaderFooter headerFooter=new PDFHeaderFooter();
            PDFKit kit=new PDFKit();
            kit.setHeaderFooterBuilder(headerFooter);
            //设置输出路径
            kit.setSaveFilePath("D:/hello.pdf");

            String saveFilePath=kit.exportToFile(fileName,data);
            return  saveFilePath;
        } catch (Exception e) {
//            log.error("PDF生成失败{}", ExceptionUtils.getFullStackTrace(e));
            return null;
        }

    }
	/**
	 * <p>根据集合生成折线图</p>
	 * @return
	 * 2018年11月26日上午9:33:48
	 * @author lvjie
	 */
	public static List<XYLine> getTemperatureLineList() {
        List<XYLine> list= Lists.newArrayList();
        for(int i=1;i<=7;i++){
            XYLine line=new XYLine();
            float random=Math.round(Math.random()*10);
            line.setXValue("星期"+i);
            line.setYValue(20+random);
            line.setGroupName("下周");
            list.add(line);
        }
        for(int i=1;i<=7;i++){
            XYLine line=new XYLine();
            float random=Math.round(Math.random()*10);
            line.setXValue("星期"+i);
            line.setYValue(20+random);
            line.setGroupName("这周");
            list.add(line);
        }
        return list;
    }
	/**
	 * <p>根据模版table生成FTP</p>
	 * 2018年11月29日下午2:32:14
	 * @author lvjie
	 */
	@Override
	public String templateFtpTable(EsNewsQueryParam param) {
		Map<String,Object> dataMap = new HashMap<String, Object>();
		
        dataMap = this.getAbstractInfo(param);
        
      //pdf保存路径
        long filename = System.currentTimeMillis();
        String path = UPLOAD_FOLDER+filename+".pdf";
        try {
            PDFKit kit=new PDFKit();
            //设置输出路径
            kit.setSaveFilePath(path);

            String saveFilePath=kit.exportToFile("tempFtp.pdf",dataMap);
            
            System.out.println(saveFilePath);
        } catch (Exception e) {
//            log.error("PDF生成失败{}", ExceptionUtils.getFullStackTrace(e));
        }
        WebReports reports = new WebReports();
        reports.setUserId(param.getUserId());//用户id
        reports.setTitle(param.getTitle());
		reports.setDocType("pdf");
		reports.setIsRead(0);
		reports.setDocUrl(path);
		reports.setSendingTime(new Date());
		reports.setNum(0);
		reports.setState("0");
		reportsService.insertReport(reports);
        return "/file/"+filename+".pdf";
	}
	/**
	 * <p>根据查询条件获取数据</p>
	 * @param param
	 * @return
	 * 2018年11月30日下午4:09:14
	 * @author lvjie
	 */
	public Map<String,Object> getAbstractInfo(EsNewsQueryParam param){
		Map<String,Object> dataMap = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		dataMap.put("newDate", sdf.format(new Date()));
		dataMap.put("date", sdf1.format(new Date()));
		List<Map<String ,Object>> members = new ArrayList<>();
		
		int pageNum = 1;
		int pageSize = 20;
		PageBean<EsNews> page;
		List<Long> removeList = null;//排除新闻列表
		List<String> sourceIdList = null;
		List<Long> webTypeIdList = param.getWebTypeIdList();
		if(param.getId() != null) {//如果id不是null，是已经定制好的搜索集合
			param = queryParamService.getById(param.getId());//根据id查出详情
			removeList = queryParamRemoveService.getListByQueryParamId(param.getId());//查出删除列表
		}
		if(webTypeIdList != null && webTypeIdList.size() > 0) {
			sourceIdList = webSourceService.getSourceIdListByTypeIdList(webTypeIdList);//查询站点
		}
		page = searchService.getSearch(param,sourceIdList,removeList,pageNum, pageSize);//最后多条件分页查询
		
		List<EsNews> list = page.getList();//数据集合
		for(int i=0;i<list.size();i++){
			Map<String ,Object> memMap = new HashMap<>();
			memMap.put("num", (i+1));//序列
			memMap.put("inserDate", list.get(i).getPublishDate());//消息时间
			memMap.put("source", list.get(i).getSource());//来源
			memMap.put("title", list.get(i).getTitle());//标题
			memMap.put("abstract", list.get(i).getIntroduction());//简介
			members.add(memMap);
		}
		
		dataMap.put("page", members);
		return dataMap;
	}
	/**
	 * <p>后台自动生成表格</p>
	 * @throws Exception
	 * 2018年12月4日下午2:07:55
	 * @author lvjie
	 */
	@Override
	public void templateTable()throws Exception{
		imageWaterMark(createPDF(), "D://upload/admin.jpg"); 
	}
	
	/**
	 * <p>创建PDF文档</p>
	 * 2018年12月4日下午1:38:54
	 * @author lvjie
	 */
	public String createPDF() throws Exception{
		//输出路径
	    String outPath = "D://upload/test.pdf";//DataUtil.createTempPath(".pdf");
	    //设置纸张
        Rectangle rect = new Rectangle(PageSize.A4);
        //创建文档实例
        Document doc=new Document(rect);
        //添加中文字体
        BaseFont bfChinese=BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        
        //设置字体样式
        Font textFont = new Font(bfChinese,11,Font.NORMAL); //正常
        Font redTextFont = new Font(bfChinese,11,Font.NORMAL,Color.RED); //正常,红色
        Font boldFont = new Font(bfChinese,11,Font.BOLD); //加粗
        Font redBoldFont = new Font(bfChinese,11,Font.BOLD,Color.RED); //加粗,红色
        Font firsetTitleFont = new Font(bfChinese,22,Font.BOLD); //一级标题
        Font secondTitleFont = new Font(bfChinese,15,Font.BOLD); //二级标题
        Font underlineFont = new Font(bfChinese,11,Font.UNDERLINE); //下划线斜体
        
        //手指图片
        Image hand = Image.getInstance("D://upload/shouzhi.png");
        
        //创建输出流
        PdfWriter.getInstance(doc, new FileOutputStream(new File(outPath)));
        
        doc.open();
        doc.newPage();
        
        //段落  
        Paragraph p1 = new Paragraph();  
        //短语
        Phrase ph1 = new Phrase();  
        //块
        Chunk c1 = new Chunk("*********", boldFont) ;
        Chunk c11 = new Chunk("（信用报告提供机构l ogo）", textFont) ;
        //将块添加到短语
        ph1.add(c1);
        ph1.add(c11);
        //将短语添加到段落
        p1.add(ph1);
        //将段落添加到短语
        doc.add(p1);
        
        p1 = new Paragraph();  
        ph1 = new Phrase(); 
        Chunk c2 = new Chunk("报告编号：", boldFont) ;
        Chunk c22 = new Chunk("SN-201604010001", textFont) ;
        ph1.add(c2);
        ph1.add(c22);
        p1.add(ph1);
        doc.add(p1);
        
        p1 = new Paragraph("企业信用报告", firsetTitleFont);
        p1.setLeading(50);
        p1.setAlignment(Element.ALIGN_CENTER);
        doc.add(p1);
        
        p1 = new Paragraph("（企业版）", textFont);
        p1.setLeading(20);
        p1.setAlignment(Element.ALIGN_CENTER);
        doc.add(p1);
        
        p1 = new Paragraph();  
        p1.setLeading(20);
        p1.setAlignment(Element.ALIGN_CENTER);
        ph1 = new Phrase(); 
        Chunk c3 = new Chunk("查询时间：", boldFont) ;
        Chunk c33 = new Chunk("2016-04-01 00:00:00", textFont) ;
        Chunk c4 = new Chunk(leftPad("查询人：", 10), boldFont) ;
        Chunk c44 = new Chunk("admin（用户登录名）", textFont) ;
        ph1.add(c3);
        ph1.add(c33);
        ph1.add(c4);
        ph1.add(c44);
        p1.add(ph1);
        doc.add(p1);
        
        p1 = new Paragraph("报告说明", secondTitleFont);
        p1.setLeading(50);
        p1.setAlignment(Element.ALIGN_CENTER);
        doc.add(p1);
        
        p1 = new Paragraph(" ");  
        p1.setLeading(30);
        doc.add(p1);
        
        p1 = new Paragraph();  
        ph1 = new Phrase(); 
        Chunk c5 = new Chunk("1.本报告由", textFont) ;
        Chunk c6 = new Chunk("****信用信息服务中心", underlineFont) ;
        c6.setSkew(0, 30);
        Chunk c7 = new Chunk(" 出具，依据截止报告时间小微企业信用信息数据库记录的信息生成。除异议标注和查询记录外，报告中的信息均由相关报数机构和信息主体提供，", textFont);
        Chunk c8 = new Chunk("不保证其真实性和准确性，但承诺在信息整合、汇总、展示的全过程中保持客观、中立的地位。", textFont) ;
        ph1.add(c5);
        ph1.add(c6);
        ph1.add(c7);
        ph1.add(c6);
        ph1.add(c8);
        p1.add(ph1);
        doc.add(p1);
        
        p1 = new Paragraph();  
        ph1 = new Phrase(); 
        Chunk c9 = new Chunk("2.异议标注是", textFont) ;
        Chunk c10 = new Chunk(" 对报告中的信息记录或对信息主体所作的说明。", textFont);
        ph1.add(c9);
        ph1.add(c6);
        ph1.add(c10);
        p1.add(ph1);
        doc.add(p1);
        
        p1 = new Paragraph("3.信息主体说明是信息主体对报数机构提供的信息记录所作的简要说明。", textFont);  
        doc.add(p1);
        
        p1 = new Paragraph();  
        ph1 = new Phrase(); 
        Chunk c12 = new Chunk("4.信息主体有权对本报告中的内容提出异议。如有异议，可联系报数机构，也可到", textFont) ;
        Chunk c13 = new Chunk(" 提出异议申请。", textFont);
        ph1.add(c12);
        ph1.add(c6);
        ph1.add(c13);
        p1.add(ph1);
        doc.add(p1);
        
        p1 = new Paragraph("5.更多咨询，请致电客户服务热线********。", textFont);  
        doc.add(p1);
        
        p1 = new Paragraph("信息概况", secondTitleFont);
        p1.setSpacingBefore(30);
        p1.setSpacingAfter(30);
        p1.setAlignment(Element.ALIGN_CENTER);
        doc.add(p1);
        
        p1 = new Paragraph("信息主体实缴资本**万元，****年**月**日股东****公司向***公司转让**万元股权。", textFont);  
        p1.setFirstLineIndent(23);
        p1.setSpacingAfter(15);
        doc.add(p1);
        
        p1 = new Paragraph("信息主体于20**年首次与金融机构发生信贷关系，报告期内，共在**家金融机构办理过信贷业务，目前**家金融机构对信息主体授信共**万元，**家金融机构**万元贷款未结清。提供对外担保****万元。", textFont);  
        p1.setFirstLineIndent(23);
        p1.setSpacingAfter(15);
        doc.add(p1);
        
        p1 = new Paragraph("报告期内，信息主体共有**条人民银行表彰记录、**条外汇管理表彰记录、**条环保部门表彰记录、**条质监部门表彰记录、**条税务部门表彰记录、**条财政部门表彰记录、**条工商部门表彰记录、**条卫生部门表彰记录、**条海关部门表彰记录、**条其他部门表彰记录。", textFont);  
        p1.setFirstLineIndent(23);
        p1.setSpacingAfter(15);
        doc.add(p1);
        
        p1 = new Paragraph("报告期内，信息主体共有**条人民银行处罚记录、**条外汇管理处罚记录、**条环保部门处罚记录、**条质监部门处罚记录、**条税务部门处罚记录、**条财政部门处罚记录、**条工商部门处罚记录、**条卫生部门处罚记录、**条海关部门处罚记录、**条其他部门处罚记录。共有**条欠税记录、**条欠水/电/汽记录、**条法院判决执行记录。", textFont);  
        p1.setFirstLineIndent(23);
        doc.add(p1);
        
        p1 = new Paragraph("信息主体最近一次缴纳社会保险为****年**月**日，最近一次缴纳公积金为****年**月**日。", textFont);  
        p1.setFirstLineIndent(23);
        doc.add(p1);
        
        p1 = new Paragraph("目前，报告中共有*条报数机构说明、*条信息主体说明、*条服务中心说明。", textFont);  
        p1.setFirstLineIndent(23);
        p1.setSpacingAfter(15);
        doc.add(p1);
        
        p1 = new Paragraph();  
        p1.setSpacingAfter(10);
        ph1 = new Phrase(); 
        Chunk c14 = new Chunk(hand, 0, 0);
        Chunk c15 = new Chunk(leftPad("身份信息", 7), boldFont);
        ph1.add(c14);
        ph1.add(c15);
        p1.add(ph1);
        doc.add(p1);
        
        // 创建一个有4列的表格  
        PdfPTable table = new PdfPTable(4);
        table.setTotalWidth(new float[]{ 105, 170, 105, 170 }); //设置列宽
        table.setLockedWidth(true); //锁定列宽
        
        PdfPCell cell;
        cell = new PdfPCell(new Phrase("企业名称", boldFont));
        cell.setBorderWidthLeft(3);
        cell.setBorderWidthTop(3);
        cell.setMinimumHeight(30); //设置单元格高度
        cell.setUseAscender(true); //设置可以居中
        cell.setHorizontalAlignment(Cell.ALIGN_CENTER); //设置水平居中
        cell.setVerticalAlignment(Cell.ALIGN_MIDDLE); //设置垂直居中
        table.addCell(cell);
        cell = new PdfPCell();
        cell.setBorderWidthRight(3);
        cell.setBorderWidthTop(3);
        cell.setUseAscender(true); //设置可以居中
        cell.setVerticalAlignment(Cell.ALIGN_MIDDLE); //设置垂直居中
        cell.setColspan(3);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase("统一社会信用代码（组织机构代码）", boldFont));
        cell.setBorderWidthLeft(3);
        cell.setMinimumHeight(40); //设置单元格高度
        cell.setUseAscender(true); //设置可以居中
        cell.setHorizontalAlignment(Cell.ALIGN_CENTER); //设置水平居中
        cell.setVerticalAlignment(Cell.ALIGN_MIDDLE); //设置垂直居中
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(" ", textFont));
        cell.setUseAscender(true); //设置可以居中
        cell.setVerticalAlignment(Cell.ALIGN_MIDDLE); //设置垂直居中
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("注册地址", boldFont));
        cell.setUseAscender(true); //设置可以居中
        cell.setHorizontalAlignment(Cell.ALIGN_CENTER); //设置水平居中
        cell.setVerticalAlignment(Cell.ALIGN_MIDDLE); //设置垂直居中
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(" ", textFont));
        cell.setBorderWidthRight(3);
        cell.setUseAscender(true); //设置可以居中
        cell.setVerticalAlignment(Cell.ALIGN_MIDDLE); //设置垂直居中
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase("行业分类", boldFont));
        cell.setBorderWidthLeft(3);
        cell.setMinimumHeight(30); //设置单元格高度
        cell.setUseAscender(true); //设置可以居中
        cell.setHorizontalAlignment(Cell.ALIGN_CENTER); //设置水平居中
        cell.setVerticalAlignment(Cell.ALIGN_MIDDLE); //设置垂直居中
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(" ", textFont));
        cell.setUseAscender(true); //设置可以居中
        cell.setVerticalAlignment(Cell.ALIGN_MIDDLE); //设置垂直居中
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("实缴资本", boldFont));
        cell.setUseAscender(true); //设置可以居中
        cell.setHorizontalAlignment(Cell.ALIGN_CENTER); //设置水平居中
        cell.setVerticalAlignment(Cell.ALIGN_MIDDLE); //设置垂直居中
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(" ", textFont));
        cell.setBorderWidthRight(3);
        cell.setUseAscender(true); //设置可以居中
        cell.setVerticalAlignment(Cell.ALIGN_MIDDLE); //设置垂直居中
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase("数据来源", boldFont));
        cell.setBorderWidthLeft(3);
        cell.setBorderWidthBottom(3);
        cell.setMinimumHeight(30); //设置单元格高度
        cell.setUseAscender(true); //设置可以居中
        cell.setHorizontalAlignment(Cell.ALIGN_CENTER); //设置水平居中
        cell.setVerticalAlignment(Cell.ALIGN_MIDDLE); //设置垂直居中
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(" ", textFont));
        cell.setBorderWidthBottom(3);
        cell.setUseAscender(true); //设置可以居中
        cell.setVerticalAlignment(Cell.ALIGN_MIDDLE); //设置垂直居中
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("采集时间", boldFont));
        cell.setBorderWidthBottom(3);
        cell.setUseAscender(true); //设置可以居中
        cell.setHorizontalAlignment(Cell.ALIGN_CENTER); //设置水平居中
        cell.setVerticalAlignment(Cell.ALIGN_MIDDLE); //设置垂直居中
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(" ", textFont));
        cell.setBorderWidthRight(3);
        cell.setBorderWidthBottom(3);
        cell.setUseAscender(true); //设置可以居中
        cell.setVerticalAlignment(Cell.ALIGN_MIDDLE); //设置垂直居中
        table.addCell(cell);
        doc.add(table);
        
        p1 = new Paragraph();  
        p1.setSpacingBefore(20);
        p1.setSpacingAfter(10);
        ph1 = new Phrase(); 
        Chunk c17 = new Chunk(hand, 0, 0);
        Chunk c18 = new Chunk(leftPad("出资情况", 7), boldFont);
        ph1.add(c17);
        ph1.add(c18);
        p1.add(ph1);
        doc.add(p1);
        
        table = new PdfPTable(9);
        table.setTotalWidth(new float[]{80, 60, 60, 50, 60, 60, 60, 60, 60}); //设置列宽
        table.setLockedWidth(true); //锁定列宽
        
        table = createCell(table, new String[]{"出资方名称","证件类型","证件号码","币种","出资金额","出资方式","出资占比","数据来源","采集时间"}, 2, 9);
        doc.add(table);
        
        p1 = new Paragraph();  
        p1.setSpacingBefore(20);
        p1.setSpacingAfter(10);
        ph1 = new Phrase(); 
        Chunk c62 = new Chunk(hand, 0, 0);
        Chunk c63 = new Chunk(leftPad("行政处罚信息", 9), boldFont);
        ph1.add(c62);
        ph1.add(c63);
        p1.add(ph1);
        doc.add(p1);
        
        table = new PdfPTable(10);
        table.setTotalWidth(new float[]{55, 55, 55, 55, 55, 55, 55, 55, 55, 55}); //设置列宽
        table.setLockedWidth(true); //锁定列宽
        
        table = createCell(table, new String[]{"处罚文书号","裁定处罚部门","处罚时间","违法或违规行为描述","涉及金额","处罚金额","处罚决定","整改情况","数据来源","采集时间"}, 2, 10);
        doc.add(table);
        
        p1 = new Paragraph();  
        p1.setSpacingBefore(20);
        p1.setSpacingAfter(10);
        ph1 = new Phrase(); 
        Chunk c65 = new Chunk(hand, 0, 0);
        Chunk c66 = new Chunk(leftPad("司法信息", 7), boldFont);
        ph1.add(c65);
        ph1.add(c66);
        p1.add(ph1);
        doc.add(p1);
        
        table = new PdfPTable(7);
        table.setTotalWidth(new float[]{70, 80, 80, 80, 80, 80, 80}); //设置列宽
        table.setLockedWidth(true); //锁定列宽
        
        table = createCell(table, new String[]{"案号","立案法院","立案日期","案由","执行标的","执行标的金额","已执行标的"}, 2, 7);
        table = createCell(table, new String[]{"已执行标的金额","执行标的日期","案件状态","结案日期","执行结案方式","数据来源","采集时间"}, 2, 7);
        doc.add(table);
        
        p1 = new Paragraph();  
        p1.setSpacingBefore(20);
        p1.setSpacingAfter(10);
        ph1 = new Phrase(); 
        Chunk c68 = new Chunk(hand, 0, 0);
        Chunk c69 = new Chunk(leftPad("银行授信", 7), boldFont);
        ph1.add(c68);
        ph1.add(c69);
        p1.add(ph1);
        doc.add(p1);
        
        table = new PdfPTable(8);
        table.setTotalWidth(new float[]{40, 100, 75, 75, 75, 60, 60, 60}); //设置列宽
        table.setLockedWidth(true); //锁定列宽
        
        table = createCell(table, new String[]{"编号","授信金融机构","授信额度","起始日期","终止日期","银行内部评级结果","数据来源","采集时间"}, 2, 8);
        doc.add(table);
        
        p1 = new Paragraph();  
        p1.setSpacingBefore(20);
        p1.setSpacingAfter(10);
        ph1 = new Phrase(); 
        Chunk c71 = new Chunk(hand, 0, 0);
        Chunk c72 = new Chunk(leftPad("银行贷款", 7), boldFont);
        ph1.add(c71);
        ph1.add(c72);
        p1.add(ph1);
        doc.add(p1);
        
        table = new PdfPTable(10);
        table.setTotalWidth(new float[]{30, 55, 75, 55, 55, 55, 55, 55, 55, 55}); //设置列宽
        table.setLockedWidth(true); //锁定列宽
        
        table = createCell(table, new String[]{" ","编号","贷款金融机构","贷款余额","贷款日期","到期日","五级分类","欠息金额","数据来源","采集时间"}, 1, 10);
        table = createCell(table, null, 3, 10);
        table = createCell(table, null, 3, 10);
        doc.add(table);
        
        p1 = new Paragraph();  
        p1.setSpacingBefore(20);
        p1.setSpacingAfter(10);
        ph1 = new Phrase(); 
        Chunk c74 = new Chunk(hand, 0, 0);
        Chunk c75 = new Chunk(leftPad("贴现", 5), boldFont);
        ph1.add(c74);
        ph1.add(c75);
        p1.add(ph1);
        doc.add(p1);
        
        table = new PdfPTable(8);
        table.setTotalWidth(new float[]{40, 155, 60, 60, 60, 60, 60, 60}); //设置列宽
        table.setLockedWidth(true); //锁定列宽
        
        table = createCell(table, new String[]{"编号","贴现金融机构","贴现金额","贴现日","贴现率","五级分类","数据来源","采集时间"}, 2, 8);
        doc.add(table);
        
        p1 = new Paragraph();  
        p1.setSpacingBefore(20);
        p1.setSpacingAfter(10);
        ph1 = new Phrase(); 
        Chunk c85 = new Chunk(hand, 0, 0);
        Chunk c86 = new Chunk(leftPad("信息主体说明", 9), boldFont);
        ph1.add(c85);
        ph1.add(c86);
        p1.add(ph1);
        doc.add(p1);
        
        table = new PdfPTable(1);
        table.setTotalWidth(new float[]{500}); //设置列宽
        table.setLockedWidth(true); //锁定列宽
        
        table = createCell(table, null, 1,1);
        doc.add(table);
        
        p1 = new Paragraph();  
        p1.setSpacingBefore(20);
        p1.setSpacingAfter(10);
        ph1 = new Phrase(); 
        Chunk c87 = new Chunk(hand, 0, 0);
        Chunk c88 = new Chunk(leftPad("查询记录", 7), boldFont);
        ph1.add(c87);
        ph1.add(c88);
        p1.add(ph1);
        doc.add(p1);
        
        table = new PdfPTable(4);
        table.setTotalWidth(new float[]{220, 100, 100, 100}); //设置列宽
        table.setLockedWidth(true); //锁定列宽
        
        table = createCell(table, new String[]{"序号","查询日期","查询用户","查询原因"}, 3, 4);
        doc.add(table);
        
        p1 = new Paragraph();  
        p1.setSpacingBefore(20);
        p1.setSpacingAfter(10);
        ph1 = new Phrase(); 
        Chunk c89 = new Chunk("异议标注（用于记录信息主体对某类信息提出异议处理情况，置于每类信息项下方）：", redBoldFont);
        Chunk c90 = new Chunk("信息主体于2016年**月**日提出异议：我公司从未发生过***；业务发生机构于2016年**月**日提交说明：该笔信息确实存在；信息主体于2016年**月**日提出声明：该笔信息为我公司***所致。（在每一板块下详细记录信息主体提出异议处理过程和结果。）", redTextFont);
        ph1.add(c89);
        ph1.add(c90);
        p1.add(ph1);
        doc.add(p1);
        
        doc.close();
        return outPath;
	}
	/**
	 * <p>创建单元格</p>
	 * @param table
	 * @param title
	 * @param row
	 * @param cols
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 * 2018年12月4日下午1:56:16
	 * @author lvjie
	 */
	public PdfPTable createCell(PdfPTable table, String[] title, int row, int cols) throws DocumentException, IOException{
		//添加中文字体
        BaseFont bfChinese=BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font font = new Font(bfChinese,11,Font.BOLD);
        
        for(int i = 0; i < row; i++){

        	for(int j = 0; j < cols; j++){

	        	PdfPCell cell = new PdfPCell();
	        	       
	        	if(i==0 && title!=null){//设置表头
		        	cell = new PdfPCell(new Phrase(title[j], font)); //这样表头才能居中
		        	if(table.getRows().size() == 0){
		        		cell.setBorderWidthTop(3);
		        	}
	        	}
	        	if(row==1 && cols==1){ //只有一行一列
	        		cell.setBorderWidthTop(3);
	        	}
	        	if(j==0){//设置左边的边框宽度
	        		cell.setBorderWidthLeft(3);
	        	}
	        	if(j==(cols-1)){//设置右边的边框宽度
	        		cell.setBorderWidthRight(3);
	        	}
	        	if(i==(row-1)){//设置底部的边框宽度
	        		cell.setBorderWidthBottom(3);
	        	}
	        	cell.setMinimumHeight(40); //设置单元格高度
	            cell.setUseAscender(true); //设置可以居中
	            cell.setHorizontalAlignment(Cell.ALIGN_CENTER); //设置水平居中
	            cell.setVerticalAlignment(Cell.ALIGN_MIDDLE); //设置垂直居中
	
	            table.addCell(cell);
        	}
        }
        return table;
	}
	/**
	 * <p>加水印（文字）</p>
	 * @param inputFile
	 * @param waterMarkName
	 * 2018年12月4日下午1:57:18
	 * @author lvjie
	 */
	public void stringWaterMark(String inputFile, String waterMarkName){
		try {
			String[] spe = DataUtil.separatePath(inputFile);
			String outputFile = spe[0] + "_WM." + spe[1];
			
			PdfReader reader = new PdfReader(inputFile);
			PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFile));
			
			//添加中文字体
		    BaseFont bfChinese=BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		    
		    int total = reader.getNumberOfPages() + 1;
		    
		    PdfContentByte under;
		    int j = waterMarkName.length();
		    char c = 0;
		    int rise = 0;
		    //给每一页加水印
		    for (int i = 1; i < total; i++) {
			    rise = 400;
			    under = stamper.getUnderContent(i);
			    under.beginText();
			    under.setFontAndSize(bfChinese, 30);
			    under.setTextMatrix(200, 120);
			    for (int k = 0; k < j; k++) {
				    under.setTextRise(rise);
				    c = waterMarkName.charAt(k);
				    under.showText(c + "");
			    }
			    // 添加水印文字
			    under.endText();
		    }
		    stamper.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
	/**
	 * <p>加水印（图片）</p>
	 * @param inputFile
	 * @param imageFile
	 * 2018年12月4日下午2:01:32
	 * @author lvjie
	 */
	public void imageWaterMark(String inputFile, String imageFile){
		try {
		    String[] spe = DataUtil.separatePath(inputFile);
		    String outputFile = spe[0] + "_WM." + spe[1];
		   
		    PdfReader reader = new PdfReader(inputFile);
		    PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFile));
		   
		    int total = reader.getNumberOfPages() + 1;
		   
		    Image image = Image.getInstance(imageFile);
		    image.setAbsolutePosition(-100, 0);//坐标
		    image.scaleAbsolute(800,1000);//自定义大小
		    //image.setRotation(-20);//旋转 弧度
		    //image.setRotationDegrees(-45);//旋转 角度
		    //image.scalePercent(50);//依照比例缩放
		   
		    PdfGState gs = new PdfGState();
		    gs.setFillOpacity(0.2f);// 设置透明度为0.2


		    PdfContentByte under;
		    //给每一页加水印
		    for (int i = 1; i < total; i++) {
			    under = stamper.getUnderContent(i);
			    under.beginText();
			    // 添加水印图片
			    under.addImage(image);
			    under.setGState(gs);
		    }
		    stamper.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
	/**
	 * <p>设置左边距</p>
	 * @param str
	 * @param i
	 * @return
	 * 2018年12月4日下午2:03:06
	 * @author lvjie
	 */
    public  String leftPad(String str, int i) {
        int addSpaceNo = i-str.length();
        String space = ""; 
        for (int k=0; k<addSpaceNo; k++){
                space= " "+space;
        };
        String result =space + str ;
        return result;
    }
    /**
     * 设置模拟数据
     * @param list
     * @param num
     */
    public void add(List<String> list,int num){
        for(int i=0;i<num;i++){
            list.add("test"+i);
        }
    }
    /**
     * 设置间距
     * @param tmp
     * @return
     */
    public static String printBlank(int tmp){
          String space="";
          for(int m=0;m<tmp;m++){
              space=space+" ";
          }
          return space;
    }
}
