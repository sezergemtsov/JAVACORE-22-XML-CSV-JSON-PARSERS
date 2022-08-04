import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.MappingStrategy;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "src/main/resources/data.csv";

        //Задание 1 csv to JSON (создает файл "src/main/resources/data.json")
        List<Employee> list = parseCSV(columnMapping, fileName);
        listToJSON(list, "src/main/resources/data.json");


        //Задание 2 XML to JSON file (создает файл "src/main/resources/dataXML.json")
        List<Employee> listXML = new ArrayList<>();
        parseXML("src/main/resources/data.xml", listXML, "src/main/resources/dataXML.json");

        //Задание 3 JSON to file (читает созданный в задании 2 файл JSON)
        List<Employee> listJSON = readString("src/main/resources/data.json", Employee.class);
        listJSON.forEach(System.out::println);
    }


    public static <T> T parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<T> csv = new CsvToBeanBuilder<T>(reader)
                    .withMappingStrategy((MappingStrategy<? extends T>) strategy)
                    .build();
            List<T> staff = csv.parse();
            return (T) staff;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void listToJSON(List<?> list, String filePath) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<?>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileOutputStream ous = new FileOutputStream(file)) {
            ous.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T readString(String filePath, Type type) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(filePath));
            JSONArray jsObj = (JSONArray) obj;
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.setPrettyPrinting().create();
            List<T> list = new ArrayList<>();
            for (Object newObj : jsObj) {
                T employee = gson.fromJson(String.valueOf(newObj), type);
                list.add(employee);
            }
            return (T) list;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void parseXML(String pathXML, List<Employee> list, String filePath) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            Document doc = builder.parse(new File(pathXML));
            Node root = doc.getDocumentElement();
            NodeList nl = root.getChildNodes();
            root(nl, list);
            listToJSON(list, filePath);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void root(NodeList nl, List<Employee> list) {
        String[] employee = new String[5];
        for (int cnt = 0; cnt < nl.getLength(); cnt++) {

            if (nl.item(cnt).getNodeType() == 1) {
                if (nl.item(cnt).getChildNodes().getLength() < 2) {
                    String atr = nl.item(cnt).getNodeName();
                    String value = nl.item(cnt).getChildNodes().item(0).getNodeValue();
                    switch (atr) {
                        case "id":
                            employee[0] = value;
                            break;
                        case "firstName":
                            employee[1] = value;
                            break;
                        case "lastName":
                            employee[2] = value;
                            break;
                        case "country":
                            employee[3] = value;
                            break;
                        case "age":
                            employee[4] = value;
                    }
                } else {
                    root(nl.item(cnt).getChildNodes(), list);
                }
            }
        }
        if (employee[4] != null) {
            list.add(new Employee(employee[0], employee[1], employee[2], employee[3], Integer.parseInt(employee[0])));
        }
    }

}
