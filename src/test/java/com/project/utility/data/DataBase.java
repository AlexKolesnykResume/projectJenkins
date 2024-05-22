package com.project.utility.data;

import com.project.pages.CommonSteps;
import com.project.testData.TestData;
import org.junit.jupiter.api.Assertions;

import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.stream.IntStream;

public class DataBase extends CommonSteps {
    public enum DatabaseType {
        CloudStage(1), CloudStage2(2), CloudTest(3), CloudTest2(4), PROD(5), CloudDev(6), CloudDocker(7);
        public final int value;

        DatabaseType(final int value) {
            this.value = value;
        }
    }

    public static DatabaseType getDatabaseType() {
        try {
            return switch (System.getProperty("Environment").toUpperCase()) {
                case "CLOUDSTAGE" -> DatabaseType.CloudStage;
                case "CLOUDSTAGE2" -> DatabaseType.CloudStage2;
                case "CLOUDTEST" -> DatabaseType.CloudTest;
                case "CLOUDTEST2" -> DatabaseType.CloudTest2;
                case "PROD" -> DatabaseType.PROD;
                case "CLOUDDEV" -> DatabaseType.CloudDev;
                case "CLOUDDOCKER" -> DatabaseType.CloudDocker;
                default -> {
                    Assertions.fail("Given Environment is not listed in Config.properties.");
                    yield null;
                }
            };
        } catch (Exception e) {
            Assertions.fail("Failed to get database type on Database class.");
            return null;
        }
    }

    public static String getElasticSearchHostName() {
        try {
            return switch (System.getProperty("Environment").toUpperCase()) {
                case "CLOUDSTAGE" -> "opa-stg-es-http.az-stg.optumpay.com";
                case "CLOUDSTAGE2" -> "opa-stg-es-http.az-stg2.optumpay.com";
                case "CLOUDTEST" -> "opa-stg-es-http.az-test.optumpay.com";
                case "CLOUDTEST2" -> "opa-stg-es-http.az-test2.optumpay.com";
                case "PROD" -> "opa-stg-es-http.az-prod.optumpay.com";
                case "CLOUDDEV" -> "opa-stg-es-http.az-dev.optumpay.com";
                case "CLOUDDOCKER" -> "opa-stg-es-http.az-docker.optumpay.com";
                default -> {
                    Assertions.fail("Given Environment is not listed in Config.properties.");
                    yield null;
                }
            };
        } catch (Exception e) {
            Assertions.fail("Failed to get elastic host name.");
            return null;
        }
    }

    public static ResultSet executeSelectQueryToBeRemoved(String selectQuery, DatabaseType databaseType) {
        Date startDate = new Date();
        selectQuery = replaceArgumentsWithRunTimeProperties(selectQuery);
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = getConnection(databaseType).createStatement();
            resultSet = statement.executeQuery(selectQuery);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (resultSet == null) {
            System.out.println("No data was returned from this query.");
        }

        Date endDate = new Date();
        double timeDifference = (endDate.getTime() - startDate.getTime()) / 1000.00;
        System.out.println("It took [" + timeDifference + "] seconds to run this query");
        return resultSet;
    }

