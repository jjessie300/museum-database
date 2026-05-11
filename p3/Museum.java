import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;

class Museum
{
    public static void main(String[] args)
    {
        try {
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
        } catch (SQLException e) {
            System.out.println("Error registering driver: " + e.getMessage());
            return;
        }

        String url = "jdbc:db2://winter2026-comp421.cs.mcgill.ca:50000/comp421";

        String your_userid = System.getenv("SOCSUSER");
        String your_password = System.getenv("SOCSPASSWD");

        if (your_userid == null) {
            System.err.println("Error!! do not have a user id to connect to the database!");
            return;
        }

        if (your_password == null) {
            System.err.println("Error!! do not have a password to connect to the database!");
            return;
        }

        try (
                Connection con = DriverManager.getConnection(url, your_userid, your_password);
                Scanner scanner = new Scanner(System.in)
        ) {
            boolean running = true;

            while (running)
            {
                System.out.println("Group 22 Museum Main Menu:");
                System.out.println("        1. Search for an artifact");
                System.out.println("        2. Make a reservation for a tour");
                System.out.println("        3. Relocate an artifact");
                System.out.println("        4. View tour statistics");
                System.out.println("        5. Add a new exhibition");
                System.out.println("        6. Quit");

                System.out.print("Select an option: ");
                String option = scanner.nextLine();

                try {
                    switch (option)
                    {
                        case "1":
                            option1(con, scanner);
                            break;
                        case "2":
                            option2(con, scanner);
                            break;
                        case "3":
                            option3(con, scanner);
                            break;
                        case "4":
                            option4(con, scanner);
                            break;
                        case "5":
                            option5(con, scanner);
                            break;
                        case "6":
                            System.out.println("Exiting the program.");
                            running = false;
                            break;
                        default:
                            System.out.println("Invalid option. Please try again.");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.out.println("Code: " + e.getErrorCode() + "  sqlState: " + e.getSQLState());
            System.out.println(e);
        }
    }

    public static void option1(Connection con, Scanner scanner)
    {
        System.out.println("    a) Search by Artist");
        System.out.println("    b) Search by Genre");
        System.out.println("    c) Search by Time Period");

        System.out.print("Select a search option by typing a, b, or c: ");
        String subOption = scanner.nextLine();

        switch (subOption)
        {
            case "a": {
                System.out.print("You selected to search by artist. Insert the name of the artist: ");
                String artistName = scanner.nextLine();

                String querySQL =
                        "SELECT a.artifact_id, a.name AS artifact_name, ar.name AS artist_name " +
                                "FROM Artists ar " +
                                "JOIN Creates c ON ar.artist_id = c.artist_id " +
                                "JOIN Artifacts a ON c.artifact_id = a.artifact_id " +
                                "WHERE ar.name LIKE ?";

                try (PreparedStatement pstmt = con.prepareStatement(querySQL)) {
                    pstmt.setString(1, "%" + artistName + "%");

                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next())
                        {
                            int id = rs.getInt("artifact_id");
                            String artifactName = rs.getString("artifact_name");
                            String artistNameResult = rs.getString("artist_name");

                            System.out.println(id + " | " + artifactName + " | " + artistNameResult);
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Error searching by artist: " + e.getMessage());
                    return;
                }

                break;
            }

            case "b": {
                System.out.print("You selected to search by genre. Insert the genre: ");
                String genre = scanner.nextLine();

                String querySQL_genres =
                        "SELECT a.artifact_id, a.name AS artifact_name, g.genre_name " +
                                "FROM Artifacts a " +
                                "JOIN Artifact_Genre ag ON a.artifact_id = ag.artifact_id " +
                                "JOIN Genres g ON ag.genre_name = g.genre_name " +
                                "WHERE g.genre_name LIKE ?";

                try (PreparedStatement pstmt_genres = con.prepareStatement(querySQL_genres)) {
                    pstmt_genres.setString(1, "%" + genre + "%");

                    try (ResultSet rs_genres = pstmt_genres.executeQuery()) {
                        while (rs_genres.next())
                        {
                            int id = rs_genres.getInt("artifact_id");
                            String artifactName = rs_genres.getString("artifact_name");
                            String genreName = rs_genres.getString("genre_name");

                            System.out.println(id + " | " + artifactName + " | " + genreName);
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Error searching by genre: " + e.getMessage());
                    return;
                }

                break;
            }

            case "c": {
                System.out.print("You selected to search by time period. ");
                System.out.println("Insert the start year for this time period:");
                String startYear = scanner.nextLine();
                System.out.println("Insert the end year for this time period:");
                String endYear = scanner.nextLine();

                String querySQL_time =
                        "SELECT a.artifact_id, a.name AS artifact_name, a.date_of_creation " +
                                "FROM Artifacts a " +
                                "WHERE a.date_of_creation BETWEEN ? AND ?";

                try (PreparedStatement pstmt_timePeriod = con.prepareStatement(querySQL_time)) {
                    pstmt_timePeriod.setString(1, startYear);
                    pstmt_timePeriod.setString(2, endYear);

                    try (ResultSet rs_timePeriod = pstmt_timePeriod.executeQuery()) {
                        while (rs_timePeriod.next())
                        {
                            int id = rs_timePeriod.getInt("artifact_id");
                            String artifactName = rs_timePeriod.getString("artifact_name");
                            String dateOfCreation = rs_timePeriod.getString("date_of_creation");

                            System.out.println(id + " | " + artifactName + " | " + dateOfCreation);
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Error searching by time period: " + e.getMessage());
                    return;
                }

                break;
            }

            default: {
                System.out.println("Invalid option. Please try again. The option must be a, b, or c.");
            }
        }
    }

    public static void option2(Connection con, Scanner scanner)
    {
        System.out.println("You have selected to make a reservation.");
        System.out.println("These are the available tours:\n");

        String query_tours =
                "SELECT t.tour_id, t.description, t.cost, td.date, td.start_time, td.end_time, e.name AS exhibition_name " +
                        "FROM Tours t " +
                        "JOIN TourDates td ON t.tour_id = td.tour_id " +
                        "JOIN Tour_Exhibitions te ON t.tour_id = te.tour_id " +
                        "JOIN Exhibitions e ON te.exhibition_id = e.exhibition_id " +
                        "WHERE td.date >= CURRENT DATE " +
                        "ORDER BY td.date, td.start_time";

        ArrayList<Integer> tourIds = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<String> startTimes = new ArrayList<>();

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query_tours)) {

            int option_num = 1;

            while (rs.next()) {
                int tour_id = rs.getInt("tour_id");
                String description = rs.getString("description");
                double cost = rs.getDouble("cost");
                String date = rs.getString("date");
                String start_time = rs.getString("start_time");
                String end_time = rs.getString("end_time");
                String exhibition_name = rs.getString("exhibition_name");

                tourIds.add(tour_id);
                dates.add(date);
                startTimes.add(start_time);

                System.out.println(
                        option_num + " | " +
                                exhibition_name + " | " +
                                date + " | " +
                                start_time + "-" + end_time + " | $" +
                                cost
                );

                option_num++;
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving available tours: " + e.getMessage());
            return;
        }

        if (tourIds.size() == 0) {
            System.out.println("There are no available tours");
            return;
        }

        System.out.println("In order to complete your reservation, we need some information.");
        System.out.println("Select a tour by typing the option number.");

        int choice;
        try {
            String choice_input = scanner.nextLine().trim();
            choice = Integer.parseInt(choice_input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice. Please enter a number.");
            return;
        }

        if (choice <= 0 || choice > tourIds.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        int selectedTourId = tourIds.get(choice - 1);
        String selectedDate = dates.get(choice - 1);
        String selectedStartTime = startTimes.get(choice - 1);

        System.out.println("Insert your email address: ");
        String email_input = scanner.nextLine();

        String insertSQL =
                "INSERT INTO Reservation(email, booking_date, tour_id, date, start_time) " +
                        "VALUES (?, CURRENT DATE, ?, ?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(insertSQL)) {
            pstmt.setString(1, email_input);
            pstmt.setInt(2, selectedTourId);
            pstmt.setString(3, selectedDate);
            pstmt.setString(4, selectedStartTime);

            pstmt.executeUpdate();
            System.out.println("Reservation created successfully!");

        } catch (SQLException e) {
            System.out.println("Error creating reservation: " + e.getMessage());
            return;
        }
    }

    // some edit function
    public static void option3(Connection con, Scanner scanner)
    {
        //Logic: Display artifacts and their location. Ask for artifact id to relocate.

        System.out.println("You have selected to relocate an artifact.");

        System.out.println("In order to complete the relocation, we need some information. ");
        System.out.println("Insert the artifact ID: ");
        int artifact_id_input = Integer.parseInt(scanner.nextLine());
        System.out.println("Insert the exhibition ID that the artifact belongs to: ");
        int exhibition_id_input = Integer.parseInt(scanner.nextLine());
        System.out.println("Insert the new room: ");
        String room_input = scanner.nextLine();

        String updateSQL = "UPDATE Displays SET room = ?, exhibition_id = ? WHERE artifact_id = ?";

        try(PreparedStatement pstmt = con.prepareStatement(updateSQL)){

            pstmt.setString(1, room_input);
            pstmt.setInt(2, exhibition_id_input);
            pstmt.setInt(3, artifact_id_input);

            int rows = pstmt.executeUpdate();

            if (rows == 0) {
                String insertSQL = "INSERT INTO Displays (artifact_id, exhibition_id, room) VALUES (?, ?, ?)";

                try (PreparedStatement insertStmt = con.prepareStatement(insertSQL)) {
                    insertStmt.setInt(1, artifact_id_input);
                    insertStmt.setInt(2, exhibition_id_input);
                    insertStmt.setString(3, room_input);

                    insertStmt.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Error inserting artifact: " + e.getMessage());
                    return; // terminate function (no throwing of error)
                }


                System.out.println("Artifact placed successfully!");
            } else {
                System.out.println("Artifact relocated successfully!");
            }

        }catch(SQLException e){
            System.out.println("Error relocating artifact: " + e.getMessage());
            return; // terminate function (no throwing of error)
        }

    }

    public static void option4(Connection con, Scanner scanner)
    {
        // Logic: Collect total number of active tours, total number of passed tours, collect most attended
        String query_tours =
                "SELECT t.tour_id, Count(r.email) as number_of_reservations, Count(r.email)*t.cost as generated_revenue " +
                        "FROM Tours t " +
                        "LEFT JOIN Reservation r ON t.tour_id = r.tour_id " +
                        "GROUP BY t.tour_id, t.cost " +
                        "ORDER BY generated_revenue DESC, number_of_reservations ASC";

        try(Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query_tours);){
            System.out.printf("%-8s | %-25s | %-20s%n", "Tour ID", "Number of reservations", "Total revenue");
            System.out.println("------------------------------------------------------------");
            while(rs.next()){
                int tour_id = rs.getInt("tour_id");
                int number_of_reservations = rs.getInt("number_of_reservations");
                double generated_revenue = rs.getDouble("generated_revenue");

                System.out.printf("%-8d | %-25d | %-20.2f%n", tour_id, number_of_reservations, generated_revenue);
            };

        }catch(SQLException e){
            System.out.println("Error displaying tour statistics: " + e.getMessage());
            return;
        }


    }

    public static void option5(Connection con, Scanner scanner)
    {
        // Logic: Collect data for new exhibition. Set genre(s)

        System.out.println("In order to add an exhibition to the museum, we need some information. ");
        System.out.println("Insert the exhibitions name: ");
        String name_input = scanner.nextLine();
        System.out.println("Insert the exhibitions description: ");
        String description_input = scanner.nextLine();
        System.out.println("Insert the start date of the exhibition (YYYY-MM-DD): ");
        String start_date_input = scanner.nextLine();
        System.out.println("Insert the end date of the exhibition (YYYY-MM-DD): ");
        String end_date_input = scanner.nextLine();

        String insertSQL = // id set to autoincrement
                "INSERT INTO Exhibitions(name, description, start_date, end_date) " +
                        "VALUES (?, ?, ?, ?)";

        int id = -1;

        try(PreparedStatement pstmt = con.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)){

            pstmt.setString(1, name_input);
            pstmt.setString(2, description_input);
            pstmt.setString(3, start_date_input);
            pstmt.setString(4, end_date_input);

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            } catch(SQLException e){
                System.out.println("Error retrieving generated keys: " + e.getMessage());
                return; // terminate function (no throwing of error)
            }

            if (id == -1) {
                System.out.println("Failed to create exhibition.");
                return;
            }

        }catch(SQLException e){
            System.out.println("Error creating exhibition: " + e.getMessage());
            return; // terminate function (no throwing of error)
        }

        System.out.println("Exhibition created successfully!");

        System.out.println("In order to complete the exhibition creation, please add all genres associated with the exhibition.");
        System.out.println("For your convenience, this is the list of all genres :");
        String query_genres =
                "SELECT GENRE_NAME " +
                        "FROM Genres " +
                        "ORDER BY GENRE_NAME";


        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query_genres)) {

            while (rs.next()) {
                String name = rs.getString("GENRE_NAME");
                System.out.println(name);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving genres: " + e.getMessage());
            return;
        }

        //While loop ensures a proper genre is entered to satisfy participation constraint
        boolean genre_entered = false;
        String genre_choice;

        while (true){
            if (!genre_entered) System.out.println("Insert a genre name: ");
            else System.out.println("Insert a genre name or 'DONE': ");;
            genre_choice = scanner.nextLine(); // you can assume the user will provide valid information when prompted

            if (genre_choice.equals("DONE")){ // no more genres left to add
                break;
            }

            String insertGenreSQL = // id set to autoincrement
                    "INSERT INTO Exhibition_Genre(exhibition_id, genre_name) " +
                            "VALUES (?, ?)";

            try(PreparedStatement pstmt = con.prepareStatement(insertGenreSQL)){
                pstmt.setInt(1, id);
                pstmt.setString(2, genre_choice);
                pstmt.executeUpdate();
                System.out.println("Genre added successfully!");
                genre_entered = true;

            }catch(SQLException e){
                System.out.println("Error creating exhibition: " + e.getMessage());
                // we do not return as we need to add a genre
            }
        }
        System.out.println("Exhibition created successfully!");

    }
}
