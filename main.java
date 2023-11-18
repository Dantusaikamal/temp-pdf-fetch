import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PdfDataInserter {

    public static void main(String[] args) {
        String pdfFilePath = "path/to/your/pdf.pdf"; // Path to your PDF file
        String jdbcUrl = "jdbc:mysql://localhost:3306/your_database"; // Your database URL
        String username = "your_username"; // Your database username
        String password = "your_password"; // Your database password

        try {
            String extractedText = extractTextFromPDF(pdfFilePath);
            insertDataIntoDatabase(jdbcUrl, username, password, extractedText);

            List<String> databaseData = fetchDataFromDatabase(jdbcUrl, username, password);
            System.out.println("Data fetched from database: " + databaseData);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static String extractTextFromPDF(String pdfFilePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private static void insertDataIntoDatabase(String jdbcUrl, String username, String password, String data)
            throws SQLException {
        String sql = "INSERT INTO your_table (column_name) VALUES (?)"; // Your SQL insert statement

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, data); // Set extracted text to the prepared statement
            pstmt.executeUpdate();
        }
    }

    private static List<String> fetchDataFromDatabase(String jdbcUrl, String username, String password)
            throws SQLException {
        String sql = "SELECT column_name FROM your_table"; // SQL query to select data
        List<String> dataList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String data = rs.getString("column_name"); // Assuming the data is of type String
                dataList.add(data);
            }
        }

        return dataList;
    }
}
