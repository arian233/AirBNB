/**
 * @author arian Ansari 
 * AirbnB application
 */
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Sql_main {
	private static Connection con;
	private static String space;

    public static void main(String[] args) throws Exception
    {

        getConnection();
        selectMenu();
        
    }
    public static void selectMenu()throws Exception{
        Scanner myObj = new Scanner (System.in);
        boolean flag = true;
        String answer;
        while (flag)
        {
            System.out.println("Please select from one of the options below by entering their number: ");
            System.out.println("1. search for an available place");
            System.out.println("2. Write a review for your booking");
            System.out.println("3. Exit the program");
            answer = myObj.nextLine();
            if (answer.equalsIgnoreCase("1")){
                search();
                flag= false;
            }
            else if(answer.equalsIgnoreCase("2"))
            {
                review(); 
                flag = false;
            }
            else if(answer.equalsIgnoreCase("3")){
                System.out.println("Goodbye!");
                return;
            }
            else
                System.out.println("This option doesn't exist!");
            
        
        }
        
    
    }
    
    public static void search() throws Exception
    {
        PreparedStatement pstmt = null;
        Scanner input = new Scanner (System.in);
        float min_price =0;
        float max_price = Float.MAX_VALUE;
        long num_of_rooms = 0;
        Date start_date;
        start_date = Date.valueOf("1970-01-01");
        Date end_date;
        end_date = Date.valueOf("2019-12-29");
        int counter = 1;
        //System.out.println("please specifie the following criteria, if nit sure press enter key");
        System.out.println("Do you have a minimum price in mind? answer 'y' for yes otherwise any other letter");
        if(input.next().equalsIgnoreCase("y")){
            System.out.print("What is the minimum price you are looking for: ");
            try
            {
                    min_price = input.nextFloat();
            //System.out.println("min is: " + min_price);
            }catch(InputMismatchException e){
                System.out.println(e.toString());
                return;
            }
        }
        System.out.println("Do you have a maximum price in mind? answer 'y' for yes otherwise any other letter");
        if(input.next().equalsIgnoreCase("y")){
            System.out.print("What is the maximum price you are looking for: ");
            try
            {
                max_price = input.nextFloat();
                while (max_price < min_price ){
                    System.out.println("The maximum price has to be greater than minimum price");
                    System.out.print("What is the maximum price you are looking for: ");
                    max_price = input.nextFloat();
                }
            }catch(InputMismatchException e){
            System.out.println(e.toString());
            return;
            }
        }
        
        System.out.println("How many rooms do you need during your stay, Please answer in numerical format: ");
            try
            {
                num_of_rooms = input.nextInt();
            }catch(InputMismatchException e){
            System.out.println("The format was wrong please visit us again!");
            return;
            }
        System.out.println("Please enter your start date, use 'yyyy-mm-dd' format:: ");
        
            String start = input.next();
            try{
                start_date = Date.valueOf(start);
            }catch(IllegalArgumentException exception){
                System.out.println("The format was wrong please visit us again!");
                return;
            }

        System.out.println("Please enter your end date, use 'yyyy-mm-dd' format: ");
            String end = input.next();
            try{
                end_date = Date.valueOf(end);
            }catch(IllegalArgumentException exception){
                System.out.println("The format was wrong please visit us again!");
                return;
            }
        Connection con = getConnection();
        pstmt = con.prepareStatement("Select Distinct listing_id, name, description, number_of_bedrooms, MAX(price) as price"
                + " from Calendar C, Listings L where C.listing_id = L.id and number_of_bedrooms=? and price >= ? and price <= ? and date >= ? and date <= ? and C.listing_id not in (select Distinct listing_id from Calendar where number_of_bedrooms= ? and price >= ? and price <= ? and date >= ? and date <= ? and available = 'false')\n" +
"Group by listing_id, name, description, number_of_bedrooms ");
        pstmt.setLong(1, num_of_rooms);
        pstmt.setDouble(2, min_price);
        pstmt.setDouble(3, max_price);
        pstmt.setDate(4, start_date);
        pstmt.setDate(5, end_date);
        pstmt.setLong(6, num_of_rooms);
        pstmt.setDouble(7, min_price);
        pstmt.setDouble(8, max_price);
        pstmt.setDate(9, start_date);
        pstmt.setDate(10, end_date);
        ResultSet result = pstmt.executeQuery();
        System.out.println("***************************************************");
        while(result.next())
        {
            String lId = result.getString("listing_id");
            String lName = result.getString("name");
            String description = result.getString("description");
            String lDesc;
 
            
            long lRooms = result.getLong("number_of_bedrooms");
            int Cprice = result.getInt("price");
            System.out.println("Listing " + counter);
            System.out.println("Listing ID: " + lId);
            try{
                lDesc = description.substring(0, 25);
                System.out.println("Description: " + lDesc);
            }catch(Exception e){
                System.out.println("Description is less than 25 characters");
            }
            System.out.println("Number of rooms: " + lRooms);
            System.out.println("The price is:" + Cprice);
            System.out.println("----------------------------------------------");
            counter++;
        }
        System.out.println("***************************************************");
        if (counter == 1)
        {
            System.out.println("Sorry there are no listings available with specifiesd criteria");
            System.out.println("Please visit us again!");
            return;
        }
        result.close();
        System.out.println("Do you want  to book a room? answer by 'y' if yes otherwise input any other character");
        String bookings = input.next();
        if(bookings.equalsIgnoreCase("y"))
            booking(start_date, end_date);
        return;

    }
    
    public static void booking(Date start_date, Date end_date) throws Exception{
        PreparedStatement pstmt = null;
        Scanner input = new Scanner (System.in);
        String name;
        int guestNumber = 0;
        int bookId = 0;
        int maxid = 0;
        
        System.out.println("Please enter the Listing ID number of the AirBnb you want to book: ");
        while(!input.hasNextLong()){
                System.out.println("The input has to be in integer format!");
                System.out.println("Please enter the ID number of the AirBnb you want to book: ");
        }
        bookId = input.nextInt();
        System.out.println("Please insert your name: ");
        name = input.next();
        System.out.println("Please insert number of the guests: ");
        if(!input.hasNextInt()){
                System.out.println("The answer has to be in integer format");
                System.out.println("Please insert number of the guests: ");
        }
        guestNumber = input.nextInt();
        try{
            Connection con = getConnection();
            Statement myStmt = con.createStatement();
            pstmt = con.prepareStatement("update Calendar set available = 'false' WHERE listing_id =?");
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
            Statement stmt  = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            
            ResultSet rs = stmt.executeQuery("select MAX(id) as maxId from bookings");
            if (rs.next()) {
                maxid = rs.getInt("maxId");
            }
            maxid++;
            pstmt = con.prepareStatement("INSERT INTO Bookings (id, listing_id, guest_name, stay_from, stay_to, number_of_guests) "
                    + "values(?,?,?,?,?,?)");
            pstmt.setLong(1, maxid);
            pstmt.setLong(2, bookId);
            pstmt.setString(3, name);
            pstmt.setDate(4, start_date);
            pstmt.setDate(5, end_date);
            pstmt.setInt(6, guestNumber);
            pstmt.executeUpdate();
            System.out.println("Booking successful!");
            System.out.println("***************************************************");
            System.out.println("***************************************************");
        }catch(Exception e){
            System.out.println(e);
        }
        selectMenu();
    }
    public static Connection getConnection() throws Exception
    {
       PreparedStatement pstmt = null;
		ResultSet rs;
		String sSQL= "select * from helpdesk";
		String temp="";
		
		String sUsername = "*******";
		String sPassword= "********";

		
        String connectionUrl = "jdbc:sqlserver://cypress;" +
			        "user = " + sUsername + ";" +
			        "password = " + sPassword;
			        
        //System.out.println("\n connectionUrl = " + connectionUrl + "\n\n");
        
		try
		{
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		}catch(ClassNotFoundException ce)
			{
				System.out.println("\n\nNo JDBC Driver for SQL Server; exit now.\n\n");
				return null;
			}
		
		try
		{
			con = DriverManager.getConnection(connectionUrl);
		}catch (SQLException se)
			{
				System.out.println("\n\nFail to connect to CSIL SQL Server; exit now.\n\n");
				return null;
			}
		
		try
		{
			pstmt = con.prepareStatement(sSQL);
			rs = pstmt.executeQuery();

		}catch (SQLException se)
			{
				System.out.println("\nSQL Exception occurred, the state : "+
								se.getSQLState()+"\nMessage:\n"+se.getMessage()+"\n");
				return null;
			}
                return con;
    }

    
    public static void review () throws Exception
    {
        PreparedStatement pstmt = null;
        Scanner input = new Scanner (System.in);
        Scanner sc = new Scanner (System.in);
        String name;
        String reviewTxt;
        ResultSet result;
        boolean flag = false;
        long id = 0;
        long lId;
        Date todayDate;
        String stayedTo="";
        String lToDate;
        long lListing = 0;
        int maxid =0;
        System.out.println("Please insert your name to see your bookings: ");
        name = input.next();
        try{ 
            Connection con = getConnection();
            Statement myStmt = con.createStatement();
            pstmt = con.prepareStatement("select id, listing_id, guest_name, stay_from, stay_to, number_of_guests from Bookings B where guest_name = ?");
            pstmt.setString(1, name);

            flag = true;
        }catch(Exception e){
            System.out.println(e);
        }
        result = pstmt.executeQuery();
            while(result.next())
           {
               id = result.getLong("id");
               lListing = result.getLong("listing_id");
               String lGuestName = result.getString("guest_name");
               String lFromDate = result.getString("stay_from");
               lToDate = result.getString("stay_to");
               String lNumGuest = result.getString("number_of_guests");
               
               System.out.println("ID: " + id);
               System.out.println("Listing ID: " + lListing);
               System.out.println("Guest name: " + lGuestName);
               System.out.println("Stay from: " + lFromDate);
               System.out.println("Stay to: " + lToDate);
               System.out.println("Number of the Guests: " + lNumGuest);
               System.out.println("----------------------------------------------");
               }
            

            System.out.println("please insert the Listing ID for which you like to write a review for: ");
            lId = input.nextLong();
            System.out.println("Please insert today's date in the yyyy-mm-dd format");
            String date = input.next();
            todayDate = Date.valueOf(date);
            

            try{ 
                Connection con = getConnection();
                Statement myStmt = con.createStatement();
                pstmt = con.prepareStatement("select stay_to from bookings where listing_id = ? and guest_name= ?");
                pstmt.setLong(1, lId);
                pstmt.setString(2, name);
                result = pstmt.executeQuery();
            }catch(Exception e){
                System.out.println(e);
            }
            result = pstmt.executeQuery();
            while(result.next())
           {
               stayedTo = result.getString("stay_to");
           }
//            pstmt = con.prepareStatement("select stay_to from bookings where listing_id = ? and guest_name= ?");
//            pstmt.setLong(1, lId);
//            pstmt.setString(2, name);
//            result = pstmt.executeQuery();
//            stayedTo = result.getString("stay_to");
//            System.out.println("stay_to is: " + stayedTo);

            
            Date sqlstayedTo = Date.valueOf(stayedTo);
            if(todayDate.compareTo(sqlstayedTo) <= 0){
                System.out.println("'Can only review the listing after the stay'");
                return;
            }
            System.out.println("Plese write your review here: ");
            reviewTxt= sc.nextLine();
            Statement stmt  = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            
            ResultSet rs = stmt.executeQuery("select MAX(id) as maxId from reviews");
            if (rs.next()) {
                maxid = rs.getInt("maxId");
            }
            maxid++;
            pstmt = con.prepareStatement("insert into reviews ( listing_id, id, comments, guest_name) values(?,?,?,?)");
            pstmt.setLong(1, lId);
            pstmt.setLong(2, maxid);
            pstmt.setString(3, reviewTxt);
            pstmt.setString(4, name);
            pstmt.execute();
            System.out.println("Review is registered!");
            System.out.println("***************************************************");
            System.out.println("***************************************************"); 
    }
}
