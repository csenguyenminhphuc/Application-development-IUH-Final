/*
 * @(#) TaoVe.java 1.0 Nov 13, 2024
 * Copyright (c) 2024 IUH.
 * All rights reserved.
 */
package gui.other;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import entity.HoaDon;
import entity.Ve;

/**
 * @description:
 * @author: Thanh Trong
 * @date: Nov 13, 2024
 * @version: 1.0
 */

public class TaoVe {
	private static String createLine(int doDai, char kyTu) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < doDai; i++) {
			sb.append(kyTu);
		}
		sb.append("\n");
		return sb.toString();
	}

    private static Font getFont(float size, int style) {
        try {
            String fontPath = "gui/resources/fonts/Roboto-Regular.ttf";
            com.itextpdf.text.pdf.BaseFont baseFont = com.itextpdf.text.pdf.BaseFont.createFont(fontPath, com.itextpdf.text.pdf.BaseFont.IDENTITY_H, com.itextpdf.text.pdf.BaseFont.EMBEDDED);
            return new Font(baseFont, size, style);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font(Font.FontFamily.TIMES_ROMAN, size, style);
        }
    }

    public static void taoVe(Ve ghe) {
        String[] part = ghe.getGhe().getMaGhe().split("-");
        int toa = Integer.parseInt(part[2]);
        int soKhoang = Integer.parseInt(part[3]);
        String[] parts = ghe.getGhe().getViTri().split("_");
        // parts = ["H","01","G","03"]
        int hang  = Integer.parseInt(parts[1]);  // "01" → 1
        int soghe = Integer.parseInt(parts[3]);  // "03" → 3
        int viTri = 0;
        if(ghe.getGhe().getLoaiGhe().getMaLoaiGhe().equals("GHE_NGOI_MEM")){
            viTri = 4*(hang - 1)+ soghe+ 8*(soKhoang -1);
        }else if(ghe.getGhe().getLoaiGhe().getMaLoaiGhe().equals("GIUONG_NAM_4")){
            viTri = 2*(hang - 1)+ soghe+ 4*(soKhoang -1);
        }else if(ghe.getGhe().getLoaiGhe().getMaLoaiGhe().equals("GIUONG_NAM_6")){
            viTri = 3*(hang - 1)+ soghe+ 6*(soKhoang -1);
        }
        String defaultFolderPath = "src/data/";
        String fileName = ghe.getMaVe() + "_ticket.pdf";

        Document document = new Document(new Rectangle(400, 800));

        try {
            PdfWriter.getInstance(document, new FileOutputStream(defaultFolderPath + fileName));
            document.open();

            // Tiêu đề
            Paragraph title = new Paragraph("Vé", getFont(18, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Thông tin rạp chiếu phim
            Paragraph cinemaInfo = new Paragraph("\nNhà Ga Số 8386\n12 Nguyễn Văn Bảo, Phường 4, "
            		+ "Quận Gò Vấp, TP. Hồ Chí Minh\n\n", getFont(12, Font.NORMAL));
            cinemaInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(cinemaInfo);

            // Dòng kẻ ngang
            document.add(new Paragraph("===========================================================\n", getFont(10, Font.NORMAL)));

            Paragraph viTriGhe = new Paragraph(viTri + "\n", getFont(30, Font.BOLD));
            viTriGhe.setAlignment(Element.ALIGN_CENTER); // Căn giữa nội dung
            document.add(viTriGhe);
            // Thông tin phim
            Paragraph movieInfo = new Paragraph("Thông tin vé lên Tàu\n", getFont(14, Font.BOLD));
            document.add(movieInfo);
            document.add(new Paragraph("Tàu: " +  ghe.getChuyenDi().getTau().getTenTau()+ "\n", getFont(12, Font.NORMAL)));
            document.add(new Paragraph("Chuyến đi: " + ghe.getChuyenDi().getThoiGianDiChuyen().getGaDi().getTenGa() + " --> " +ghe.getChuyenDi().getThoiGianDiChuyen().getGaDen().getTenGa() + "\n", getFont(12, Font.NORMAL)));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ENGLISH);
            document.add(new Paragraph("Ngày đi: " + dateFormatter.format(ghe.getChuyenDi().getThoiGianKhoiHanh()) + "\n", getFont(12, Font.NORMAL)));
            document.add(new Paragraph("Ngày đến dự kiến: " + dateFormatter.format(ghe.getChuyenDi().getThoiGianDenDuTinh())+ "\n", getFont(12, Font.NORMAL)));
            document.add(new Paragraph("Toa tàu: " + toa+ "\n", getFont(12, Font.NORMAL)));
            document.add(new Paragraph("Khoang tàu: " + soKhoang+ "\n", getFont(12, Font.NORMAL)));
            document.add(new Paragraph("Gia vé: " + ghe.getGiaVe()+ "\n", getFont(12, Font.NORMAL)));
            document.add(new Paragraph("===========================================================\n", getFont(10, Font.NORMAL)));
        	
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Map<EncodeHintType, Object> hintMap = new HashMap<>();
            hintMap.put(EncodeHintType.MARGIN, 1);
            BitMatrix qrCodeMatrix = new MultiFormatWriter().encode(ghe.getMaVe(), BarcodeFormat.QR_CODE, 150, 150, hintMap);
            MatrixToImageWriter.writeToStream(qrCodeMatrix, "PNG", baos);
            Image qrCodeImage = Image.getInstance(baos.toByteArray());
            qrCodeImage.setAlignment(Element.ALIGN_CENTER);
            document.add(qrCodeImage);
        	document.close();
        	try {
				if (Desktop.isDesktopSupported()) {
					File pdfFile = new File(defaultFolderPath + fileName);
					Desktop.getDesktop().open(pdfFile);
				} else {
					System.out.println("Desktop is not supported.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (DocumentException | IOException | WriterException e) {
			e.printStackTrace();
		}
	}
    
    private static PdfPCell createCell(String content, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }
}