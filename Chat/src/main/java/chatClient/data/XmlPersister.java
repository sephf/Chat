package chatClient.data;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class XmlPersister {
    private String path;
    private static XmlPersister theInstance;
    public static XmlPersister instance(String id){
        if (theInstance==null){
            theInstance=new XmlPersister(id+".xml");
        }
        return theInstance;
    }

    public XmlPersister(String p) {
        path=p;
    }

    public Data load() throws Exception{
        JAXBContext jaxbContext = JAXBContext.newInstance(Data.class);
        FileInputStream is = new FileInputStream(path);
        //is = new FileInputStream(this.getClass().getResource(path).getFile());
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Data result = (Data) unmarshaller.unmarshal(is);
        is.close();
        return result;
    }

    public void store(Data d)throws Exception{
        JAXBContext jaxbContext = JAXBContext.newInstance(Data.class);
        FileOutputStream os = new FileOutputStream(path);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(d, os);
        os.flush();
        os.close();
    }
}