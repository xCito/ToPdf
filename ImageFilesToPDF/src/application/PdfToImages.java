package application;

import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.pdmodel.PDDocument;
import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.io.File;

public class PdfToImages
{
    PDDocument doc;
    File pdfDoc;
    ArrayList<Image> img;

    public PdfToImages(File f)
    {
        pdfDoc = f;
        img = new ArrayList<Image>();       // Initialize ArrayList of Images
        try
        {
            if(!checkForPDF(pdfDoc))        // Check if file is of type PDF
                throw new Exception();      // Throw an exception if not
                
            doc = PDDocument.load(pdfDoc);  
        }catch(Exception e)
        {
            System.out.println("May not have been a PDF");
            System.out.println("Loading PDF may have failed");
        }
    }
 
/////////////////////////////////////////////////////////////////////////    
    
    public int getNumberOfPages()
    {    return doc.getNumberOfPages();    }

/////////////////////////////////////////////////////////////////////////
    
    public ArrayList<Image> getImagesFromPDF()
    {     return img;   }

/////////////////////////////////////////////////////////////////////////
      
    public void extractImages()
    {
        try
        {
            PDFRenderer ren = new PDFRenderer(doc);
            BufferedImage[] buff = new BufferedImage[doc.getNumberOfPages()];
            float scale = 0.5f;
            for(int i=0; i<doc.getNumberOfPages(); ++i)
            {
                System.out.println("Rendering page: " + (i+1));
                buff[i] = ren.renderImage(i,scale);
            }
            doc.close();
            img.addAll(convertBufferedImageToImage(buff));
        }
        catch(Exception e)
        {
            System.out.println("Problem getting image from PDF");
        }
    }

/////////////////////////////////////////////////////////////////////////
    
    private ArrayList<Image> convertBufferedImageToImage(BufferedImage[] bf)
    {
        ArrayList<Image> pics = new ArrayList<Image>();
        
        for(int i=0; i<bf.length; ++i)
        {
            pics.add(SwingFXUtils.toFXImage(bf[i], null)); 
            System.out.println("> converting from BufferedImage to Image...");
        }
        
        return pics;
    }
    
/////////////////////////////////////////////////////////////////////////
        
    private boolean checkForPDF(File file)
    {
        String ext, str = file.getName();                   // Check if input is of 
        ext = str.substring(str.lastIndexOf('.'));
        ext = ext.toLowerCase();                            // type PDF for different
        System.out.println(ext + "<---- EXTENSION");        // procedures of creating
        if(ext.equals(".pdf"))                              // new PDF document
            return true;
        return false;
    }
}