    private static Connection getConnection(DatabaseType databaseType) {
        Connection connection = null;
        String connectionUrl = null;
        String userName = null;
        String password = null;
        try {
            switch (databaseType) {
                case CloudStage -> {
                    connectionUrl = TestData.StageDBConnectionString;
                    userName = TestData.StageDBUserName;
                    password = TestData.StageDBPassword;
                }
                case CloudStage2 -> {
                    connectionUrl = TestData.Stage2DBConnectionString;
                    userName = TestData.Stage2DBUserName;
                    password = TestData.Stage2DBPassword;
                }
                case CloudTest -> {
                    connectionUrl = TestData.TestDBConnectionString;
                    userName = TestData.TestDBUserName;
                    password = TestData.TestDBPassword;
                }
                case CloudTest2 -> {
                    connectionUrl = TestData.Test2DBConnectionString;
                    userName = TestData.Test2DBUserName;
                    password = TestData.Test2DBPassword;
                }
            }
            if (connectionUrl == null) {
                Assertions.fail("connectionUrl is null");
            }
            connection = DriverManager.getConnection(connectionUrl, userName, password);
            if (connection != null) {
                System.out.println("Connection succeeded.");
            } else {
                System.err.println("Unable to establish connection.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        dbConnection = connection;
        return dbConnection;
    }

    public static List<Map<String, String>> select(String selectQuery) {
        selectQuery = replaceArgumentsWithRunTimeProperties(selectQuery);
        System.out.println("selectQuery: " + selectQuery);
        Statement statement = null;
        ResultSet resultSet;
        String exception = null;
        List<Map<String, String>> response = null;
        try {
            if (getDatabaseType() == null) {
                Assertions.fail("getDatabaseType() returns null");
            }
            statement = getConnection(getDatabaseType()).createStatement();
            resultSet = statement.executeQuery(selectQuery);
            response = convertResultSetToListOfMaps(resultSet);
        } catch (SQLException e) {
            exception = e.getMessage();
            System.out.println("select() method throws Exception: " + exception);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                    System.out.println("Statement closed.");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        closeConnection();
        if (response == null) {
            Assertions.fail("select() method throws Exception: " + exception);
        }
        return response;
    }

    /*
    IMPORTANT: This method will fetch a select query only first record, it will add " fetch first row only"
    if no fetch first statement is present in the query.
    It will also add all the columns into runtime properties
    TODO: remove runtime properties behaviour and understand where it is needed and why
     */

    public static Map<String, String> selectFirst(String selectQuery) {
        if (!selectQuery.toLowerCase().contains("fetch first")) {
            selectQuery += " fetch first row only";
        }
        Map<String, String> firstRow = select(selectQuery).get(0);
        Set<String> keys = firstRow.keySet();
        for (String key : keys) {
            putRunTimeProperty(key, firstRow.get(key));
        }
        return firstRow;
    }

    /**
     * Execute update query in DB
     * @param updateQuery The update query
     * @return Then number of row affected
     */

    public static int update(String updateQuery) {
        Date startDate = new Date();
        Statement statement = null;
        int rows = 0;
        try {
            if (getDatabaseType() == null) {
                Assertions.fail("getDatabaseType() returns null");
            }
            statement = getConnection(getDatabaseType()).createStatement();
            updateQuery = replaceArgumentsWithRunTimeProperties(updateQuery);
            if (getRunTimeProperty("replaceNULLInQuery") != null && getRunTimeProperty("replaceNULLInQuery").equalsIgnoreCase("true")) {
                if (updateQuery.contains("'(null)'") || updateQuery.contains("'(NULL)'") || updateQuery.contains("'null'") || updateQuery.contains("'NULL'")) {
                    updateQuery = updateQuery.replace("'(null)'", "NULL").replace("'(NULL)'", "NULL").replace("'null'", "NULL").replace("'NULL'", "NULL");
                }
            }
            System.out.println("Executing the update query: " + updateQuery);
            rows = statement.executeUpdate(updateQuery);
        } catch (SQLException e) {
            System.out.println("update() method throws Exception: " + e.getMessage());
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                    System.out.println("Statement closed.");
                } catch (SQLException e) {
                    System.out.println("update() method throws Exception: " + e.getMessage());
                }
            }
        }
        if (rows == 0) {
            System.out.println("No rows were updated by this query.");
        } else {
            System.out.println("[" + rows + "] rows were updated by this query.");
        }
        closeConnection();
        Date endDate = new Date();
        double timeDifference = (endDate.getTime() - startDate.getTime()) / 1000.00;
        System.out.println("It took [" + timeDifference + "] seconds to run this query");
        return rows;
    }

    public static void delete(List<String> deleteQueries) {
        Statement statement = null;
        boolean status = true;
        String exceptionMessage = null;
        try {
            if (getDatabaseType() == null) {
                Assertions.fail("getDatabaseType() returns null");
            }
            statement = getConnection(getDatabaseType()).createStatement();
            for (String deleteQuery : deleteQueries) {
                deleteQuery = replaceArgumentsWithRunTimeProperties(deleteQuery);
                System.out.println("Adding delete query [" + deleteQuery + "] to batch, to be deleted.");
                statement.addBatch(deleteQuery);
            }
            int[] results = statement.executeBatch();
            System.out.println("Deleted rows: [" + IntStream.of(results).sum() + "]");
        } catch (SQLException e) {
            exceptionMessage = e.getMessage();
            System.out.println("batch delete() method throws Exception: " + exceptionMessage);
            status = false;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                    System.out.println("Statement closed.");
                } catch (SQLException e) {
                    System.out.println("batch delete() method throws Exception: " + e.getMessage());
                }
            }
        }
        closeConnection();
        if (!status) {
            Assertions.fail("Unable to create Connection with database. Exception : " + exceptionMessage);
        }
    }

