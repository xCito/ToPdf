package application;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class ImagesToPDFConverter
{
    PDDocument mainDoc;
    List<File> imgFiles;
    String fileName;
    String path;
    
    public ImagesToPDFConverter(List<File> f, String name)
    {
        imgFiles = f;
        fileName = name;
        path = "";
    }
   
/////////////////////////////////////////////////////////////////////////
     
    public void setDirectory(String p)
    {
        path = p;
    }

/////////////////////////////////////////////////////////////////////////
 
    public void createPDF()
    {
        mainDoc = new PDDocument(); 
        ArrayList<PDPage> pages = new ArrayList<PDPage>(); // [imgFiles.size()];
        String imageName;
        boolean pdfFound = false;
        try
        {
            for(int i=0; i<imgFiles.size(); ++i)
            { 
                imageName = imgFiles.get(i).getAbsolutePath();      
                pdfFound = checkForPDF(imgFiles.get(i));    
                System.out.println("Checking if PDF....");
                if(pdfFound)
                {    
                    System.out.println("PDF found");
                    addPDFInputToDoc(imgFiles.get(i));
                    continue;
                }       
                pages.add(new PDPage());                        // Page holder OR creating a PAGE
        
                mainDoc.addPage(pages.get(pages.size()-1));                              // Adding page to new DOC
        
                PDImageXObject image = PDImageXObject.createFromFile(imageName,mainDoc);        // image Container
                PDPageContentStream contentStream = new PDPageContentStream(mainDoc, pages.get(pages.size()-1)); // The "drawer" to doc
                contentStream.drawImage(image, 0, 0, pages.get(pages.size()-1).getBBox().getWidth(),         // draw image to doc
                                                     pages.get(pages.size()-1).getBBox().getHeight());
                contentStream.close();                                                      // save progress to doc
            } 
            
            mainDoc.save(path + fileName);
            mainDoc.close();
        
        }      
        catch (Exception e) 
        {
            System.out.println("Something went wrong with the conversion to PDF file");
            e.printStackTrace();
        }

    }

/////////////////////////////////////////////////////////////////////////
     
    public boolean checkForPDF(File file)
    {
        String ext, str = file.getName();                   // Check if input is of 
        ext = str.substring(str.lastIndexOf('.'));
        ext = ext.toLowerCase();                                  // type PDF for different
        System.out.println(ext + "<---- EXTENSION");        // procedures of creating
        if(ext.equals(".pdf"))                              // new PDF document
            return true;
        return false;
    }   

/////////////////////////////////////////////////////////////////////////
     
    public void addPDFInputToDoc(File file)
    {
        System.out.println("Combining The two documents");
        try
        {   
            PDDocument input = PDDocument.load(file);       // Load the PDF doc        
            for(int i=0; i<input.getNumberOfPages(); ++i)   // Loop for each page in DOC
                mainDoc.importPage(input.getPage(i));       // Add each page to main PDF doc
      
            input.close();                                  // close (no longer in use)
        }
        catch(Exception e)
        {
            System.out.println("Could not add pdf Docment Pages to New PDF");
        }
    } 
}