package org.jgayoso.ncomplo.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UploadBetsController {
    
    public UploadBetsController() {
        super();
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/upload")
    public String uploadBets(@RequestParam("file") final MultipartFile file, final RedirectAttributes redirectAttributes){
        final Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            /* The user is not logged in */
            redirectAttributes.addFlashAttribute("message", "Session expired");
            return "login";
        }
        
        FileInputStream fis = null;
        XSSFWorkbook book = null;
        try {
            final File excel = this.convert(file);
            fis = new FileInputStream(excel);
            book = new XSSFWorkbook(fis);
            final FormulaEvaluator evaluator = book.getCreationHelper().createFormulaEvaluator();
            final XSSFSheet sheet = book.getSheetAt(3);
            final Iterator<Row> itr = sheet.iterator();
            int iRow = 0;
            while (itr.hasNext()) {
                int iCell = 0;
                final Row row = itr.next();

                // Iterating over each column of Excel file
                final Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {

                    final Cell cell = cellIterator.next();
                    final CellValue cellValue = evaluator.evaluate(cell);
                    
                    iCell++;
                    
                }
                iRow++;
            }
            redirectAttributes.addFlashAttribute("message", "Success");
            return "redirect:/scoreboard";
        } catch (final IOException e) {
            redirectAttributes.addFlashAttribute("message", "Error processing file");
            return "redirect:/scoreboard";
        } finally {
            try {
                if (book != null) {book.close(); }
                if (fis != null) { fis.close(); }
            } catch (final Exception e) { 
                // Nothing to do
            }
        }
    }
    
    public File convert(final MultipartFile file) throws IOException
    {    
        final File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile(); 
        final FileOutputStream fos = new FileOutputStream(convFile); 
        fos.write(file.getBytes());
        fos.close(); 
        return convFile;
    }

}
