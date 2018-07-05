package se.skl.tp.vp.wsdl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import se.skl.tp.vp.constants.ApplicationProperties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class WsdlConfigurationJson implements WsdlConfiguration {

    private List<WsdlConfig> wsdlConfigs;
    private HashMap<String, WsdlConfig> mapOnTjanstekontrakt;
    private HashMap<String, WsdlConfig> mapOnWsdlUrl;

    public WsdlConfigurationJson(Environment env) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        wsdlConfigs = objectMapper.readValue(new File(env.getProperty(ApplicationProperties.WSDL_JSON_FILE)), new TypeReference<List<WsdlConfig>>(){});

        createConfigurationFromWsdlFiles(env.getProperty(ApplicationProperties.WSDLFILES_DIRECTORY));
        initMaps();
    }

    private void initMaps() {
        mapOnTjanstekontrakt = new HashMap<>();
        mapOnWsdlUrl = new HashMap<>();
        for (WsdlConfig wsdlConfig : wsdlConfigs) {
            mapOnTjanstekontrakt.put(wsdlConfig.getTjanstekontrakt(), wsdlConfig);
            mapOnWsdlUrl.put(wsdlConfig.getWsdlurl(), wsdlConfig);
        }
    }

    private void createConfigurationFromWsdlFiles(String wsdlDirectory) {
        try (Stream<Path> paths = Files.walk(Paths.get(wsdlDirectory))) {
            paths.filter(Files::isRegularFile).forEach(file -> createConfigFromWsdlFile(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createConfigFromWsdlFile(Path file) {
        WsdlInfo wsdlInfo = getWsdlInfoFromFile(file);
        String serviceInteractionNameSpace = wsdlInfo.getServiceInteractionNameSpace();

        if(serviceInteractionNameSpace != null) {
            String[] serviceNameSpaceArray = serviceInteractionNameSpace.split(":");
            String maindomain = serviceNameSpaceArray[2];

            int serviceNameSpaceSize=serviceNameSpaceArray.length-1;
            String rivtaVersion = serviceNameSpaceArray[serviceNameSpaceSize];
            String serviceVersion = serviceNameSpaceArray[serviceNameSpaceSize-1];
            String serviceName = serviceNameSpaceArray[serviceNameSpaceSize-2];
            String subdomain=serviceNameSpaceArray[3];

            int i= serviceNameSpaceSize-3;
            for (int y = 4; i >= y; y++) {
                subdomain = subdomain + ":" + serviceNameSpaceArray[y];
            }

            String subdomainAdress = subdomain.replaceAll(":", "/");
            String serviceRelativePath = serviceName + "/" + serviceVersion + "/" + rivtaVersion;

            String wsdlurl = "vp/" + maindomain +"/" + subdomainAdress + "/" + serviceRelativePath;
            String wsdlPath = file.toString();
            String tjanstekontrakt = wsdlInfo.getServiceContractName();

            if(wsdlConfigs != null && !wsdlConfigs.contains(tjanstekontrakt)) {
                WsdlConfig wsdlConfig = new WsdlConfig();
                wsdlConfig.setWsdlurl(wsdlurl);
                wsdlConfig.setWsdlfilepath(wsdlPath);
                wsdlConfig.setTjanstekontrakt(tjanstekontrakt);
                wsdlConfigs.add(wsdlConfig);
            }
        }
    }

    private WsdlInfo getWsdlInfoFromFile(Path file) {
        WsdlInfo wsdlInfo = new WsdlInfo();
        try {
            InputStream is = new FileInputStream(file.toFile());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(is);
            Node node = document.getDocumentElement();

            NamedNodeMap attributes = node.getAttributes();
            for(int i = 0; i < attributes.getLength(); i++) {
                String attribute = attributes.item(i).getNodeValue();
                if(attribute.toLowerCase().contains("rivtabp")) {
                    wsdlInfo.setServiceInteractionNameSpace(attribute);
                } else if(attribute.toLowerCase().contains("responder")) {
                    wsdlInfo.setServiceContractName(attribute);
                }
                System.out.println();
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return wsdlInfo;
    }

    @Override
    public WsdlConfig getOnWsdlUrl(String wsdlUrl) {
        return mapOnWsdlUrl.get(wsdlUrl);
    }

    @Override
    public WsdlConfig getOnTjanstekontrakt(String tjanstekontrakt) {
        return mapOnTjanstekontrakt.get(tjanstekontrakt);
    }

    @Override
    public List<String> getAllWsdlUrl() {
        return wsdlConfigs.stream().map(WsdlConfig::getWsdlurl).collect(Collectors.toList());
    }

    private class WsdlInfo {
        String serviceInteractionNameSpace;
        String ServiceContractName;

        public String getServiceInteractionNameSpace() {
            return serviceInteractionNameSpace;
        }

        public void setServiceInteractionNameSpace(String serviceInteractionNameSpace) {
            this.serviceInteractionNameSpace = serviceInteractionNameSpace;
        }

        public String getServiceContractName() {
            return ServiceContractName;
        }

        public void setServiceContractName(String serviceContractName) {
            ServiceContractName = serviceContractName;
        }
    }
}
