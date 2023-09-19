import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class VehicleInfo {
    private String vehicleId;
    private String xmlFilePath;
    private double x;
    private double y;

    public VehicleInfo(String vehicleId,String xmlFilePath) {
        this.vehicleId = vehicleId;
        this.xmlFilePath = xmlFilePath;
        this.x = 0.0; 
        this.y = 0.0;

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new UpdateCoordinatesTask(), 0, 5000);//Delay 0 para começar a atualizar e atualiza de 5 em 5 segundos(5000 ms)
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    private void updateCoordinatesFromXML() {

        try {
            File inputFile = new File(this.xmlFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList timestepList = doc.getElementsByTagName("timestep");

            //Itera na lista de timestep começando pelo final
            for (int i = timestepList.getLength() - 1; i >= 0; i--) {
                Node timestepNode = timestepList.item(i);
                if (timestepNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element timestepElement = (Element) timestepNode;
                    NodeList vehicleList = timestepElement.getElementsByTagName("vehicle");
                    for (int j = 0; j < vehicleList.getLength(); j++) {
                        Node vehicleNode = vehicleList.item(j);
                        if (vehicleNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element vehicleElement = (Element) vehicleNode;
                            String vehicleId = vehicleElement.getAttribute("id");
                            //Se for o ID procurado atualiza as informações
                            if (vehicleId.equals(this.vehicleId)) {
                                this.x = Double.parseDouble(vehicleElement.getAttribute("x"));
                                this.y = Double.parseDouble(vehicleElement.getAttribute("y"));
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class UpdateCoordinatesTask extends TimerTask {
        @Override
        public void run() {
            updateCoordinatesFromXML();
        }
    }

    public static void main(String[] args) {
        VehicleInfo vehicleInfo = new VehicleInfo("f_0.0","osmWithStop.xml"); // VehicleID/pathXML
    }
}