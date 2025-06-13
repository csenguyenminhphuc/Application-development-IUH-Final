package gui.other;

import java.awt.Desktop;
import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import entity.KhuyenMaiResult;
import entity.Ve;

public class TaoHoaDon {
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

    public static void taoHD(ArrayList<Ve> danhSachGhe, HoaDon hd,KhuyenMaiResult kmr) {
        DecimalFormat df = new DecimalFormat("#.00");
        String defaultFolderPath = "src/data/";
        String fileName = hd.getMaHoaDon() + "_ticket.pdf";

        Document document = new Document(new Rectangle(400, 800));

        try {
            PdfWriter.getInstance(document, new FileOutputStream(defaultFolderPath + fileName));
            document.open();

            // Tiêu đề
            Paragraph title = new Paragraph("Hóa Đơn", getFont(18, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Thông tin Chuyến Đi
            Paragraph cinemaInfo = new Paragraph("\nNhà Ga Số 8386\n12 Nguyễn Văn Bảo, Phường 4, "
            		+ "Quận Gò Vấp, TP. Hồ Chí Minh\n\n", getFont(12, Font.NORMAL));
            cinemaInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(cinemaInfo);

            // Dòng kẻ ngang
            document.add(new Paragraph("===========================================================\n", getFont(10, Font.NORMAL)));

            // Thông tin khách hàng
            Paragraph customerInfo = new Paragraph("Thông tin khách hàng\n", getFont(14, Font.BOLD));
            document.add(customerInfo);
            document.add(new Paragraph("Tên: " + hd.getKhachHang().getTenKH() + "\n", getFont(12, Font.NORMAL)));
            document.add(new Paragraph("SĐT: " + hd.getKhachHang().getSoDienThoai() + "\n", getFont(12, Font.NORMAL)));
            document.add(new Paragraph("Số CCCD: " + hd.getKhachHang().getCccd() + "\n", getFont(12, Font.NORMAL)));

            document.add(new Paragraph("===========================================================\n", getFont(10, Font.NORMAL)));

            // Thông tin phim
            Paragraph movieInfo = new Paragraph("Thông tin chuyến đi\n", getFont(14, Font.BOLD));
            document.add(movieInfo);
            document.add(new Paragraph("Tàu: " +  danhSachGhe.get(0).getChuyenDi().getTau().getTenTau()+ "\n", getFont(12, Font.NORMAL)));
            document.add(new Paragraph("Chuyến đi: " + danhSachGhe.get(0).getChuyenDi().getThoiGianDiChuyen().getGaDi().getTenGa() + " --> " +danhSachGhe.get(0).getChuyenDi().getThoiGianDiChuyen().getGaDen().getTenGa() + "\n", getFont(12, Font.NORMAL)));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ENGLISH);
            document.add(new Paragraph("Ngày đi: " + dateFormatter.format(danhSachGhe.get(0).getChuyenDi().getThoiGianKhoiHanh()) + "\n", getFont(12, Font.NORMAL)));
            document.add(new Paragraph("Ngày đến dự kiến: " + dateFormatter.format(danhSachGhe.get(0).getChuyenDi().getThoiGianDenDuTinh())+ "\n", getFont(12, Font.NORMAL)));
            document.add(new Paragraph("===========================================================\n", getFont(10, Font.NORMAL)));
            //Bảng ghế
            double tongTienVe = 0;
            PdfPTable bangGhe = new PdfPTable(new float[]{2, 1});
            bangGhe.setWidthPercentage(100);
            bangGhe.addCell(createCell("Ghế", getFont(12, Font.BOLD), Element.ALIGN_LEFT));
            bangGhe.addCell(createCell("Thành tiền", getFont(12, Font.BOLD), Element.ALIGN_RIGHT));
            
            for (Ve ghe : danhSachGhe) {
                //G-001-01-06-0045
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

                String viTriGhe ="Toa: "+toa+ " Khoang: "+soKhoang + " Ghế: "+ viTri; 
            	bangGhe.addCell(createCell(viTriGhe, getFont(12, Font.NORMAL), Element.ALIGN_LEFT));
                tongTienVe+= ghe.getGiaVe();
            	bangGhe.addCell(createCell(Double.toString(ghe.getGiaVe()), getFont(12, Font.NORMAL), Element.ALIGN_RIGHT));
            }
            
            document.add(bangGhe);

            
            
            document.add(new Paragraph("===========================================================\n", getFont(10, Font.NORMAL)));
            PdfPTable bangThanhToan = new PdfPTable(new float[]{2, 1});
            bangThanhToan.setWidthPercentage(100);
            bangThanhToan.addCell(createCell("Chi tiết thanh toán", getFont(12, Font.BOLD), Element.ALIGN_LEFT));
            bangThanhToan.addCell(createCell("Thành tiền", getFont(12, Font.BOLD), Element.ALIGN_RIGHT));
            
            bangThanhToan.addCell(createCell("Tổng tiền vé ", getFont(12, Font.NORMAL), Element.ALIGN_LEFT));
            bangThanhToan.addCell(createCell(Double.toString(tongTienVe), getFont(12, Font.NORMAL), Element.ALIGN_RIGHT));
            
            
            bangThanhToan.addCell(createCell("VAT ", getFont(12, Font.NORMAL), Element.ALIGN_LEFT));
            bangThanhToan.addCell(createCell(Double.toString(tongTienVe *hd.getVAT()), getFont(12, Font.NORMAL), Element.ALIGN_RIGHT));
            
            bangThanhToan.addCell(createCell("Khuyến mãi ", getFont(12, Font.NORMAL), Element.ALIGN_LEFT));        
            bangThanhToan.addCell(createCell("- "+String.valueOf(kmr.getTienGiam()), getFont(12, Font.NORMAL), Element.ALIGN_RIGHT));
            document.add(bangThanhToan);
            document.add(new Paragraph("===========================================================\n", getFont(10, Font.NORMAL)));

            // Tổng tiền
            Paragraph totalParagraph = new Paragraph("Tổng tiền: " + df.format(hd.getTongTien()) + " VND\n", getFont(14, Font.BOLD));
            totalParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalParagraph);

            // Mã QR Code
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Map<EncodeHintType, Object> hintMap = new HashMap<>();
            hintMap.put(EncodeHintType.MARGIN, 1);
            BitMatrix qrCodeMatrix = new MultiFormatWriter().encode(hd.getMaHoaDon(), BarcodeFormat.QR_CODE, 150, 150, hintMap);
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