    public static void insert(List<String> insertQueries) {
        Statement statement = null;
        boolean status = true;
        String exceptionMessage = null;
        try {
            if (getDatabaseType() == null) {
                Assertions.fail("getDatabaseType() returns null");
            }
            statement = getConnection(getDatabaseType()).createStatement();
            for (String insertQuery : insertQueries) {
                insertQuery = replaceArgumentsWithRunTimeProperties(insertQuery);
                System.out.println("Adding insert query [" + insertQuery + "] to batch, to be inserted.");
                statement.addBatch(insertQuery);
            }
            int[] results = statement.executeBatch();
            System.out.println("Inserted rows: [" + IntStream.of(results).sum() + "]");
        } catch (SQLException e) {
            exceptionMessage = e.getMessage();
            System.out.println("batch insert() method throws Exception: " + exceptionMessage);
            status = false;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                    System.out.println("Statement closed.");
                } catch (SQLException e) {
                    System.out.println("batch delete() method throws Exception: " + e.getMessage());
                }
            }
        }
        closeConnection();
        if (!status) {
            Assertions.fail("Unable to create Connection with database. Exception : " + exceptionMessage);
        }
    }

    public static List<Map<String, String>> convertResultSetToListOfMaps(ResultSet resultSet) throws SQLException {
        // Convert the ResultSet into a HashMap
        List<Map<String, String>> listOfMaps = new ArrayList<>();
        while (resultSet.next()) {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            Map<String, String> columnMapData = new HashMap<>();
            for (int column = 1; column <= resultSetMetaData.getColumnCount(); column++) {
                try {
                    columnMapData.put(resultSetMetaData.getColumnLabel(column), resultSet.getObject(column).toString());
                } catch (NullPointerException e) {
                    columnMapData.put(resultSetMetaData.getColumnLabel(column), "NULL");
                }
            }
            listOfMaps.add(columnMapData);
        }
        return listOfMaps;
    }

    public static boolean testDBConnection() {
        boolean status = false;
        try {
            if (getDatabaseType() == null) {
                Assertions.fail("getDatabaseType() returns null");
            }
            if (getConnection(getDatabaseType()).isValid(3)) {
                status = true;
            }
        } catch (SQLException e) {
            System.out.println("testDBConnection() method throws Exception: " + e.getMessage());
            closeConnection();
        }
        return status;
    }

    // TODO: Use static connection through a test scenario and close it only once, avoid opening in each query
    public static void closeConnection() {
        if (dbConnection != null) {
            try {
                dbConnection.close();
                dbConnection = null;
                System.out.println("Database connection closed successfully.");
            } catch (SQLException e) {
                Assertions.fail("Database connection was not closed. Exception: " + e.getMessage());
            }
        }
    }
}
