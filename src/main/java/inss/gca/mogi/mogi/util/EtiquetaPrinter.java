package inss.gca.mogi.mogi.util;

import inss.gca.mogi.mogi.dto.CaixaDTO;

import java.awt.*;
import java.awt.print.*;

public class EtiquetaPrinter {

    public static void gerarEtiqueta(CaixaDTO dto) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("ETIQUETA DE CAIXA", 100, 50);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString("Código da Caixa: " + dto.getCodCaixa(), 50, 100);
            g2d.drawString("NB Inicial: " + dto.getNbInicial(), 50, 130);
            g2d.drawString("NB Final: " + dto.getNbFinal(), 50, 160);

            return Printable.PAGE_EXISTS;
        });

        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }
}
