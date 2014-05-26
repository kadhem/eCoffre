package edu.esprit.eCoffreWeb.converter;

import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

import edu.esprit.eCoffreEJB.Entities.ObN;

public class ObnDataModel extends ListDataModel<ObN> implements SelectableDataModel<ObN> {  

	List<ObN> obNs;
	

    public ObnDataModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ObnDataModel(List<ObN> list) {
		super(list);
		obNs = list;
		System.out.println("from obndatamodel");
		// TODO Auto-generated constructor stub
	}

    @Override
    public ObN getRowData(String rowKey) {
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data
        System.out.println(rowKey);
        
        for(ObN o : obNs) {
        	
            if (String.valueOf(o.getIdU()).equals(rowKey))
                return o;
        }
        return null;
    }

    @Override
    public Object getRowKey(ObN obN) {
        return obN.getLibelle();
    }
}