package servlet;
// Written by David Gonzalez, April 2020
// Modified by Jeff Offutt
// Built to deploy in github with Heroku

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "FilePersistence", urlPatterns = {"/file"})
public class persistenceFile extends HttpServlet{
  static enum Data {AGE, NAME, HAIRCOLOR};
  static String RESOURCE_FILE = "entries.txt";
  static final String VALUE_SEPARATOR = ";";

  static String Domain  = "";
  static String Path    = "/";
  static String Servlet = "file";

  // Button labels
  static String OperationAdd = "Operate";

  // Other strings.

  /** *****************************************************
   *  Overrides HttpServlet's doPost().
   *  Converts the values in the form, performs the operation
   *  indicated by the submit button, and sends the results
   *  back to the client.
  ********************************************************* */
  @Override
  public void doPost (HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException
  {
     String name = request.getParameter(Data.NAME.name());
     String age = request.getParameter(Data.AGE.name());
     String hairColor = request.getParameter(Data.HAIRCOLOR.name());

     String error = "";
     if(name == null){
       error= "<li>Clause 1 is required</li>";
       name = "";
     }

     if(age == null){
       error+= "<li>Operator is required.<li>";
       age = "";
     }else{
          try{
            //Integer ageInteger =new Integer(age);
            if(!age.equals("OR") && !age.equals("AND")){
                error+= "<li>Please select AND or OR operators.</li>";
                age = "";
            }//else{
              //if(ageInteger>150){
                  //error+= "<li>Age must be an integer less than 150.</li>";
                  //age = "";
              //}
           // }
          }catch (Exception e) {
            error+= "<li>Please select AND or OR operators.</li>";
            age = "";
          }
     }

     if(hairColor == null){
        error= "<li>Clause 2 is required</li>";
        hairColor = "";
     }

     response.setContentType("text/html");
     PrintWriter out = response.getWriter();

     if (error.length() == 0){
       PrintWriter entriesPrintWriter = new PrintWriter(new FileWriter(RESOURCE_FILE, true), true);
       entriesPrintWriter.println(name+VALUE_SEPARATOR+age+VALUE_SEPARATOR+hairColor);
       entriesPrintWriter.close();

       PrintHead(out);
       PrintResponseBody(out, RESOURCE_FILE);
       PrintTail(out);
     }else{
       PrintHead(out);
       PrintBody(out, name, age, hairColor, error);
       PrintTail(out);
     }
  }

  /** *****************************************************
   *  Overrides HttpServlet's doGet().
   *  Prints an HTML page with a blank form.
  ********************************************************* */
  @Override
  public void doGet (HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException{
     response.setContentType("text/html");
     PrintWriter out = response.getWriter();
     PrintHead(out);
     PrintBody(out, "", "", "", "");
     PrintTail(out);
  }

  /** *****************************************************
   *  Prints the <head> of the HTML page, no <body>.
  ********************************************************* */
  private void PrintHead (PrintWriter out){
     out.println("<html>");
     out.println("");
     out.println("<head>");
     out.println("<title>File Persistence Example</title>");
     // Put the focus in the name field
     out.println ("<script>");
     out.println ("  function setFocus(){");
     out.println ("    document.persist2file.NAME.focus();");
     out.println ("  }");
     out.println ("</script>");
     out.println("</head>");
     out.println("");
  }

  /** *****************************************************
   *  Prints the <BODY> of the HTML page
  ********************************************************* */
  private void PrintBody (PrintWriter out, String name, String age, String hairColor, String error){
     out.println("<body onLoad=\"setFocus()\">");
     out.println("<p>");
     out.println("A simple example that demonstrates how to persist data to a file");
     out.println("</p>");

     if(error != null && error.length() > 0){
       out.println("<p style=\"color:red;\">Please correct the following and resubmit.</p>");
       out.println("<ol>");
       out.println(error);
       out.println("</ol>");
     }

     out.print  ("<form name=\"persist2file\" method=\"post\"");
     out.println(" action=\""+Domain+Path+Servlet+"\">");
     out.println("");
     out.println(" <table>");
     out.println("  <tr>");
     out.println("   <td>Clause 1:</td>");
     out.println("   <td><input type=\"text\" name=\""+Data.NAME.name()
      +"\" value=\""+name+"\" size=30 required></td>");
     out.println("  </tr>");
     out.println("  <tr>");
     out.println("   <td>Operator:</td>");
     out.println("   <td><input type=\"text\"  name=\""+Data.AGE.name()
     +"\" value=\""+age+"\" size=30 required></td>"); 
     // +"\" oninput=\"this.value=this.value.replace(/[^0-9]/g,'');\" value=\""
     // +age+"\" size=3 required></td>");
     out.println("  </tr>");
     out.println("  <tr>");
     out.println("   <td>Clause 2:</td>");
     out.println("   <td><input type=\"text\"  name=\""+Data.HAIRCOLOR.name()
      +"\" value=\""+hairColor+"\" size=30 required></td>");
     out.println("  </tr>");
     out.println(" </table>");
     out.println(" <br>");
     out.println(" <br>");
     out.println(" <input type=\"submit\" value=\"" + OperationAdd
      + "\" name=\"Operation\">");
     out.println(" <input type=\"reset\" value=\"Reset\" name=\"reset\">");
     out.println("</form>");
     out.println("");
     out.println("</body>");
  }

  /** *****************************************************
   *  Prints the <BODY> of the HTML page
  ********************************************************* */
  private void PrintResponseBody (PrintWriter out, String resourcePath){
    out.println("<body onLoad=\"setFocus()\">");
    out.println("<p>");
    out.println("A simple example that demonstrates how to persist data to a file");
    out.println("</p>");
    out.println("");
    out.println(" <table>");

    try {
        out.println("  <tr>");
        out.println("   <th>Clause 1</th>");
        out.println("   <th>Operator</th>");
        out.println("   <th>Clause 2</th>");
        out.println("  </tr>");
        File file = new File(resourcePath);
        if(!file.exists()){
          out.println("  <tr>");
          out.println("   <td>No entries persisted yet.</td>");
          out.println("  </tr>");
          return;
        }

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
          String []  entry= line.split(VALUE_SEPARATOR);
          out.println("  <tr>");
          for(String value: entry){
              out.println("   <td>"+value+"</td>");
          }
          out.println("  </tr>");
        }
        bufferedReader.close();
      } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
     out.println(" </table>");
     out.println("");
     out.println("</body>");
  }

  /** *****************************************************
   *  Prints the bottom of the HTML page.
  ********************************************************* */
  private void PrintTail (PrintWriter out){
     out.println("");
     out.println("</html>");
  }
}
