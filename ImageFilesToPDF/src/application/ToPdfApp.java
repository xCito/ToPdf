package application;

// Outside files:
//      1) Background image
//      2) NoFileSelectedFile image
//      *use path of this project, to find outside imagefiles*

// BUGS
//      1) Input validation for bad file names
//          *consider save file dialog IDK*

// Add more features to the application
//     x 1) Combine 2 or more PDF documents
//     x 2) Combine PDF with other image file types
//     x 3) Allow user to select new PDF file destination 
//     x 4) Display PDF pages in Right-side preview
//       5) Speed up process of displaying PDF page preview

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundSize; 
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.io.File;    

public class ToPdfApp extends Application
{
    List<File> files;
    ListView<ImageView> list;
    TextArea selected;
    VBox vbox;
    final int WINDOW_LEN = 483;
    final int WINDOW_WID = 678;
    final int IMG_LEN = 310;
    final int IMG_WID = 232;
    
    public void start(Stage stage)
    {
        BorderPane border = new BorderPane();
        Group root = new Group();
        Scene scene = new Scene(root);
        
        BackgroundImage myBI= new BackgroundImage(                                          // Place image
            new Image("CitoCustomBackground.png",(WINDOW_WID-18),(WINDOW_LEN-38),false,true),                       // as background
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);
        border.setBackground(new Background(myBI));
        
        
        
        vbox = new VBox();
        createList();
        GridPane grid = createGrid();
        
        Label lab = createLabel("Selected Files..");
        selected = createTextArea();
        Button chooseBtn = createChooseButton();  
        Button clearBtn = createClearButton();
        Button convertBtn = createConvertButton();
      
        grid.add(chooseBtn,0,0);            // Places CHOOSE button top left   on GRID
        grid.add(clearBtn,2,0);             // Places CLEAR  button top right  on GRID
        grid.add(lab,0,1);                  // Places LABEL  button mid left   on GRID
        grid.add(selected,0,2,3,3);         // Places TEXT-A button bot center on GRID

        border.setCenter(grid);                             // Places Grid on Center section
        border.setRight(vbox);                              // Places vbox to Right section
        border.setBottom(convertBtn);                       // Places convertB to Bottom section
        BorderPane.setAlignment(convertBtn, Pos.CENTER);
        BorderPane.setMargin(convertBtn, new Insets(0,0,40,0));
        root.getChildren().add(border);

        stage.setTitle("Images ---> PDF");
        stage.initStyle(StageStyle.UTILITY);
        stage.setScene(scene);
        stage.setMinHeight(WINDOW_LEN);
        stage.setMaxHeight(WINDOW_LEN);
        stage.setMinWidth(WINDOW_WID);
        stage.setMaxWidth(WINDOW_WID);
        stage.setOpacity(0.8);
        stage.show();
    }

    private Button createChooseButton()
    {
        Button b = new Button("Choose files");
        b.setMaxWidth(200);
        b.setMaxHeight(200);
        b.setFont(new Font("Yu Gothic Regular", 20));//Gisha
        b.setOnAction((event) -> 
            {
                System.out.println("Openning File Explorer");
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().addAll(
                        new ExtensionFilter("ImageFiles",
                                            "*.png", 
                                            "*.jpg", 
                                            "*.gif",
                                            "*.pdf"));
                
                List<File>selectedFiles = fc.showOpenMultipleDialog(new Stage());   // OPEN file chooser
                	
                if(selectedFiles==null){											
                    System.out.println("File chooser cancelled"); 					
                    return;
                } 
                                                         
                if(isFileListBlank())                              // If no files yet selected
                    files = new ArrayList<File>(selectedFiles);         // create new array of files
                else
                    files.addAll(new ArrayList<File>(selectedFiles));   // append to current array of files
                
                updateListView(files);                                  
                
                String text = "";
                for(int i=0; i<files.size(); ++i)
                    text += files.get(i).getName() + "\n";              // display file(s) name
           
                selected.setText(text);
            });
        return b;
    }
    
    private Button createClearButton() 
    {
        Button b = new Button("Clear");
        b.setMaxWidth(200);
        b.setMaxHeight(200);
        b.setFont(new Font("Yu Gothic Regular", 20));
        b.setOnAction((event) -> 
            {
                System.out.println("Clearing...");
                selected.clear();
                
                if(files !=null)
                {
                    System.out.println("removing selected Files...");
                    for(File x: files)
                        System.out.println(" -"+ x);
                    files.clear();
                }   
                
                if(list != null)
                {
                    Image pic = new Image("NoFilesSelected.jpg");       // Set as default image
                    ImageView iv = new ImageView(pic);                  // For listview
                    iv.setFitHeight(IMG_LEN);
                    iv.setFitWidth(IMG_WID);
                    list.setItems(FXCollections.observableArrayList(iv));
                }                 
                                    
            });
        return b;
    }
    
    private Button createConvertButton()
    {
        Button b = new Button("Convert!");
        b.setMaxWidth(500);
        b.setMaxHeight(500);
        b.setFont(new Font("Yu Gothic Regular", 20));
        b.setOnAction((event) -> 
            {
                if(files == null || files.isEmpty()) 
                {
                    Alert a = new Alert(AlertType.WARNING);
                    a.setTitle("Warning Dialog");
                    a.setHeaderText("Nothing to Convert!");
                    a.setContentText("No Files were selected to convert to PDF!");
                    a.showAndWait();    
                    return; // Do nothing if user has not selected any files...
                }
            
                FileChooser sf = new FileChooser();
                sf.getExtensionFilters().addAll(
                        new ExtensionFilter("PDF doc",
                                            "*.pdf"));                           
                sf.setInitialFileName("File_name.pdf");
                File input = sf.showSaveDialog(new Stage());
                if(input == null)
                    return; // Do nothing if user cancels  
                 
                // Conversion and Clearing 
                System.out.println("User enterered: "+input.getName()+"\n"+input.getParent());
                ImagesToPDFConverter conv = new ImagesToPDFConverter(files ,input.getName());
                conv.setDirectory(input.getParent()+"\\");
                conv.createPDF();
                selected.clear();
                files.clear();
                                    
                System.out.println("convert button pressed");
                
                // Clears ListView (PDF preview) 
                if(list != null)
                {
                    Image pic = new Image("NoFilesSelected.jpg");
                    ImageView iv = new ImageView(pic);
                    iv.setFitHeight(IMG_LEN);
                    iv.setFitWidth(IMG_WID);
                    list.setItems(FXCollections.observableArrayList(iv));
                }
                
                // Message to inform conversion was successful
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("Conversion Completed!");
                alert.setContentText(null);
                alert.showAndWait();
             
            });
        return b;
    }
    
    private TextArea createTextArea()
    {
        TextArea t = new TextArea();
        t.setPrefColumnCount(40);
        t.setPrefRowCount(15);
        t.setEditable(false);
        
        return t;
    }
    
    private Label createLabel(String str)
    {
        Label lab = new Label(str);
        lab.setFont(new Font("Yu Gothic Regular", 20));
        lab.setTextFill(Color.WHITE);
        
        return lab;
    }
    
    private void createList()
    {
        Image pic = new Image("NoFilesSelected.jpg");
        ImageView iv = new ImageView(pic);
        iv.setFitHeight(IMG_LEN);
        iv.setFitWidth(IMG_WID);
        
        list = new ListView<ImageView>();
        ObservableList<ImageView> pictures = FXCollections.observableArrayList(iv);
        list.setItems(pictures);
        list.setOrientation(Orientation.HORIZONTAL);
        vbox.setPadding(new Insets(20,30,10,0));
    
        list.setPrefHeight(350);
        //list.setFocusTraversable(false);
        vbox.getChildren().addAll(list);

    }
        
    private void updateListView(List<File> pics)
    {        
        ArrayList<ImageView> iv = new ArrayList<ImageView>();
        ArrayList<Image> img    = new ArrayList<Image>();
        
        try{
            for(int i=0; i<pics.size(); i++)
            {
                String str = pics.get(i).getName();
                String ext = str.substring(str.lastIndexOf('.'));
                ext = ext.toLowerCase();
                if(!ext.equals(".pdf"))
                {
                    iv.add(new ImageView());    
                    URL url = pics.get(i).toURI().toURL();
                    img.add(new Image(url.toExternalForm()));
                    iv.get(iv.size()-1).setImage(img.get(img.size()-1));
                    iv.get(iv.size()-1).setFitHeight(IMG_LEN);
                    iv.get(iv.size()-1).setFitWidth(IMG_WID);
                } 
                else
                {
                    PdfToImages convert=new PdfToImages(pics.get(i));
                    int pages = convert.getNumberOfPages();
                    convert.extractImages();
                    
                    for(int j=0; j<pages; ++j)
                    {
                        iv.add(new ImageView());
                        img.add(convert.getImagesFromPDF().get(j));
                        iv.get(iv.size()-1).setImage(img.get(img.size()-1));
                        iv.get(iv.size()-1).setFitHeight(IMG_LEN);
                        iv.get(iv.size()-1).setFitWidth(IMG_WID);
                    }
                }
            }
        }catch(Exception e)
        {   
            System.out.println("Problem obtaining the images");
            e.printStackTrace();
        }
        
        
        ObservableList<ImageView> pictures = FXCollections.observableArrayList(iv);
        
        if(isFileListBlank())
            list.getItems().addAll(pictures);   // Append to what is currently in ListView
        else                                    //      OR
            list.setItems(pictures);            // Overwrite "NoFileSelected" image

        // Print files selected, DEBUG purposes
        for(int i=0; i<list.getItems().size(); ++i)
            System.out.println(list.getItems().get(i));        
    }
    
    private boolean isFileListBlank()
    {
        if(files == null)
            return true;
        else if(files.isEmpty())
            return true;
        else
            return false;
    }
    
    private GridPane createGrid()
    {
        GridPane g = new GridPane(); 
        ColumnConstraints col1 = new ColumnConstraints(150,150,Double.MAX_VALUE);
        ColumnConstraints col2 = new ColumnConstraints(25,25,Double.MAX_VALUE);
        ColumnConstraints col3 = new ColumnConstraints(150,150,Double.MAX_VALUE);
               
        RowConstraints row1 = new RowConstraints(35,35,Double.MAX_VALUE);
        RowConstraints row2 = new RowConstraints(20,25,Double.MAX_VALUE);
        RowConstraints row3 = new RowConstraints(100,100,Double.MAX_VALUE);
        RowConstraints row4 = new RowConstraints(100,125,Double.MAX_VALUE);

        g.getColumnConstraints().addAll(col1,col2,col3); // first column gets any extra width
        g.getRowConstraints().addAll(row1,row2,row3,row4);
        g.setHgap(7);
        g.setVgap(7);
        g.setPadding(new Insets(20,20,20,25));
        //g.setGridLinesVisible(true);
        return g;
    } 
    
    public static void main(String [] args)
    {
        launch();
        System.out.println("Application closing");
    }
}